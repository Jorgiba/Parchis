package com.example.parchis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parchis.model.SesionUsuario
import com.example.parchis.model.UsuarioRegistrado

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: LiveData<LoginResult?> = _loginResult

    fun login(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty()) {
            if (username.equals("Marco", ignoreCase = true)) {
                // Si es Marco, cargamos sus datos de prueba predefinidos
                SesionUsuario.cargarDatosPrueba()
            } else {
                // Para cualquier otro usuario, creamos una sesión nueva (vacía)
                SesionUsuario.usuarioLogueado = UsuarioRegistrado(username)
            }
            
            val usuario = SesionUsuario.usuarioLogueado!!
            _loginResult.value = LoginResult.Success(usuario)
        } else {
            _loginResult.value = LoginResult.Error("El usuario y la contraseña no pueden estar vacíos")
        }
    }

    fun clearLoginResult() {
        _loginResult.value = null
    }
}

sealed class LoginResult {
    data class Success(val usuario: UsuarioRegistrado) : LoginResult()
    data class Error(val message: String) : LoginResult()
}