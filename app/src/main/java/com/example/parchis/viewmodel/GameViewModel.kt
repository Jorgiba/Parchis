package com.example.parchis.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.parchis.R
import com.example.parchis.database.UsuarioDao
import com.example.parchis.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    // Color del turno actual para el fondo del dado
    val currentPlayerColorRes: LiveData<Int> = _currentPlayer.map { jugador ->
        when (jugador?.color) {
            ColorParchis.ROJO -> R.color.red
            ColorParchis.AZUL -> R.color.blue
            ColorParchis.VERDE -> R.color.green
            ColorParchis.AMARILLO -> R.color.yellow
            else -> R.color.blue
        }
    }

    private var game: ParchisGame? = null

    fun initGame(jugadores: List<Jugador>) {
        game = ParchisGame(jugadores)
        _currentPlayer.value = game?.obtenerJugadorActual()
        verificarTurnoBot()
    }

    fun getJugadores() = game?.jugadores ?: emptyList()

    fun lanzarDado() {
        if (_diceResult.value != null || _gameFinished.value != null) return

        val gameInstance = game ?: return
        val result = gameInstance.lanzarDado()
        _diceResult.value = result
        
        val jugadorActual = gameInstance.obtenerJugadorActual()
        val puedeMoverAlguna = jugadorActual.fichas.any { gameInstance.puedeMoverFicha(it, result) }
        
        if (!puedeMoverAlguna) {
            viewModelScope.launch {
                delay(1500)
                finalizarTurno()
            }
        } else if (jugadorActual is Bot) {
            viewModelScope.launch {
                delay(2000)
                ejecutarMovimientoBot(jugadorActual, result)
            }
        }
    }

    private fun ejecutarMovimientoBot(bot: Bot, dado: Int) {
        val gameInstance = game ?: return
        val fichaAElegir = BotStrategy.decidirMovimiento(bot, dado, gameInstance)
        
        fichaAElegir?.let { ficha ->
            gameInstance.moverFicha(ficha, dado)
            _fichasUpdateEvent.value = Unit
            
            viewModelScope.launch {
                delay(1500)
                if (gameInstance.obtenerJugadorActual().fichas.all { it.estado == EstadoFicha.FINALIZADA }) {
                    gestionarFinPartida(bot)
                } else if (gameInstance.movimientosExtra > 0) {
                    val extra = gameInstance.movimientosExtra
                    gameInstance.movimientosExtra = 0
                    if (gameInstance.puedeMoverFicha(ficha, extra)) {
                        _diceResult.value = extra
                        delay(1000)
                        ejecutarMovimientoBot(bot, extra)
                    } else {
                        finalizarTurno()
                    }
                } else {
                    finalizarTurno()
                }
            }
        } ?: run {
            viewModelScope.launch { delay(1000); finalizarTurno() }
        }
    }

    private fun verificarTurnoBot() {
        val jugadorActual = game?.obtenerJugadorActual()
        if (jugadorActual is Bot && _gameFinished.value == null) {
            viewModelScope.launch { delay(2000); lanzarDado() }
        }
    }

    fun onFichaClicked(ficha: Ficha) {
        val dado = _diceResult.value ?: return
        val gameInstance = game ?: return
        val jugadorActual = gameInstance.obtenerJugadorActual()

        if (jugadorActual is Bot) return

        if (jugadorActual.fichas.contains(ficha) && gameInstance.puedeMoverFicha(ficha, dado)) {
            gameInstance.moverFicha(ficha, dado)
            _fichasUpdateEvent.value = Unit
            
            if (gameInstance.obtenerJugadorActual().fichas.all { it.estado == EstadoFicha.FINALIZADA }) {
                gestionarFinPartida(jugadorActual)
            } else if (gameInstance.movimientosExtra > 0) {
                val extra = gameInstance.movimientosExtra
                gameInstance.movimientosExtra = 0
                if (gameInstance.puedeMoverFicha(ficha, extra)) {
                    _diceResult.value = extra
                } else {
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
        val jugadores = game?.jugadores?.map { it.nombre } ?: emptyList()

        viewModelScope.launch {
            val resultado = if (ganador.nombre == usuario.username) ResultadoPartida.VICTORIA else ResultadoPartida.DERROTA
            val nuevaPartida = Partida(
                id = UUID.randomUUID().toString(),
                fecha = Date(),
                resultado = resultado,
                jugadores = jugadores,
                usernameUsuario = usuario.username
            )
            
            // Usamos NonCancellable para asegurar que si el usuario sale rápido del fragmento, se guarde igual
            withContext(Dispatchers.IO + NonCancellable) {
                usuario.agregarPartidaAlHistorial(nuevaPartida)
                usuarioDao.actualizarUsuario(usuario)
                usuarioDao.insertarPartida(nuevaPartida)
                Log.d("ParchisGame", "💾 Partida finalizada guardada en historial")
            }
        }
    }

    fun abandonarPartida() {
        val usuario = SesionUsuario.usuarioLogueado ?: return
        val jugadores = game?.jugadores?.map { it.nombre } ?: emptyList()

        viewModelScope.launch {
            val nuevaPartida = Partida(
                id = UUID.randomUUID().toString(),
                fecha = Date(),
                resultado = ResultadoPartida.ABANDONADA,
                jugadores = jugadores,
                usernameUsuario = usuario.username
            )
            
            withContext(Dispatchers.IO + NonCancellable) {
                usuario.agregarPartidaAlHistorial(nuevaPartida)
                usuarioDao.actualizarUsuario(usuario)
                usuarioDao.insertarPartida(nuevaPartida)
                Log.d("ParchisGame", "💾 Abandono registrado en historial")
            }
        }
    }

    private fun finalizarTurno() {
        game?.siguienteTurno()
        _diceResult.value = null 
        _currentPlayer.value = game?.obtenerJugadorActual()
        verificarTurnoBot()
    }
}
