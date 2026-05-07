package com.example.parchis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parchis.database.UsuarioDao
import com.example.parchis.model.SesionUsuario
import com.example.parchis.model.UsuarioRegistrado
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: LiveData<LoginResult?> = _loginResult

    fun onLoginClick() {
        val emailVal = email.value ?: ""
        val passVal = password.value ?: ""
        
        if (emailVal.isNotEmpty() && passVal.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    val result = auth.signInWithEmailAndPassword(emailVal, passVal).await()
                    val firebaseUser = result.user

                    if (firebaseUser != null) {
                        var usuario = usuarioDao.obtenerUsuarioPorEmail(emailVal)
                        
                        if (usuario == null) {
                            usuario = UsuarioRegistrado(username = emailVal.split("@")[0], email = emailVal)
                            usuarioDao.insertarUsuario(usuario)
                        }
                        
                        val historial = usuarioDao.obtenerHistorial(usuario.username)
                        usuario.historialPartidas.clear()
                        usuario.historialPartidas.addAll(historial)
                        
                        SesionUsuario.usuarioLogueado = usuario
                        _loginResult.postValue(LoginResult.Success(usuario))
                    }
                } catch (e: Exception) {
                    _loginResult.postValue(LoginResult.Error("Fallo al iniciar sesión: ${e.message}"))
                }
            }
        } else {
            _loginResult.value = LoginResult.Error("El email y la contraseña no pueden estar vacíos")
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
