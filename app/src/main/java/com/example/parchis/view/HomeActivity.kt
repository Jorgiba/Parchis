package com.example.parchis.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.parchis.R
import androidx.core.view.isVisible

class HomeActivity : AppCompatActivity() {
    private lateinit var btnMenu: ImageButton
    private lateinit var sideMenu: LinearLayout
    private lateinit var btnIniciarPartida: Button
    private lateinit var btnEstadisticas: Button
    private lateinit var btnCerrarSesion: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnMenu = findViewById(R.id.btnMenu)
        btnIniciarPartida = findViewById(R.id.btnStartGame)
        btnEstadisticas = findViewById(R.id.btnStats)
        btnCerrarSesion = findViewById(R.id.btnLogout)
        sideMenu = findViewById(R.id.sideMenu)

        btnMenu.setOnClickListener {
            if (sideMenu.isVisible) {
                sideMenu.visibility = View.GONE
            } else {
                sideMenu.visibility = View.VISIBLE
            }
        }

        btnIniciarPartida.setOnClickListener {
            // Lógica para iniciar partida
            startActivity(Intent(this, GameSettingsActivity::class.java))
        }

        btnEstadisticas.setOnClickListener {
            // Lógica para ver estadísticas
            startActivity(Intent(this, StatsActivity::class.java))
            sideMenu.visibility = View.GONE
        }

        btnCerrarSesion.setOnClickListener {
            // Lógica para cerrar sesión: volver al MainActivity y limpiar el stack
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


    }
}