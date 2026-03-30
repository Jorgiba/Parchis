package com.example.parchis.model

import java.util.Date

object SesionUsuario {
    var usuarioLogueado: UsuarioRegistrado? = null

    // Método para simular datos iniciales si no hay nadie logueado (para pruebas)
    fun cargarDatosPrueba() {
        val user = UsuarioRegistrado("Marco")
        user.agregarPartidaAlHistorial(Partida("1", Date(), ResultadoPartida.VICTORIA, listOf("Marco", "Bot1", "Bot2", "Bot3")))
        user.agregarPartidaAlHistorial(Partida("2", Date(), ResultadoPartida.DERROTA, listOf("Marco", "Jugador2", "Bot1", "Bot2")))
        user.agregarPartidaAlHistorial(Partida("3", Date(), ResultadoPartida.VICTORIA, listOf("Marco", "Jugador2", "Jugador3", "Jugador4")))
        
        // Fichas comidas manuales para la prueba
        user.fichasComidas = 24
        
        usuarioLogueado = user
    }
}