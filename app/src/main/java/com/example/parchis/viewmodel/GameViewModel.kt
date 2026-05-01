package com.example.parchis.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.parchis.database.UsuarioDao
import com.example.parchis.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class GameViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _diceResult = MutableLiveData<Int?>(null)
    val diceResult: LiveData<Int?> = _diceResult

    private val _currentPlayer = MutableLiveData<Jugador>()
    val currentPlayer: LiveData<Jugador> = _currentPlayer

    private val _fichasUpdateEvent = MutableLiveData<Unit>()
    val fichasUpdateEvent: LiveData<Unit> = _fichasUpdateEvent

    private val _gameFinished = MutableLiveData<Jugador?>()
    val gameFinished: LiveData<Jugador?> = _gameFinished

    // Propiedades para DataBinding
    val diceValue: LiveData<String> = _diceResult.map { it?.toString() ?: "?" }
    
    val turnText: LiveData<String> = _currentPlayer.map { jugador ->
        "TURNO DE:\n${jugador?.nombre?.uppercase() ?: ""}"
    }

    private var game: ParchisGame? = null

    fun initGame(jugadores: List<Jugador>) {
        Log.d("ParchisGame", "--------------------------------------")
        Log.d("ParchisGame", "🎮 INICIANDO NUEVA PARTIDA")
        game = ParchisGame(jugadores)
        val inicial = game?.obtenerJugadorActual()
        Log.d("ParchisGame", "👤 Primer turno para: ${inicial?.nombre} (${inicial?.color})")
        _currentPlayer.value = inicial
        verificarTurnoBot()
    }

    fun getJugadores() = game?.jugadores ?: emptyList()

    fun lanzarDado() {
        if (_diceResult.value != null || _gameFinished.value != null) return

        val gameInstance = game ?: return
        val result = gameInstance.lanzarDado()
        _diceResult.value = result
        
        val jugadorActual = gameInstance.obtenerJugadorActual()
        Log.d("ParchisGame", "🎲 ${jugadorActual.nombre} ha sacado un $result")
        
        val puedeMoverAlguna = jugadorActual.fichas.any { gameInstance.puedeMoverFicha(it, result) }
        
        if (!puedeMoverAlguna) {
            Log.d("ParchisGame", "🚫 ${jugadorActual.nombre} no tiene movimientos posibles.")
            viewModelScope.launch {
                delay(1500) // Pausa para que el humano vea el "bloqueo"
                finalizarTurno()
            }
        } else if (jugadorActual is Bot) {
            viewModelScope.launch {
                delay(2000) // Tiempo de "reflexión" del bot tras ver el dado
                ejecutarMovimientoBot(jugadorActual, result)
            }
        }
    }

    private fun ejecutarMovimientoBot(bot: Bot, dado: Int) {
        val gameInstance = game ?: return
        val fichaAElegir = BotStrategy.decidirMovimiento(bot, dado, gameInstance)
        
        fichaAElegir?.let { ficha ->
            Log.d("ParchisGame", "🤖 El Bot elige mover la ficha ID: ${ficha.id}")
            
            // Realizamos el movimiento manualmente para controlar el tiempo del turno
            val posAnterior = if (ficha.estado == EstadoFicha.EN_CASA) "CASA" else ficha.posicion.toString()
            gameInstance.moverFicha(ficha, dado)
            val posNueva = if (ficha.estado == EstadoFicha.FINALIZADA) "META" else ficha.posicion.toString()
            Log.d("ParchisGame", "🏃 Bot mueve ficha ${ficha.id}: $posAnterior -> $posNueva")
            
            _fichasUpdateEvent.value = Unit
            
            viewModelScope.launch {
                delay(1500) // Pausa para que el humano vea dónde ha quedado la ficha del bot
                
                if (gameInstance.obtenerJugadorActual().fichas.all { it.estado == EstadoFicha.FINALIZADA }) {
                    Log.d("ParchisGame", "🏆 ¡PARTIDA FINALIZADA! Ganador: ${bot.nombre}")
                    gestionarFinPartida(bot)
                } else if (gameInstance.movimientosExtra > 0) {
                    val extra = gameInstance.movimientosExtra
                    gameInstance.movimientosExtra = 0

                    if (gameInstance.puedeMoverFicha(ficha, extra)) {
                        Log.d("ParchisGame", "🤖🎁 BONUS: El bot debe mover $extra casillas.")
                        _diceResult.value = extra
                        delay(1000)
                        // El bot se llama a sí mismo para usar el 20 inmediatamente
                        ejecutarMovimientoBot(bot, extra)
                    } else {
                        finalizarTurno()
                    }
                } else {
                    finalizarTurno()
                }
            }
        } ?: run {
            Log.d("ParchisGame", "⚠️ El Bot no pudo elegir ficha")
            viewModelScope.launch {
                delay(1000)
                finalizarTurno()
            }
        }
    }

    private fun verificarTurnoBot() {
        val jugadorActual = game?.obtenerJugadorActual()
        if (jugadorActual is Bot && _gameFinished.value == null) {
            viewModelScope.launch {
                delay(2000) // Pausa al empezar el turno del bot
                lanzarDado()
            }
        }
    }

    fun onFichaClicked(ficha: Ficha) {
        val dado = _diceResult.value ?: return
        val gameInstance = game ?: return
        val jugadorActual = gameInstance.obtenerJugadorActual()

        // Solo permitir clicks manuales si es turno de humano
        if (jugadorActual is Bot) return

        if (jugadorActual.fichas.contains(ficha) && 
            gameInstance.puedeMoverFicha(ficha, dado)) {
            
            val posAnterior = if (ficha.estado == EstadoFicha.EN_CASA) "CASA" else ficha.posicion.toString()
            gameInstance.moverFicha(ficha, dado)
            Log.d("ParchisGame", "🏃 ${jugadorActual.nombre} mueve ficha ${ficha.id}: $posAnterior -> ${ficha.posicion}")
            
            _fichasUpdateEvent.value = Unit
            
            if (gameInstance.obtenerJugadorActual().fichas.all { it.estado == EstadoFicha.FINALIZADA }) {
                Log.d("ParchisGame", "🏆 ¡PARTIDA FINALIZADA! Ganador: ${jugadorActual.nombre}")
                gestionarFinPartida(jugadorActual)
            } else if (gameInstance.movimientosExtra > 0) {
                // Si ha comido, tiene movimientos extra
                val extra = gameInstance.movimientosExtra
                gameInstance.movimientosExtra = 0 // Consumimos el bonus

                if (gameInstance.puedeMoverFicha(ficha, extra)) {
                    Log.d("ParchisGame", "🎁 BONUS: ${jugadorActual.nombre} debe mover $extra casillas.")
                    _diceResult.value = extra // Cambiamos el dado en pantalla al número del bonus
                    // IMPORTANTE: No llamamos a finalizarTurno(). El jugador debe hacer otro clic.
                } else {
                    Log.d("ParchisGame", "🚫 Tiene bonus de $extra pero ninguna ficha puede moverse.")
                    finalizarTurno()
                }
            } else {
                finalizarTurno()
            }
        }
    }

    private fun gestionarFinPartida(ganador: Jugador) {
        _gameFinished.value = ganador
        val usuario = SesionUsuario.usuarioLogueado ?: return
        
        viewModelScope.launch {
            val resultado = if (ganador.nombre == usuario.username) ResultadoPartida.VICTORIA else ResultadoPartida.DERROTA
            val nuevaPartida = Partida(
                id = UUID.randomUUID().toString(),
                fecha = Date(),
                resultado = resultado,
                jugadores = game?.jugadores?.map { it.nombre } ?: emptyList(),
                usernameUsuario = usuario.username
            )
            usuarioDao.insertarPartida(nuevaPartida)
            Log.d("ParchisGame", "💾 Partida guardada")
        }
    }

    private fun finalizarTurno() {
        game?.siguienteTurno()
        _diceResult.value = null 
        val nuevoJugador = game?.obtenerJugadorActual()
        _currentPlayer.value = nuevoJugador
        Log.d("ParchisGame", "🔄 Cambio de turno -> Ahora: ${nuevoJugador?.nombre}")
        verificarTurnoBot()
    }
}
