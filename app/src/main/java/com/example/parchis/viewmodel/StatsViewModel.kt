package com.example.parchis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.parchis.database.UsuarioDao
import com.example.parchis.model.SesionUsuario
import com.example.parchis.model.UsuarioRegistrado
import kotlinx.coroutines.launch

class StatsViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _statsResult = MutableLiveData<StatsResult?>()
    val statsResult: LiveData<StatsResult?> = _statsResult

    val gamesPlayed: LiveData<String> = _statsResult.map { result ->
        if (result is StatsResult.Success) "Partidas jugadas: ${result.usuario.partidasJugadas}" else ""
    }
    
    val wins: LiveData<String> = _statsResult.map { result ->
        if (result is StatsResult.Success) "Victorias: ${result.usuario.victorias}" else ""
    }
    
    val losses: LiveData<String> = _statsResult.map { result ->
        if (result is StatsResult.Success) "Derrotas: ${result.usuario.derrotas}" else ""
    }
    
    val piecesEaten: LiveData<String> = _statsResult.map { result ->
        if (result is StatsResult.Success) "Fichas comidas: ${result.usuario.fichasComidas}" else ""
    }

    fun loadUserStats() {
        val usuario = SesionUsuario.usuarioLogueado
        if (usuario != null) {
            viewModelScope.launch {
                val usuarioActualizado = usuarioDao.obtenerUsuario(usuario.username)
                if (usuarioActualizado != null) {
                    val historial = usuarioDao.obtenerHistorial(usuario.username)
                    usuarioActualizado.historialPartidas.clear()
                    usuarioActualizado.historialPartidas.addAll(historial)
                    _statsResult.postValue(StatsResult.Success(usuarioActualizado))
                } else {
                    _statsResult.postValue(StatsResult.Success(usuario))
                }
            }
        } else {
            _statsResult.value = StatsResult.Error("No hay ningún usuario con sesión iniciada")
        }
    }

    fun clearStatsResult() {
        _statsResult.value = null
    }
}

sealed class StatsResult {
    data class Success(val usuario: UsuarioRegistrado) : StatsResult()
    data class Error(val message: String) : StatsResult()
}
