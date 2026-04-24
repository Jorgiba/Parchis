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

class RegisterViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    // Variables para DataBinding bidireccional
    val username = MutableLiveData<String>("")
    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")
    val confirmPassword = MutableLiveData<String>("")

    private val _registerResult = MutableLiveData<RegisterResult?>()
    val registerResult: LiveData<RegisterResult?> = _registerResult

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

        viewModelScope.launch {
            try {
                // 1. Crear usuario en Firebase Auth
                val result = auth.createUserWithEmailAndPassword(e, p).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    // 2. Guardar el usuario en la base de datos local (Room) 
                    // Usamos el email como identificador o el UID de Firebase
                    val nuevoUsuario = UsuarioRegistrado(u) // Mantenemos el nombre de usuario elegido
                    usuarioDao.insertarUsuario(nuevoUsuario)
                    
                    SesionUsuario.usuarioLogueado = nuevoUsuario
                    _registerResult.postValue(RegisterResult.Success(nuevoUsuario))
                }
            } catch (ex: Exception) {
                _registerResult.postValue(RegisterResult.Error("Error al registrar: ${ex.message}"))
            }
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
