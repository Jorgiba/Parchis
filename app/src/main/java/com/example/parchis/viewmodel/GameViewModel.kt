package com.example.parchis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parchis.model.ParchisGame
import com.example.parchis.model.Jugador
import com.example.parchis.model.Ficha

class GameViewModel : ViewModel() {

    private val _diceResult = MutableLiveData<Int?>()
    val diceResult: LiveData<Int?> = _diceResult

    private val _currentPlayer = MutableLiveData<Jugador>()
    val currentPlayer: LiveData<Jugador> = _currentPlayer

    private val _fichasUpdateEvent = MutableLiveData<Unit>()
    val fichasUpdateEvent: LiveData<Unit> = _fichasUpdateEvent

    private var game: ParchisGame? = null

    fun initGame(jugadores: List<Jugador>) {
        game = ParchisGame(jugadores)
        _currentPlayer.value = game?.obtenerJugadorActual()
    }

    fun getJugadores() = game?.jugadores ?: emptyList()

    fun lanzarDado() {
        if (_diceResult.value != null) return // Ya se lanzó el dado y se espera movimiento

        val gameInstance = game ?: return
        val result = gameInstance.lanzarDado()
        _diceResult.value = result
        
        val jugadorActual = gameInstance.obtenerJugadorActual()
        val puedeMoverAlguna = jugadorActual.fichas.any { gameInstance.puedeMoverFicha(it, result) }
        
        if (!puedeMoverAlguna) {
            finalizarTurno()
        }
    }

    fun onFichaClicked(ficha: Ficha) {
        val dado = _diceResult.value ?: return
        val gameInstance = game ?: return

        if (gameInstance.obtenerJugadorActual().fichas.contains(ficha) && 
            gameInstance.puedeMoverFicha(ficha, dado)) {
            
            gameInstance.moverFicha(ficha, dado)
            _fichasUpdateEvent.value = Unit // Notificar a la vista que las fichas se movieron
            
            finalizarTurno()
        }
    }

    private fun finalizarTurno() {
        game?.siguienteTurno()
        _diceResult.value = null // Resetear dado para el siguiente turno
        _currentPlayer.value = game?.obtenerJugadorActual()
    }
}