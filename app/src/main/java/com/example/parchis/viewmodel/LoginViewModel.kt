package com.example.parchis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parchis.database.UsuarioDao
import com.example.parchis.model.SesionUsuario
import com.example.parchis.model.UsuarioRegistrado
import kotlinx.coroutines.launch

class LoginViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    // Variables para DataBinding (Two-way binding)
    val username = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: LiveData<LoginResult?> = _loginResult

    fun onLoginClick() {
        val userVal = username.value ?: ""
        val passVal = password.value ?: ""
        
        if (userVal.isNotEmpty() && passVal.isNotEmpty()) {
            viewModelScope.launch {
                if (userVal.equals("Ejemplo", ignoreCase = true)) {
                    SesionUsuario.cargarDatosPrueba()
                } else {
                    var usuario = usuarioDao.obtenerUsuario(userVal)
                    
                    if (usuario == null) {
                        usuario = UsuarioRegistrado(userVal)
                        usuarioDao.insertarUsuario(usuario)
                    }
                    
                    val historial = usuarioDao.obtenerHistorial(userVal)
                    usuario.historialPartidas.clear()
                    usuario.historialPartidas.addAll(historial)
                    
                    SesionUsuario.usuarioLogueado = usuario
                }
                
                val usuarioLogueado = SesionUsuario.usuarioLogueado!!
                _loginResult.postValue(LoginResult.Success(usuarioLogueado))
            }
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
