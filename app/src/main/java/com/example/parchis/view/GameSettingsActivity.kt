package com.example.parchis.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.parchis.R

class GameSettingsActivity : AppCompatActivity() {
    private lateinit var btnVolver: ImageButton
    private lateinit var btnEmpezar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        btnVolver = findViewById(R.id.btnBackSettings)
        btnEmpezar = findViewById(R.id.btnStartLocal)

        btnVolver.setOnClickListener {
            // Lógica para volver a la pantalla anterior
            finish()
        }

        btnEmpezar.setOnClickListener {
            // Lógica para comenzar la partida
            startActivity(Intent(this, GameActivity::class.java))
        }
    }
}