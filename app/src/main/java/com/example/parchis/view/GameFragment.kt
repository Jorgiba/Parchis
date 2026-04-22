package com.example.parchis.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.parchis.R
import com.example.parchis.databinding.FragmentGameBinding
import com.example.parchis.model.*
import com.example.parchis.viewmodel.GameViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by viewModels()
    private val fichaViews = mutableMapOf<Ficha, ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "GameFragment: onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d("Lifecycle", "GameFragment: onCreateView")
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Lifecycle", "GameFragment: onViewCreated")

        binding.btnAbandon.setOnClickListener {
            showAbandonDialog()
        }

        binding.diceActionArea.setOnClickListener {
            viewModel.lanzarDado()
        }

        // Observamos el LiveData del dado para actualizar la UI
        viewModel.diceResult.observe(viewLifecycleOwner) { result ->
            binding.tvDiceNumber.text = result?.toString() ?: getString(R.string.interrogación)
        }

        // Observamos el turno actual
        viewModel.currentPlayer.observe(viewLifecycleOwner) { jugador ->
            binding.tvTurn.text = getString(R.string.turno_jugador, jugador.nombre.uppercase())
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

    private fun showAbandonDialog() {
        // Implementación de Diálogo Material (Punto 7 y 16 del temario)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.abandonar_partida)
            .setMessage("¿Estás seguro de que deseas salir de la partida? Se perderá todo el progreso actual.")
            .setNegativeButton(R.string.volver) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.abandonar_partida) { _, _ ->
                confirmAbandon()
            }
            .show()
    }

    private fun confirmAbandon() {
        Log.d("Navigation", "Abandon confirmed")
        val destination = if (SesionUsuario.usuarioLogueado != null) {
            R.id.homeFragment
        } else {
            R.id.mainFragment
        }
        
        val popped = findNavController().popBackStack(destination, false)
        if (!popped) {
            findNavController().navigate(destination)
        }
    }

    private fun mostrarNombresJugadores() {
        val jugadores = viewModel.getJugadores()
        Log.d("ParchisGame", "Configurando partida con ${jugadores.size} jugadores")

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
                val fichaView = ImageView(requireContext()).apply {
                    setImageResource(R.drawable.ficha_circle)
                    
                    val colorRes = when(ficha.color) {
                        ColorParchis.ROJO -> android.R.color.holo_red_dark
                        ColorParchis.AZUL -> android.R.color.holo_blue_dark
                        ColorParchis.VERDE -> android.R.color.holo_green_dark
                        ColorParchis.AMARILLO -> android.R.color.holo_orange_light
                    }
                    imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorRes))

                    setOnClickListener {
                        viewModel.onFichaClicked(ficha)
                    }
                }

                fichaViews[ficha] = fichaView
                binding.boardContainer.addView(fichaView)
            }
        }
        actualizarPosicionesFichas()
    }

    private fun actualizarPosicionesFichas() {
        binding.boardContainer.post {
            val density = resources.displayMetrics.density
            val size = (20 * density).toInt()
            val boardWidth = binding.boardContainer.width
            val boardHeight = binding.boardContainer.height

            if (boardWidth == 0 || boardHeight == 0) return@post

            fichaViews.forEach { (ficha, view) ->
                view.layoutParams = FrameLayout.LayoutParams(size, size).apply {
                    if (ficha.estado == EstadoFicha.EN_CASA) {
                        val casaBase = when(ficha.color) {
                            ColorParchis.ROJO -> Pair(boardWidth * 0.15f, boardHeight * 0.15f)
                            ColorParchis.AZUL -> Pair(boardWidth * 0.75f, boardHeight * 0.15f)
                            ColorParchis.AMARILLO -> Pair(boardWidth * 0.75f, boardHeight * 0.75f)
                            ColorParchis.VERDE -> Pair(boardWidth * 0.15f, boardHeight * 0.75f)
                        }
                        val offsetX = (if (ficha.id % 2 == 0) -15 else 15) * density
                        val offsetY = (if (ficha.id < 2) -15 else 15) * density
                        
                        leftMargin = (casaBase.first + offsetX).toInt()
                        topMargin = (casaBase.second + offsetY).toInt()
                    } else {
                        val coords = BoardPositionMapper.getPosition(ficha.posicion, boardWidth, boardHeight)
                        leftMargin = (coords.first - size/2).toInt()
                        topMargin = (coords.second - size/2).toInt()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "GameFragment: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "GameFragment: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "GameFragment: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "GameFragment: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Lifecycle", "GameFragment: onDestroyView")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "GameFragment: onDestroy")
    }
}