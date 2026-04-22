package com.example.parchis.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.parchis.R
import com.example.parchis.databinding.FragmentStatsBinding
import com.example.parchis.model.ResultadoPartida
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "StatsFragment: onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Lifecycle", "StatsFragment: onCreateView")
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Lifecycle", "StatsFragment: onViewCreated")

        if (SesionUsuario.usuarioLogueado == null) {
            SesionUsuario.cargarDatosPrueba()
        }

        binding.btnBackStats.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnShareStats.setOnClickListener {
            val victorias = SesionUsuario.usuarioLogueado?.victorias ?: 0
            val shareText = getString(R.string.share_message, victorias)
            
            // Ejemplo de Intent Explícito para abrir una hipotética DetailActivity o similar
            // val intent = Intent(requireContext(), SettingsActivity::class.java)
            // startActivity(intent)

            // Intent Implícito (Compartir)
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, getString(R.string.choose_share))
            startActivity(shareIntent)
        }

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

        // Rellenar cabecera con strings formateados (evitando hardcoding)
        binding.tvGamesPlayed.text = "Partidas jugadas: ${usuario.partidasJugadas}"
        binding.tvWins.text = "Victorias: ${usuario.victorias}"
        binding.tvLosses.text = "Derrotas: ${usuario.derrotas}"
        binding.tvPiecesEaten.text = "Fichas comidas: ${usuario.fichasComidas}"

        // Limpiar contenedor de historial
        binding.containerHistory.removeAllViews()

        if (usuario.historialPartidas.isEmpty()) {
            val emptyTv = TextView(requireContext())
            emptyTv.text = "No hay partidas registradas"
            binding.containerHistory.addView(emptyTv)
        } else {
            // Recorrer partidas (de más reciente a más antigua)
            usuario.historialPartidas.reversed().forEach { partida ->
                val itemView = LayoutInflater.from(requireContext()).inflate(android.R.layout.simple_list_item_2, null)
                val text1 = itemView.findViewById<TextView>(android.R.id.text1)
                val text2 = itemView.findViewById<TextView>(android.R.id.text2)

                val resultadoStr = when(partida.resultado) {
                    ResultadoPartida.VICTORIA -> "🏆 VICTORIA"
                    ResultadoPartida.DERROTA -> "❌ DERROTA"
                    ResultadoPartida.ABANDONADA -> "🏳️ ABANDONADA"
                }

                text1.text = "$resultadoStr - ${dateFormat.format(partida.fecha)}"
                text1.setTextColor(if(partida.resultado == ResultadoPartida.VICTORIA)
                    ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
                else ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))

                text2.text = "Jugadores: ${partida.jugadores.joinToString(", ")}\nModo: Local"

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 24)
                itemView.layoutParams = params

                binding.containerHistory.addView(itemView)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "StatsFragment: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "StatsFragment: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "StatsFragment: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "StatsFragment: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Lifecycle", "StatsFragment: onDestroyView")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "StatsFragment: onDestroy")
    }
}
