package com.example.parchis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parchis.model.SesionUsuario
import com.example.parchis.model.UsuarioRegistrado

class StatsViewModel : ViewModel() {

    private val _statsResult = MutableLiveData<StatsResult?>()
    val statsResult: LiveData<StatsResult?> = _statsResult

    fun loadUserStats() {
        val usuario = SesionUsuario.usuarioLogueado
        if (usuario != null) {
            _statsResult.value = StatsResult.Success(usuario)
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