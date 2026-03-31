package com.example.parchis.view

import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMenu.setOnClickListener {
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
            findNavController().navigate(R.id.mainFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
