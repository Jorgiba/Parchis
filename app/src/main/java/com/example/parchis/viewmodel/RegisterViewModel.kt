package com.example.parchis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parchis.model.SesionUsuario
import com.example.parchis.model.UsuarioRegistrado

class RegisterViewModel : ViewModel() {

    private val _registerResult = MutableLiveData<RegisterResult?>()
    val registerResult: LiveData<RegisterResult?> = _registerResult

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _registerResult.value = RegisterResult.Error("Por favor, rellena todos los campos")
            return
        }

        if (password != confirmPassword) {
            _registerResult.value = RegisterResult.Error("Las contraseñas no coinciden")
            return
        }

        val usuario = UsuarioRegistrado(username)
        // Guardamos la sesión en el singleton
        SesionUsuario.usuarioLogueado = usuario
        _registerResult.value = RegisterResult.Success(usuario)
    }

    fun clearRegisterResult() {
        _registerResult.value = null
    }
}

sealed class RegisterResult {
    data class Success(val usuario: UsuarioRegistrado) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}