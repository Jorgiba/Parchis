package com.example.parchis.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.parchis.R
import com.example.parchis.databinding.FragmentGameBinding
import com.example.parchis.model.*
import com.example.parchis.viewmodel.GameViewModel

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by viewModels()
    private val fichaViews = mutableMapOf<Ficha, ImageView>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAbandon.setOnClickListener {
            findNavController().popBackStack()
            findNavController().popBackStack()
        }

        binding.diceActionArea.setOnClickListener {
            viewModel.lanzarDado()
        }

        viewModel.diceResult.observe(viewLifecycleOwner) { result ->
            binding.tvDiceNumber.text = result?.toString() ?: "?"
        }

        viewModel.currentPlayer.observe(viewLifecycleOwner) { jugador ->
            binding.tvTurn.text = "TURNO DE:\n${jugador.nombre.uppercase()}"
        }

        viewModel.fichasUpdateEvent.observe(viewLifecycleOwner) {
            actualizarPosicionesFichas()
        }

        val jugadoresSeleccionados = ConfiguracionPartida.jugadoresSeleccionados
        if (jugadoresSeleccionados.isNotEmpty()) {
            viewModel.initGame(jugadoresSeleccionados)
            mostrarNombresJugadores()
        }

        crearFichas()
    }

    private fun mostrarNombresJugadores() {
        val jugadores = viewModel.getJugadores()

        // Logs para depuración en consola
        Log.d("ParchisGame", "=== CONFIGURACIÓN DE PARTIDA ===")
        jugadores.forEach { j ->
            Log.d("ParchisGame", "Jugador: ${j.nombre} | Color: ${j.color}")
        }

        // Ocultamos todos inicialmente
        binding.llPlayerRed.visibility = View.GONE
        binding.llPlayerBlue.visibility = View.GONE
        binding.llPlayerGreen.visibility = View.GONE
        binding.llPlayerYellow.visibility = View.GONE

        jugadores.forEach { jugador ->
            when (jugador.color) {
                ColorParchis.ROJO -> {
                    binding.tvPlayer1.text = jugador.nombre
                    binding.llPlayerRed.visibility = View.VISIBLE
                }
                ColorParchis.AZUL -> {
                    binding.tvPlayer2.text = jugador.nombre
                    binding.llPlayerBlue.visibility = View.VISIBLE
                }
                ColorParchis.VERDE -> {
                    binding.tvPlayer4.text = jugador.nombre
                    binding.llPlayerGreen.visibility = View.VISIBLE
                }
                ColorParchis.AMARILLO -> {
                    binding.tvPlayer3.text = jugador.nombre
                    binding.llPlayerYellow.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun crearFichas() {
        val players = viewModel.getJugadores()
        players.forEach { jugador ->
            jugador.fichas.forEach { ficha ->
                val fichaView = ImageView(requireContext())
                fichaView.setImageResource(R.drawable.ficha_circle)

                val colorRes = when(ficha.color) {
                    ColorParchis.ROJO -> android.R.color.holo_red_dark
                    ColorParchis.AZUL -> android.R.color.holo_blue_dark
                    ColorParchis.VERDE -> android.R.color.holo_green_dark
                    ColorParchis.AMARILLO -> android.R.color.holo_orange_light
                }
                fichaView.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorRes))

                fichaView.setOnClickListener {
                    viewModel.onFichaClicked(ficha)
                }

                fichaViews[ficha] = fichaView
                binding.boardContainer.addView(fichaView)
            }
        }
        actualizarPosicionesFichas()
    }

    private fun actualizarPosicionesFichas() {
        val density = resources.displayMetrics.density
        val size = (20 * density).toInt()
        val boardWidth = binding.boardContainer.width.takeIf { it > 0 } ?: (360 * density).toInt()
        val boardHeight = binding.boardContainer.height.takeIf { it > 0 } ?: (360 * density).toInt()

        fichaViews.forEach { (ficha, view) ->
            val params = FrameLayout.LayoutParams(size, size)
            if (ficha.estado == EstadoFicha.EN_CASA) {
                val casaBase = when(ficha.color) {
                    ColorParchis.ROJO -> Pair(55, 55)
                    ColorParchis.AZUL -> Pair(265, 55)
                    ColorParchis.AMARILLO -> Pair(265, 265)
                    ColorParchis.VERDE -> Pair(55, 265)
                }
                val offsetX = if (ficha.id % 2 == 0) 0 else 40
                val offsetY = if (ficha.id < 2) 0 else 40
                params.leftMargin = ((casaBase.first + offsetX) * density).toInt()
                params.topMargin = ((casaBase.second + offsetY) * density).toInt()
            } else {
                val coords = BoardPositionMapper.getPosition(ficha.posicion, boardWidth, boardHeight)
                params.leftMargin = (coords.first - size/2).toInt()
                params.topMargin = (coords.second - size/2).toInt()
            }
            view.layoutParams = params
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}