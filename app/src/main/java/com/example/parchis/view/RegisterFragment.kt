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
import com.example.parchis.databinding.FragmentRegisterBinding
import com.example.parchis.viewmodel.RegisterResult
import com.example.parchis.viewmodel.RegisterViewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RegisterResult.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Cuenta creada con éxito para ${result.usuario.username}",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                }

                is RegisterResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }

                null -> { /* Estado inicial */
                }
            }
        }

        binding.btnBackRegister.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnRegisterSubmit.setOnClickListener {
            val username = binding.etRegisterUsername.text.toString()
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            val confirmPassword = binding.etRegisterConfirmPassword.text.toString()

            viewModel.register(username, email, password, confirmPassword)
        }

        binding.tvGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
