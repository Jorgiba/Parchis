package com.example.parchis.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.parchis.databinding.FragmentStatsBinding
import com.example.parchis.model.SesionUsuario
import com.example.parchis.model.UsuarioRegistrado
import com.example.parchis.viewmodel.StatsResult
import com.example.parchis.viewmodel.StatsViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cargamos datos de prueba si no hay sesión para evitar errores visuales
        if (SesionUsuario.usuarioLogueado == null) {
            SesionUsuario.cargarDatosPrueba()
        }

        binding.btnBackStats.setOnClickListener {
            findNavController().popBackStack()
        }

        // Lógica del Intent Implícito para compartir estadísticas
        binding.btnShareStats.setOnClickListener {
            val victorias = SesionUsuario.usuarioLogueado?.victorias ?: 0
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "¡Mira mis estadísticas en el Parchís! He ganado $victorias partidas. ¿Te animas a jugar?")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Compartir mis logros")
            startActivity(shareIntent)
        }

        // Observamos los resultados del ViewModel
        viewModel.statsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is StatsResult.Success -> {
                    mostrarEstadisticas(result.usuario)
                }
                is StatsResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                null -> { /* Estado inicial */ }
            }
        }

        viewModel.loadUserStats()
    }

    private fun mostrarEstadisticas(usuario: UsuarioRegistrado) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        binding.tvGamesPlayed.text = "Partidas jugadas: ${usuario.partidasJugadas}"
        binding.tvWins.text = "Victorias: ${usuario.victorias}"
        binding.tvLosses.text = "Derrotas: ${usuario.derrotas}"
        binding.tvPiecesEaten.text = "Fichas comidas: ${usuario.fichasComidas}"

        if (usuario.historialPartidas.isEmpty()) {
            binding.tvLastGamesList.text = "No hay partidas registradas"
        } else {
            val historialTexto = usuario.historialPartidas.reversed().joinToString("\n\n") { partida ->
                val resultadoTexto = if (partida.resultado.name == "VICTORIA") "Victoria" else "Derrota"
                val fechaFormateada = dateFormat.format(partida.fecha)
                val nombresJugadores = partida.jugadores.joinToString(", ")

                "$resultadoTexto ($fechaFormateada)\nJugadores: $nombresJugadores"
            }
            binding.tvLastGamesList.text = historialTexto
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}