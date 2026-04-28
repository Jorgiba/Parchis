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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.parchis.ParchisApplication
import com.example.parchis.R
import com.example.parchis.databinding.FragmentGameBinding
import com.example.parchis.model.*
import com.example.parchis.viewmodel.GameViewModel
import com.example.parchis.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by viewModels {
        ViewModelFactory((requireActivity().application as ParchisApplication).database.usuarioDao())
    }

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

        viewModel.diceResult.observe(viewLifecycleOwner) { result ->
            binding.tvDiceNumber.text = result?.toString() ?: getString(R.string.interrogación)
        }

        viewModel.currentPlayer.observe(viewLifecycleOwner) { jugador ->
            binding.tvTurn.text = getString(R.string.turno_jugador, jugador.nombre.uppercase())
        }

        viewModel.fichasUpdateEvent.observe(viewLifecycleOwner) {
            actualizarPosicionesFichas()
        }

        viewModel.gameFinished.observe(viewLifecycleOwner) { ganador ->
            ganador?.let {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("¡Partida Finalizada!")
                    .setMessage("El ganador es: ${it.nombre}")
                    .setPositiveButton("Ir al inicio") { _, _ ->
                        confirmAbandon()
                    }
                    .setCancelable(false)
                    .show()
            }
        }

        val jugadoresSeleccionados = ConfiguracionPartida.jugadoresSeleccionados
        if (jugadoresSeleccionados.isNotEmpty()) {
            viewModel.initGame(jugadoresSeleccionados)
            mostrarNombresJugadores()
        }

        // Esperar a que el boardContainer tenga sus dimensiones antes de crear y posicionar fichas
        binding.boardContainer.post {
            crearFichas()
        }
    }

    private fun showAbandonDialog() {
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
        val destination = if (SesionUsuario.usuarioLogueado != null) {
            R.id.homeFragment
        } else {
            R.id.mainFragment
        }
        findNavController().popBackStack(destination, false)
    }

    private fun mostrarNombresJugadores() {
        val jugadores = viewModel.getJugadores()
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
                    val colorRes = when (ficha.color) {
                        ColorParchis.ROJO -> android.R.color.holo_red_dark
                        ColorParchis.AZUL -> android.R.color.holo_blue_dark
                        ColorParchis.VERDE -> android.R.color.holo_green_dark
                        ColorParchis.AMARILLO -> android.R.color.holo_orange_light
                    }
                    imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), colorRes)
                    )
                    setOnClickListener { viewModel.onFichaClicked(ficha) }
                }
                fichaViews[ficha] = fichaView
                binding.boardContainer.addView(fichaView)
            }
        }
        actualizarPosicionesFichas()
    }

    private fun actualizarPosicionesFichas() {
        val boardWidth = binding.boardContainer.width
        val boardHeight = binding.boardContainer.height

        // Si el contenedor aún no tiene dimensiones, esperar al siguiente layout pass
        if (boardWidth == 0 || boardHeight == 0) {
            binding.boardContainer.post { actualizarPosicionesFichas() }
            return
        }

        val density = resources.displayMetrics.density
        val fichaSize = (15 * density).toInt()

        // Posiciones dentro de cada casa para las 4 fichas (2x2 grid)
        // Los offsets están en porcentaje del tamaño del tablero para escalar con él
        // Las casas ocupan ~27% del tablero en cada esquina
        // Grid 2x2: fichas a ±4% del centro de la casa
        val casaOffsets = listOf(
            Pair(-0.04f, -0.04f),  // ficha id=0: arriba-izquierda
            Pair( 0.04f, -0.04f),  // ficha id=1: arriba-derecha
            Pair(-0.04f,  0.04f),  // ficha id=2: abajo-izquierda
            Pair( 0.04f,  0.04f)   // ficha id=3: abajo-derecha
        )

        fichaViews.forEach { (ficha, view) ->
            val params = FrameLayout.LayoutParams(fichaSize, fichaSize)

            if (ficha.estado == EstadoFicha.EN_CASA) {
                // Centro de cada casa en coordenadas relativas al tablero
                val casaCentro = when (ficha.color) {
                    ColorParchis.ROJO     -> Pair(0.165f, 0.165f)  // esquina superior izquierda
                    ColorParchis.AZUL     -> Pair(0.835f, 0.165f)  // esquina superior derecha
                    ColorParchis.AMARILLO -> Pair(0.835f, 0.835f)  // esquina inferior derecha
                    ColorParchis.VERDE    -> Pair(0.165f, 0.835f)  // esquina inferior izquierda
                }

                // Offset para este id de ficha (0-3) dentro del grid 2x2
                val offset = casaOffsets[ficha.id.coerceIn(0, 3)]

                val cx = (casaCentro.first + offset.first) * boardWidth
                val cy = (casaCentro.second + offset.second) * boardHeight

                params.leftMargin = (cx - fichaSize / 2f).toInt()
                params.topMargin  = (cy - fichaSize / 2f).toInt()

            } else {
                val coords = BoardPositionMapper.getPosition(
                    ficha.posicion, boardWidth, boardHeight
                )
                params.leftMargin = (coords.first  - fichaSize / 2f).toInt()
                params.topMargin  = (coords.second - fichaSize / 2f).toInt()
            }

            view.layoutParams = params
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}