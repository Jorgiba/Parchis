package com.example.parchis.model

import java.util.Date

object SesionUsuario {
    var usuarioLogueado: UsuarioRegistrado? = null

    fun cargarDatosPrueba() {
        val username = "Ejemplo"
        val user = UsuarioRegistrado(username)
        user.agregarPartidaAlHistorial(Partida("1", Date(), ResultadoPartida.VICTORIA, listOf(username, "Bot1", "Bot2", "Bot3"), username))
        user.agregarPartidaAlHistorial(Partida("2", Date(), ResultadoPartida.DERROTA, listOf(username, "Jugador2", "Bot1", "Bot2"), username))
        user.agregarPartidaAlHistorial(Partida("3", Date(), ResultadoPartida.VICTORIA, listOf(username, "Jugador2", "Jugador3", "Jugador4"), username))
        user.agregarPartidaAlHistorial(Partida("4", Date(), ResultadoPartida.DERROTA, listOf(username, "Bot1", "Bot2", "Bot3"), username))
        user.agregarPartidaAlHistorial(Partida("5", Date(), ResultadoPartida.VICTORIA, listOf(username, "Bot1", "Bot2", "Bot3"), username))
        user.agregarPartidaAlHistorial(Partida("6", Date(), ResultadoPartida.ABANDONADA, listOf(username, "Bot1", "Bot2", "Bot3"), username))
        user.agregarPartidaAlHistorial(Partida("7", Date(), ResultadoPartida.VICTORIA, listOf(username, "Bot1", "Bot2", "Bot3"), username))
        user.agregarPartidaAlHistorial(Partida("8", Date(), ResultadoPartida.DERROTA, listOf(username, "Jugador3", "Bot1", "Bot2"), username))
        user.agregarPartidaAlHistorial(Partida("9", Date(), ResultadoPartida.VICTORIA, listOf(username, "Jugador2", "Bot1", "Bot3"), username))
        user.agregarPartidaAlHistorial(Partida("10", Date(), ResultadoPartida.DERROTA, listOf(username, "Bot1", "Bot2", "Bot3"), username))
        
        user.fichasComidas = 65
        
        usuarioLogueado = user
    }
}
