package com.example.parchis.view

import android.os.Bundle
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

    // El objeto de binding se genera automáticamente a partir del nombre del layout XML
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 1. Inflamos la vista usando DataBinding
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // 2. Vinculamos el ViewModel y el LifecycleOwner al binding
        // Esto permite que el XML reaccione a cambios de LiveData si los usas allí
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observamos los cambios en el resultado del login
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(requireContext(), "Bienvenido, ${result.usuario.username}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                is LoginResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                null -> { /* Estado inicial */ }
            }
        }

        // ACCESO A VISTAS: Usamos binding.id_de_la_vista (en camelCase)
        binding.btnBackLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnLoginSubmit.setOnClickListener {
            val username = binding.etUsernameLogin.text.toString()
            val password = binding.etPasswordLogin.text.toString()

            // Llamamos a la lógica del ViewModel
            viewModel.login(username, password)
        }

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidad no implementada", Toast.LENGTH_SHORT).show()
        }
    }

    // Es fundamental limpiar el binding al destruir la vista para evitar fugas de memoria
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}