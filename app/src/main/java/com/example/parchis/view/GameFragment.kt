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
    private var numerosDibujados = false

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

        // Si el contenedor aún no tiene dimensiones, esperar al siguiente layout pass
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

                var cx = coords.first
                var cy = coords.second

                val fichasAqui = fichasEnTablero[ficha.posicion] ?: emptyList()

                if (fichasAqui.size > 1) {
                    val indice = fichasAqui.indexOf(ficha)

                    // Separación desde el centro (1/3 del tamaño de la ficha suele ir bien)
                    val offset = fichaSize / 1.5f

                    // Si es la primera ficha (-1) la movemos a la izquierda/arriba
                    // Si es la segunda ficha (1) la movemos a la derecha/abajo
                    val modificador = if (indice == 0) -1 else 1

                    val pos = ficha.posicion

                    // Comprobamos la orientación del "brazo" del tablero donde está la ficha
                    val esVertical = pos in 1..8 || pos in 61..68 ||   // Brazo inferior
                            pos in 27..34 || pos in 35..42 || // Brazo superior
                            pos in 101..108 || pos in 401..408// Pasillos rojo y amarillo

                    val esHorizontal = pos in 10..17 || pos in 18..25 || // Brazo derecho
                            pos in 44..51 || pos in 52..59 || // Brazo izquierdo
                            pos in 201..208 || pos in 301..308// Pasillos azul y verde

                    val esEsquina = pos == 9 || pos == 26 || pos == 43 || pos == 60

                    // Aplicamos el desplazamiento según la orientación
                    if (esVertical) {
                        // Mover en el eje X (izquierda y derecha dentro de la casilla)
                        cx += offset * modificador
                    } else if (esHorizontal) {
                        // Mover en el eje Y (arriba y abajo dentro de la casilla)
                        cy += offset * modificador
                    } else if (esEsquina) {
                        // Para las 4 esquinas del tablero, las dejamos en diagonal para que no se salgan
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

        // --- 🔧 CONTROL DE DISTANCIA 🔧 ---
        // offsetPrincipal: Cuánto se pega al pasillo central de color
        val offsetPrincipal = cellSize * 0.35f
        // offsetSecundario: Cuánto se pega a la otra esquina de la casilla (ej. hacia arriba)
        val offsetSecundario = cellSize * 0.35f

        for (i in 1..68) {
            val tv = android.widget.TextView(requireContext()).apply {
                text = i.toString()
                textSize = 8f
                setTextColor(android.graphics.Color.BLACK)
            }

            binding.boardContainer.addView(tv)

            val coords = BoardPositionMapper.getPosition(i, boardWidth, boardHeight)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )

            var cx = coords.first
            var cy = coords.second

            // Lógica inteligente para pegar el número siempre al pasillo de color
            when (i) {
                // --- BRAZOS VERTICALES (Amarillo y Rojo) ---
                // Columnas Derechas (1 al 8 y 26 al 33) -> El interior es la IZQUIERDA (-)
                in 1..8, in 26..33 -> {
                    cx -= offsetPrincipal
                    cy -= offsetSecundario
                }
                // Columnas Izquierdas (35 al 42 y 60 al 67) -> El interior es la DERECHA (+)
                in 35..42, in 60..67 -> {
                    cx += offsetPrincipal
                    cy -= offsetSecundario
                }

                // --- BRAZOS HORIZONTALES (Azul y Verde) ---
                // Filas Superiores (18 al 25 y 43 al 50) -> El interior es ABAJO (+)
                in 18..25, in 43..50 -> {
                    cy += offsetPrincipal
                    cx -= offsetSecundario
                }
                // Filas Inferiores (9 al 16 y 52 al 59) -> El interior es ARRIBA (-)
                in 9..16, in 52..59 -> {
                    cy -= offsetPrincipal
                    cx -= offsetSecundario
                }

                // Casillas centrales de los extremos (17, 34, 51, 68)
                else -> {
                    cx -= offsetPrincipal
                    cy -= offsetSecundario
                }
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