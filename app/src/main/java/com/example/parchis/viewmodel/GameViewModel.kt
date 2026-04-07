package com.example.parchis.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parchis.model.ParchisGame
import com.example.parchis.model.Jugador
import com.example.parchis.model.Ficha
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _diceResult = MutableLiveData<Int?>()
    val diceResult: LiveData<Int?> = _diceResult

    private val _currentPlayer = MutableLiveData<Jugador>()
    val currentPlayer: LiveData<Jugador> = _currentPlayer

    private val _fichasUpdateEvent = MutableLiveData<Unit>()
    val fichasUpdateEvent: LiveData<Unit> = _fichasUpdateEvent

    private var game: ParchisGame? = null

    fun initGame(jugadores: List<Jugador>) {
        Log.d("ParchisGame", "Iniciando juego con ${jugadores.size} jugadores")
        game = ParchisGame(jugadores)
        _currentPlayer.value = game?.obtenerJugadorActual()
    }

    fun getJugadores() = game?.jugadores ?: emptyList()

    fun lanzarDado() {
        if (_diceResult.value != null) {
            Log.d("ParchisGame", "Dado ya lanzado: ${_diceResult.value}. Esperando movimiento.")
            return
        }

        val gameInstance = game ?: run {
            Log.e("ParchisGame", "Error: El juego no ha sido inicializado")
            return
        }

        val result = gameInstance.lanzarDado()
        _diceResult.value = result
        Log.d("ParchisGame", "Dado lanzado: $result para el jugador ${gameInstance.obtenerJugadorActual().nombre}")
        
        val jugadorActual = gameInstance.obtenerJugadorActual()
        val puedeMoverAlguna = jugadorActual.fichas.any { gameInstance.puedeMoverFicha(it, result) }
        
        if (!puedeMoverAlguna) {
            Log.d("ParchisGame", "El jugador no puede mover ninguna ficha. Pasando turno en 1s...")
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
            
            Log.d("ParchisGame", "Moviendo ficha ${ficha.id} de color ${ficha.color}")
            gameInstance.moverFicha(ficha, dado)
            _fichasUpdateEvent.value = Unit
            
            finalizarTurno()
        } else {
            Log.d("ParchisGame", "Movimiento no válido para esta ficha")
        }
    }

    private fun finalizarTurno() {
        game?.siguienteTurno()
        _diceResult.value = null 
        _currentPlayer.value = game?.obtenerJugadorActual()
        Log.d("ParchisGame", "Turno finalizado. Nuevo turno para: ${game?.obtenerJugadorActual()?.nombre}")
    }
}