package com.example.parchis.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.parchis.R
import com.example.parchis.databinding.FragmentGameSettingsBinding
import com.example.parchis.model.DificultadBot
import com.example.parchis.model.Jugador
import com.example.parchis.model.ParchisGame
import com.example.parchis.model.SesionUsuario
import com.example.parchis.viewmodel.MainViewModel

class GameSettingsFragment : Fragment() {
    private var _binding: FragmentGameSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameSettingsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Valores por defecto
        binding.rgPlayers.check(binding.rb4.id)
        binding.rgDifficulty.check(binding.rbMedium.id)

        binding.btnBackSettings.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnStartLocal.setOnClickListener {
            val numHumanos = when (binding.rgPlayers.checkedRadioButtonId) {
                binding.rb1.id -> 1
                binding.rb2.id -> 2
                binding.rb3.id -> 3
                binding.rb4.id -> 4
                else -> 4
            }

            val dificultad = when (binding.rgDifficulty.checkedRadioButtonId) {
                binding.rbEasy.id -> DificultadBot.FACIL
                binding.rbMedium.id -> DificultadBot.MEDIA
                binding.rbHard.id -> DificultadBot.DIFICIL
                else -> DificultadBot.MEDIA
            }

            iniciarJuego(numHumanos, dificultad)
        }
    }

    private fun iniciarJuego(numHumanos: Int, dificultad: DificultadBot) {
        val jugadores = ParchisGame.crearPartidaLocal(
            numHumanos, 
            dificultad, 
            SesionUsuario.usuarioLogueado
        )

        ConfiguracionPartida.jugadoresSeleccionados = jugadores
        
        findNavController().navigate(R.id.action_gameSettingsFragment_to_gameFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
