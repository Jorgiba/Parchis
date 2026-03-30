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
import com.example.parchis.viewmodel.RegisterResult
import com.example.parchis.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegisterSubmit: Button
    private lateinit var tvGoToLogin: TextView

    // Instanciamos el ViewModel usando el delegado viewModels()
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnBack = findViewById(R.id.btnBackRegister)
        etUsername = findViewById(R.id.etRegisterUsername)
        etEmail = findViewById(R.id.etRegisterEmail)
        etPassword = findViewById(R.id.etRegisterPassword)
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        // Observamos los cambios en el resultado del registro
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is RegisterResult.Success -> {
                    Toast.makeText(this, "Cuenta creada con éxito para ${result.usuario.username}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                is RegisterResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                null -> { /* Estado inicial */ }
            }
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnRegisterSubmit.setOnClickListener {
            val username = etUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            // Delegamos la lógica al ViewModel
            viewModel.register(username, email, password, confirmPassword)
        }

        tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}