package com.example.parchis.view

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.parchis.R

class HomeFragment : Fragment() {
    private lateinit var btnMenu: ImageButton
    private lateinit var sideMenu: LinearLayout
    private lateinit var btnIniciarPartida: Button
    private lateinit var btnEstadisticas: Button
    private lateinit var btnCerrarSesion: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnMenu = view.findViewById(R.id.btnMenu)
        btnIniciarPartida = view.findViewById(R.id.btnStartGame)
        btnEstadisticas = view.findViewById(R.id.btnStats)
        btnCerrarSesion = view.findViewById(R.id.btnLogout)
        sideMenu = view.findViewById(R.id.sideMenu)

        btnMenu.setOnClickListener {
            sideMenu.isVisible = !sideMenu.isVisible
        }

        btnIniciarPartida.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_gameModeFragment)
        }

        btnEstadisticas.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_statsFragment)
            sideMenu.visibility = View.GONE
        }

        btnCerrarSesion.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }
    }
}