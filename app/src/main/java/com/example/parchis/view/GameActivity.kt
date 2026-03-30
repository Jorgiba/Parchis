package com.example.parchis.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.parchis.R
import com.example.parchis.model.ColorParchis
import com.example.parchis.model.EstadoFicha
import com.example.parchis.model.Ficha
import com.example.parchis.viewmodel.GameViewModel

class GameActivity : AppCompatActivity() {
    private lateinit var btnAbandonar: Button
    private lateinit var boardContainer: FrameLayout
    private lateinit var tvTurn: TextView
    private lateinit var tvDiceNumber: TextView
    private lateinit var diceActionArea: View

    private val viewModel: GameViewModel by viewModels()
    private val fichaViews = mutableMapOf<Ficha, ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        btnAbandonar = findViewById(R.id.btnAbandon)
        boardContainer = findViewById(R.id.boardContainer)
        tvTurn = findViewById(R.id.tvTurn)
        tvDiceNumber = findViewById(R.id.tvDiceNumber)
        diceActionArea = findViewById(R.id.diceActionArea)

        btnAbandonar.setOnClickListener {
            finish()
        }

        diceActionArea.setOnClickListener {
            viewModel.lanzarDado()
        }

        viewModel.diceResult.observe(this) { result ->
            tvDiceNumber.text = result?.toString() ?: "?"
        }

        viewModel.currentPlayer.observe(this) { jugador ->
            tvTurn.text = "TURNO DE:\n${jugador.nombre.uppercase()}"
        }

        viewModel.fichasUpdateEvent.observe(this) {
            actualizarPosicionesFichas()
        }

        // Obtener los jugadores configurados desde el Singleton
        val jugadoresSeleccionados = ConfiguracionPartida.jugadoresSeleccionados
        if (jugadoresSeleccionados.isNotEmpty()) {
            viewModel.initGame(jugadoresSeleccionados)
        }

        crearFichas()
    }

    private fun crearFichas() {
        // Obtenemos los jugadores reales del ViewModel
        val players = viewModel.getJugadores()
        
        players.forEach { jugador ->
            jugador.fichas.forEach { ficha ->
                val fichaView = ImageView(this)
                fichaView.setImageResource(R.drawable.ficha_circle)
                
                val colorRes = when(ficha.color) {
                    ColorParchis.ROJO -> android.R.color.holo_red_dark
                    ColorParchis.AZUL -> android.R.color.holo_blue_dark
                    ColorParchis.VERDE -> android.R.color.holo_green_dark
                    ColorParchis.AMARILLO -> android.R.color.holo_orange_light
                }
                fichaView.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, colorRes))
                
                fichaView.setOnClickListener {
                    viewModel.onFichaClicked(ficha)
                }

                fichaViews[ficha] = fichaView
                boardContainer.addView(fichaView)
            }
        }
        actualizarPosicionesFichas()
    }

    private fun actualizarPosicionesFichas() {
        val size = (24 * resources.displayMetrics.density).toInt()
        val boardWidth = boardContainer.width.takeIf { it > 0 } ?: (360 * resources.displayMetrics.density).toInt()
        val boardHeight = boardContainer.height.takeIf { it > 0 } ?: (360 * resources.displayMetrics.density).toInt()

        fichaViews.forEach { (ficha, view) ->
            val params = FrameLayout.LayoutParams(size, size)
            
            if (ficha.estado == EstadoFicha.EN_CASA) {
                val casaBase = when(ficha.color) {
                    ColorParchis.ROJO -> Pair(40, 240)
                    ColorParchis.AZUL -> Pair(40, 40)
                    ColorParchis.VERDE -> Pair(240, 240)
                    ColorParchis.AMARILLO -> Pair(240, 40)
                }
                val offsetX = if (ficha.id % 2 == 0) 0 else 35
                val offsetY = if (ficha.id < 2) 0 else 35
                params.leftMargin = (casaBase.first + offsetX).dpToPx()
                params.topMargin = (casaBase.second + offsetY).dpToPx()
            } else {
                val coords = BoardPositionMapper.getPosition(ficha.posicion, boardWidth, boardHeight)
                params.leftMargin = (coords.first - size/2).toInt()
                params.topMargin = (coords.second - size/2).toInt()
            }
            
            view.layoutParams = params
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}