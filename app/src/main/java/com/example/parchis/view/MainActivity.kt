package com.example.parchis.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.parchis.R

class MainActivity : AppCompatActivity() {
    private lateinit var btnIniciarSesion: Button
    private lateinit var btnIniciarPartida: Button
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnIniciarSesion = findViewById(R.id.btnLogin)
        btnIniciarPartida = findViewById(R.id.btnGuest)
        btnRegistrar = findViewById(R.id.btnRegister)

        btnIniciarSesion.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnIniciarPartida.setOnClickListener {
            startActivity(Intent(this, GameSettingsActivity::class.java))
        }

        btnRegistrar.setOnClickListener {
            // Lógica para registrarse
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}