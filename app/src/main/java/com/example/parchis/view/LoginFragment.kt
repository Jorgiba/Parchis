package com.example.parchis.view

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
import com.example.parchis.databinding.FragmentLoginBinding
import com.example.parchis.viewmodel.LoginResult
import com.example.parchis.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "LoginFragment: onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Lifecycle", "LoginFragment: onCreateView")
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Lifecycle", "LoginFragment: onViewCreated")

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(requireContext(), getString(R.string.iniciar_sesi_n), Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                is LoginResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                null -> { /* Estado inicial */ }
            }
        }

        binding.btnBackLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        // El onClick del botón se maneja ahora vía DataBinding en el XML,
        // pero mantenemos los listeners manuales si fuera necesario para otros eventos.

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidad no implementada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "LoginFragment: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "LoginFragment: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "LoginFragment: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "LoginFragment: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Lifecycle", "LoginFragment: onDestroyView")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "LoginFragment: onDestroy")
    }
}