package com.example.parchis.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.parchis.R
import com.example.parchis.viewmodel.LoginResult
import com.example.parchis.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var btnVolver: ImageButton
    private lateinit var etUsuario: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var btnContrasenaOlvidada: TextView
    private lateinit var btnRegistrarse: TextView

    // Instanciamos el ViewModel usando el delegado viewModels()
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnVolver = findViewById(R.id.btnBackLogin)
        etUsuario = findViewById(R.id.etUsernameLogin)
        etContrasena = findViewById(R.id.etPasswordLogin)
        btnIniciarSesion = findViewById(R.id.btnLoginSubmit)
        btnContrasenaOlvidada = findViewById(R.id.btnForgotPassword)
        btnRegistrarse = findViewById(R.id.btnGoToRegister)

        // Observamos los cambios en el resultado del login
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(this, "Bienvenido, ${result.usuario.username}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                is LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                null -> { /* Estado inicial o limpiado */ }
            }
        }

        btnVolver.setOnClickListener {
            finish()
        }

        btnIniciarSesion.setOnClickListener {
            val username = etUsuario.text.toString()
            val password = etContrasena.text.toString()
            
            // Delegamos la lógica al ViewModel
            viewModel.login(username, password)
        }

        btnContrasenaOlvidada.setOnClickListener {
            // Lógica para recuperar contraseña
            Toast.makeText(this, "Funcionalidad no implementada", Toast.LENGTH_SHORT).show()
        }

        btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}