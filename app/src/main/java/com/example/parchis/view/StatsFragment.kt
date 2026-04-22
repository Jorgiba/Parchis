package com.example.parchis.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.parchis.R
import com.example.parchis.databinding.FragmentStatsBinding
import com.example.parchis.model.SesionUsuario
import com.example.parchis.model.UsuarioRegistrado
import com.example.parchis.viewmodel.StatsResult
import com.example.parchis.viewmodel.StatsViewModel

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
                    setupRecyclerView(result.usuario)
                }
                is StatsResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                null -> { /* Estado inicial */ }
            }
        }

        viewModel.loadUserStats()
    }

    private fun setupRecyclerView(usuario: UsuarioRegistrado) {
        // Implementación del RecyclerView (Punto 7 y 11 del temario)
        // Sustituimos la carga manual de vistas por un Adapter profesional
        val adapter = HistoryAdapter(usuario.historialPartidas.reversed())
        binding.rvHistory.adapter = adapter
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
