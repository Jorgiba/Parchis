package com.example.parchis.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.parchis.R

class GameActivity : AppCompatActivity() {
    private lateinit var btnAbandonar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        btnAbandonar = findViewById(R.id.btnAbandon)

        btnAbandonar.setOnClickListener {
            // Lógica para abandonar la partida
            finish()
        }
    }
}