package com.example.parchis.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.parchis.R
import com.example.parchis.model.DificultadBot
import com.example.parchis.model.Jugador
import com.example.parchis.model.ParchisGame
import com.example.parchis.model.SesionUsuario

class GameSettingsActivity : AppCompatActivity() {
    private lateinit var btnVolver: ImageButton
    private lateinit var btnEmpezar: Button
    private lateinit var rgPlayers: RadioGroup
    private lateinit var rgDifficulty: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        btnVolver = findViewById(R.id.btnBackSettings)
        btnEmpezar = findViewById(R.id.btnStartLocal)
        rgPlayers = findViewById(R.id.rgPlayers)
        rgDifficulty = findViewById(R.id.rgDifficulty)

        // Valores por defecto
        rgPlayers.check(rgPlayers.getChildAt(3).id) // 4 jugadores
        rgDifficulty.check(rgDifficulty.getChildAt(1).id) // Media

        btnVolver.setOnClickListener {
            finish()
        }

        btnEmpezar.setOnClickListener {
            val numHumanos = when (rgPlayers.checkedRadioButtonId) {
                rgPlayers.getChildAt(0).id -> 1
                rgPlayers.getChildAt(1).id -> 2
                rgPlayers.getChildAt(2).id -> 3
                rgPlayers.getChildAt(3).id -> 4
                else -> 4
            }

            val dificultad = when (rgDifficulty.checkedRadioButtonId) {
                rgDifficulty.getChildAt(0).id -> DificultadBot.FACIL
                rgDifficulty.getChildAt(1).id -> DificultadBot.MEDIA
                rgDifficulty.getChildAt(2).id -> DificultadBot.DIFICIL
                else -> DificultadBot.MEDIA
            }

            iniciarJuego(numHumanos, dificultad)
        }
    }

    private fun iniciarJuego(numHumanos: Int, dificultad: DificultadBot) {
        // Llamamos al método estático del modelo para crear los jugadores
        val jugadores = ParchisGame.crearPartidaLocal(
            numHumanos, 
            dificultad, 
            SesionUsuario.usuarioLogueado
        )

        // Guardamos los jugadores en el objeto de configuración para que la GameActivity los lea
        ConfiguracionPartida.jugadoresSeleccionados = jugadores
        
        startActivity(Intent(this, GameActivity::class.java))
    }
}

object ConfiguracionPartida {
    var jugadoresSeleccionados: List<Jugador> = emptyList()
}