package com.example.parchis.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.parchis.R
import com.example.parchis.databinding.ActivityMainBinding
import com.example.parchis.viewmodel.MainViewModel

class MainFragment : Fragment() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "MainFragment: onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Lifecycle", "MainFragment: onCreateView")
        _binding = ActivityMainBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Lifecycle", "MainFragment: onViewCreated")

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }

        binding.btnGuest.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_gameSettingsFragment)
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_registerFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "MainFragment: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "MainFragment: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "MainFragment: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "MainFragment: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Lifecycle", "MainFragment: onDestroyView")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "MainFragment: onDestroy")
    }
}
