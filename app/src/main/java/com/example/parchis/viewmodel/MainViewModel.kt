package com.example.parchis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parchis.model.ParchisGame
import com.example.parchis.model.Jugador
import com.example.parchis.model.Ficha

class MainViewModel : ViewModel() {

    // 1. Estado de la Navegación (para cambiar de "pantalla")
    private val _navigationEvent = MutableLiveData<NavigationState>(NavigationState.Home)
    val navigationEvent: LiveData<NavigationState> = _navigationEvent

    // 2. Estado del Juego
    private val _game = MutableLiveData<ParchisGame>()
    val game: LiveData<ParchisGame> = _game

    private val _diceResult = MutableLiveData<Int>(0)
    val diceResult: LiveData<Int> = _diceResult

    private val _currentPlayer = MutableLiveData<Jugador>()
    val currentPlayer: LiveData<Jugador> = _currentPlayer

    // Acciones de la Vista
    fun onStartGameClicked() {
        _navigationEvent.value = NavigationState.GameSettings
    }

    fun iniciarPartida(jugadores: List<Jugador>) {
        val nuevaPartida = ParchisGame(jugadores)
        _game.value = nuevaPartida
        _currentPlayer.value = nuevaPartida.obtenerJugadorActual()
        _navigationEvent.value = NavigationState.InGame
    }

    fun lanzarDado() {
        val gameInstance = _game.value ?: return
        val resultado = (1..6).random() // Lógica simple de dado
        _diceResult.value = resultado

        // Verificar si el jugador actual puede moverse
        val jugadorActual = _currentPlayer.value
        if (jugadorActual != null && !puedeMover(jugadorActual, resultado)) {
            // Si no puede mover, pasamos turno automáticamente o tras un delay
            finalizarTurno()
        }
    }

    fun onFichaClicked(ficha: Ficha) {
        val dado = _diceResult.value ?: 0
        if (dado > 0) {
            // Mover ficha y pasar turno
            // game.value?.moverFicha(ficha, dado)
            finalizarTurno()
        }
    }

    private fun finalizarTurno() {
        // Lógica para cambiar al siguiente jugador en el Model
        // _currentPlayer.value = _game.value?.siguienteTurno()
        _diceResult.value = 0
    }

    private fun puedeMover(jugador: Jugador, dado: Int): Boolean {
        // Lógica para comprobar si alguna ficha puede moverse
        return true
    }
}

// Clase para gestionar los estados de navegación
sealed class NavigationState {
    object Home : NavigationState()
    object GameSettings : NavigationState()
    object InGame : NavigationState()
}
