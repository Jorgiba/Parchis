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
        Log.d("Lifecycle", "GameViewModel: initGame")
        game = ParchisGame(jugadores)
        _currentPlayer.value = game?.obtenerJugadorActual()
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
                delay(1000)
                finalizarTurno()
            }
        }
    }

    fun onFichaClicked(ficha: Ficha) {
        val dado = _diceResult.value ?: return
        val gameInstance = game ?: return

        if (gameInstance.obtenerJugadorActual().fichas.contains(ficha) && 
            gameInstance.puedeMoverFicha(ficha, dado)) {
            
            gameInstance.moverFicha(ficha, dado)
            _fichasUpdateEvent.value = Unit
            
            // Verificar si el jugador ha ganado
            if (gameInstance.obtenerJugadorActual().fichas.all { it.estado == EstadoFicha.FINALIZADA }) {
                gestionarFinPartida(gameInstance.obtenerJugadorActual())
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
            
            // Guardar partida y actualizar usuario en DB
            usuarioDao.insertarPartida(nuevaPartida)
            
            val usuarioDb = usuarioDao.obtenerUsuario(usuario.username)
            usuarioDb?.let {
                it.agregarPartidaAlHistorial(nuevaPartida)
                usuarioDao.actualizarUsuario(it)
                SesionUsuario.usuarioLogueado = it
            }
        }
    }

    private fun finalizarTurno() {
        game?.siguienteTurno()
        _diceResult.value = null 
        _currentPlayer.value = game?.obtenerJugadorActual()
    }
}
