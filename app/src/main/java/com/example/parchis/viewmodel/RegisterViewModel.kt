package com.example.parchis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parchis.model.SesionUsuario
import com.example.parchis.model.UsuarioRegistrado

class RegisterViewModel : ViewModel() {

    // Variables para DataBinding bidireccional (Punto 15 y 16 del temario)
    val username = MutableLiveData<String>("")
    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")
    val confirmPassword = MutableLiveData<String>("")

    private val _registerResult = MutableLiveData<RegisterResult?>()
    val registerResult: LiveData<RegisterResult?> = _registerResult

    // Función de orden superior/Lambda aplicada en la lógica (Punto 3)
    fun onRegisterClick() {
        val u = username.value ?: ""
        val e = email.value ?: ""
        val p = password.value ?: ""
        val cp = confirmPassword.value ?: ""

        if (u.isEmpty() || e.isEmpty() || p.isEmpty() || cp.isEmpty()) {
            _registerResult.value = RegisterResult.Error("Por favor, rellena todos los campos")
            return
        }

        if (p != cp) {
            _registerResult.value = RegisterResult.Error("Las contraseñas no coinciden")
            return
        }

        // Uso de funciones de contexto (Punto 4)
        UsuarioRegistrado(u).also { usuario ->
            SesionUsuario.usuarioLogueado = usuario
            _registerResult.value = RegisterResult.Success(usuario)
        }
    }

    fun clearRegisterResult() {
        _registerResult.value = null
    }
}

sealed class RegisterResult {
    data class Success(val usuario: UsuarioRegistrado) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}