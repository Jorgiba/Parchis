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
            // Lógica para iniciar sesión
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnIniciarPartida.setOnClickListener {
            // Lógica para iniciar partida como invitado
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnRegistrar.setOnClickListener {
            // Lógica para registrarse
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}