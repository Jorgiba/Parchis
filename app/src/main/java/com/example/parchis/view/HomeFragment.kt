package com.example.parchis.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.parchis.R
import com.example.parchis.databinding.FragmentHomeBinding
import com.example.parchis.viewmodel.MainViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "HomeFragment: onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Lifecycle", "HomeFragment: onCreateView")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Lifecycle", "HomeFragment: onViewCreated")

        binding.btnMenu.setOnClickListener {
            // Uso de propiedad de extensión .isVisible para mayor legibilidad
            binding.sideMenu.isVisible = !binding.sideMenu.isVisible
        }

        binding.btnStartGame.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_gameModeFragment)
        }

        binding.btnStats.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_statsFragment)
            binding.sideMenu.visibility = View.GONE
        }

        binding.btnLogout.setOnClickListener {
            // Navegación de vuelta al inicio de la app
            findNavController().navigate(R.id.mainFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "HomeFragment: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "HomeFragment: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "HomeFragment: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "HomeFragment: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Lifecycle", "HomeFragment: onDestroyView")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "HomeFragment: onDestroy")
    }
}
