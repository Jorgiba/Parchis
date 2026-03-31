package com.example.parchis.view

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.parchis.R
import com.example.parchis.databinding.FragmentGameModeBinding
import com.example.parchis.model.Jugador
import com.example.parchis.viewmodel.MainViewModel

class GameModeFragment : Fragment() {

    // Binding generado a partir de fragment_game_mode.xml
    private var _binding: FragmentGameModeBinding? = null
    private val binding get() = _binding!!

    // Usamos activityViewModels para compartir el MainViewModel si es necesario
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameModeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Volver atrás
        binding.btnBackMode.setOnClickListener {
            findNavController().popBackStack()
        }

        // Navegar a ajustes de partida local
        binding.btnLocalGame.setOnClickListener {
            findNavController().navigate(R.id.action_gameModeFragment_to_gameSettingsFragment)
        }

        // Opción no implementada aún
        binding.btnOnlineGame.setOnClickListener {
            Toast.makeText(requireContext(), "Modo online próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

object ConfiguracionPartida {
    var jugadoresSeleccionados: List<Jugador> = emptyList()
}