package com.example.parchis.view

import android.os.Bundle
import android.util.Log
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

    private var _binding: FragmentGameModeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "GameModeFragment: onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Lifecycle", "GameModeFragment: onCreateView")
        _binding = FragmentGameModeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Lifecycle", "GameModeFragment: onViewCreated")

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

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "GameModeFragment: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "GameModeFragment: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "GameModeFragment: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "GameModeFragment: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Lifecycle", "GameModeFragment: onDestroyView")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "GameModeFragment: onDestroy")
    }
}

object ConfiguracionPartida {
    var jugadoresSeleccionados: List<Jugador> = emptyList()
}