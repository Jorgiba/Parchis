package com.example.parchis.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.parchis.model.ParchisGame
import com.example.parchis.model.Jugador
import com.example.parchis.model.Ficha
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _diceResult = MutableLiveData<Int?>(null)
    val diceResult: LiveData<Int?> = _diceResult

    private val _currentPlayer = MutableLiveData<Jugador>()
    val currentPlayer: LiveData<Jugador> = _currentPlayer

    private val _fichasUpdateEvent = MutableLiveData<Unit>()
    val fichasUpdateEvent: LiveData<Unit> = _fichasUpdateEvent

    // Propiedades para DataBinding (Concepto 15)
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
        if (_diceResult.value != null) return

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
            
            finalizarTurno()
        }
    }

    private fun finalizarTurno() {
        game?.siguienteTurno()
        _diceResult.value = null 
        _currentPlayer.value = game?.obtenerJugadorActual()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Lifecycle", "GameViewModel: onCleared")
    }
}