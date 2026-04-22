package com.example.parchis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parchis.model.SesionUsuario
import com.example.parchis.model.UsuarioRegistrado

class LoginViewModel : ViewModel() {

    // Variables para DataBinding (Two-way binding)
    val username = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: LiveData<LoginResult?> = _loginResult

    fun onLoginClick() {
        val userVal = username.value ?: ""
        val passVal = password.value ?: ""
        
        if (userVal.isNotEmpty() && passVal.isNotEmpty()) {
            if (userVal.equals("Ejemplo", ignoreCase = true)) {
                SesionUsuario.cargarDatosPrueba()
            } else {
                SesionUsuario.usuarioLogueado = UsuarioRegistrado(userVal)
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