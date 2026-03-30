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

class GameModeActivity : AppCompatActivity() {
    private lateinit var btnVolver: ImageButton
    private lateinit var btnPartidaLocal: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_mode)

        btnVolver = findViewById(R.id.btnBackMode)
        btnPartidaLocal = findViewById(R.id.btnLocalGame)

        btnVolver.setOnClickListener {
            // Lógica para volver a la pantalla anterior
            finish()
        }
        btnPartidaLocal.setOnClickListener {
            val intent = Intent(this, GameSettingsActivity::class.java)
            startActivity(intent)
        }
    }
}