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
    private var numerosDibujados = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            
            // Cambio de color del dado según el turno
            val colorRes = when (jugador.color) {
                ColorParchis.ROJO -> R.color.red
                ColorParchis.AZUL -> R.color.blue
                ColorParchis.VERDE -> R.color.green
                ColorParchis.AMARILLO -> R.color.yellow
            }
            binding.diceActionArea.setBackgroundColor(ContextCompat.getColor(requireContext(), colorRes))
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
        if (SesionUsuario.usuarioLogueado != null) {
            viewModel.abandonarPartida()
        }
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
                    elevation = 10f
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

        if (boardWidth == 0 || boardHeight == 0) {
            binding.boardContainer.post { actualizarPosicionesFichas() }
            return
        }

        dibujarNumerosCasillas(boardWidth, boardHeight)

        val density = resources.displayMetrics.density
        val fichaSize = (15 * density).toInt()

        val fichasEnTablero = fichaViews.keys
            .filter { it.estado == EstadoFicha.EN_TABLERO || it.estado == EstadoFicha.EN_META }
            .groupBy { it.posicion }

        val casaOffsets = listOf(
            Pair(-0.04f, -0.04f),
            Pair( 0.04f, -0.04f),
            Pair(-0.04f,  0.04f),
            Pair( 0.04f,  0.04f)
        )

        fichaViews.forEach { (ficha, view) ->
            val params = FrameLayout.LayoutParams(fichaSize, fichaSize)

            if (ficha.estado == EstadoFicha.EN_CASA) {
                val casaCentro = when (ficha.color) {
                    ColorParchis.ROJO     -> Pair(0.165f, 0.165f)
                    ColorParchis.AZUL     -> Pair(0.835f, 0.165f)
                    ColorParchis.AMARILLO -> Pair(0.835f, 0.835f)
                    ColorParchis.VERDE    -> Pair(0.165f, 0.835f)
                }

                val offset = casaOffsets[ficha.id.coerceIn(0, 3)]
                val cx = (casaCentro.first + offset.first) * boardWidth
                val cy = (casaCentro.second + offset.second) * boardHeight

                params.leftMargin = (cx - fichaSize / 2f).toInt()
                params.topMargin  = (cy - fichaSize / 2f).toInt()

            } else {
                val coords = BoardPositionMapper.getPosition(ficha.posicion, boardWidth, boardHeight)
                var cx = coords.first
                var cy = coords.second

                val fichasAqui = fichasEnTablero[ficha.posicion] ?: emptyList()

                if (fichasAqui.size > 1) {
                    val indice = fichasAqui.indexOf(ficha)
                    val offset = fichaSize / 1.5f
                    val modificador = if (indice == 0) -1 else 1
                    val pos = ficha.posicion

                    val esVertical = pos in 1..8 || pos in 61..68 || pos in 27..34 || pos in 35..42 || pos in 101..108 || pos in 401..408
                    val esHorizontal = pos in 10..17 || pos in 18..25 || pos in 44..51 || pos in 52..59 || pos in 201..208 || pos in 301..308
                    val esEsquina = pos == 9 || pos == 26 || pos == 43 || pos == 60

                    if (esVertical) cx += offset * modificador
                    else if (esHorizontal) cy += offset * modificador
                    else if (esEsquina) {
                        cx += offset * modificador
                        cy += offset * modificador
                    }
                }

                params.leftMargin = (cx - fichaSize / 2f).toInt()
                params.topMargin  = (cy - fichaSize / 2f).toInt()
            }

            view.layoutParams = params
        }
    }

    private fun dibujarNumerosCasillas(boardWidth: Int, boardHeight: Int) {
        if (numerosDibujados) return
        val boardSize = minOf(boardWidth, boardHeight)
        val cellSize = boardSize / 19f
        val offsetPrincipal = cellSize * 0.35f
        val offsetSecundario = cellSize * 0.35f

        for (i in 1..68) {
            val tv = android.widget.TextView(requireContext()).apply {
                text = i.toString()
                textSize = 8f
                setTextColor(android.graphics.Color.BLACK)
            }
            binding.boardContainer.addView(tv)
            val coords = BoardPositionMapper.getPosition(i, boardWidth, boardHeight)
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            var cx = coords.first
            var cy = coords.second

            when (i) {
                in 1..8, in 26..33 -> { cx -= offsetPrincipal; cy -= offsetSecundario }
                in 35..42, in 60..67 -> { cx += offsetPrincipal; cy -= offsetSecundario }
                in 18..25, in 43..50 -> { cy += offsetPrincipal; cx -= offsetSecundario }
                in 9..16, in 52..59 -> { cy -= offsetPrincipal; cx -= offsetSecundario }
                else -> { cx -= offsetPrincipal; cy -= offsetSecundario }
            }

            params.leftMargin = cx.toInt()
            params.topMargin = cy.toInt()
            tv.layoutParams = params
        }
        numerosDibujados = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
