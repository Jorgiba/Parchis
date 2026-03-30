package com.example.parchis.view

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.parchis.R
import com.example.parchis.model.SesionUsuario
import com.example.parchis.model.UsuarioRegistrado
import com.example.parchis.viewmodel.StatsResult
import com.example.parchis.viewmodel.StatsViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class StatsActivity : AppCompatActivity() {
    private lateinit var btnVolver: ImageButton
    private lateinit var tvGamesPlayed: TextView
    private lateinit var tvWins: TextView
    private lateinit var tvLosses: TextView
    private lateinit var tvPiecesEaten: TextView
    private lateinit var tvLastGamesList: TextView

    private val viewModel: StatsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        // Para propósitos de prueba
        if (SesionUsuario.usuarioLogueado == null) {
            SesionUsuario.cargarDatosPrueba()
        }

        btnVolver = findViewById(R.id.btnBackStats)
        tvGamesPlayed = findViewById(R.id.tvGamesPlayed)
        tvWins = findViewById(R.id.tvWins)
        tvLosses = findViewById(R.id.tvLosses)
        tvPiecesEaten = findViewById(R.id.tvPiecesEaten)
        tvLastGamesList = findViewById(R.id.tvLastGamesList)

        btnVolver.setOnClickListener {
            finish()
        }

        // Observamos los resultados del ViewModel
        viewModel.statsResult.observe(this) { result ->
            when (result) {
                is StatsResult.Success -> {
                    mostrarEstadisticas(result.usuario)
                }
                is StatsResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                null -> { /* Estado inicial */ }
            }
        }

        // Cargamos los datos
        viewModel.loadUserStats()
    }

    private fun mostrarEstadisticas(usuario: UsuarioRegistrado) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        
        tvGamesPlayed.text = "Partidas jugadas: ${usuario.partidasJugadas}"
        tvWins.text = "Victorias: ${usuario.victorias}"
        tvLosses.text = "Derrotas: ${usuario.derrotas}"
        tvPiecesEaten.text = "Fichas comidas: ${usuario.fichasComidas}"

        if (usuario.historialPartidas.isEmpty()) {
            tvLastGamesList.text = "No hay partidas registradas"
        } else {
            val historialTexto = usuario.historialPartidas.reversed().joinToString("\n\n") { partida ->
                val resultadoTexto = if (partida.resultado.name == "VICTORIA") "Victoria" else "Derrota"
                val fechaFormateada = dateFormat.format(partida.fecha)
                val nombresJugadores = partida.jugadores.joinToString(", ")
                
                "$resultadoTexto ($fechaFormateada)\nJugadores: $nombresJugadores"
            }
            tvLastGamesList.text = historialTexto
        }
    }
}