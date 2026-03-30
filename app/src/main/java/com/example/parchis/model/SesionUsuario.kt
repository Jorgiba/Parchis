package com.example.parchis.model

import java.util.Date

object SesionUsuario {
    var usuarioLogueado: UsuarioRegistrado? = null

    // Método para simular datos iniciales si no hay nadie logueado (para pruebas)
    fun cargarDatosPrueba() {
        val user = UsuarioRegistrado("Ejemplo")
        user.agregarPartidaAlHistorial(Partida("1", Date(), ResultadoPartida.VICTORIA, listOf("Ejemplo", "Bot1", "Bot2", "Bot3")))
        user.agregarPartidaAlHistorial(Partida("2", Date(), ResultadoPartida.DERROTA, listOf("Ejemplo", "Jugador2", "Bot1", "Bot2")))
        user.agregarPartidaAlHistorial(Partida("3", Date(), ResultadoPartida.VICTORIA, listOf("Ejemplo", "Jugador2", "Jugador3", "Jugador4")))
        user.agregarPartidaAlHistorial(Partida("4", Date(), ResultadoPartida.DERROTA, listOf("Ejemplo", "Bot1", "Bot2", "Bot3")))
        user.agregarPartidaAlHistorial(Partida("5", Date(), ResultadoPartida.VICTORIA, listOf("Ejemplo", "Bot1", "Bot2", "Bot3")))
        user.agregarPartidaAlHistorial(Partida("6", Date(), ResultadoPartida.ABANDONADA, listOf("Ejemplo", "Bot1", "Bot2", "Bot3")))
        user.agregarPartidaAlHistorial(Partida("7", Date(), ResultadoPartida.VICTORIA, listOf("Ejemplo", "Bot1", "Bot2", "Bot3")))
        user.agregarPartidaAlHistorial(Partida("8", Date(), ResultadoPartida.DERROTA, listOf("Ejemplo", "Jugador3", "Bot1", "Bot2")))
        user.agregarPartidaAlHistorial(Partida("9", Date(), ResultadoPartida.VICTORIA, listOf("Ejemplo", "Jugador2", "Bot1", "Bot3")))
        user.agregarPartidaAlHistorial(Partida("10", Date(), ResultadoPartida.DERROTA, listOf("Ejemplo", "Bot1", "Bot2", "Bot3")))
        
        // Fichas comidas manuales para la prueba
        user.fichasComidas = 65
        
        usuarioLogueado = user
    }
}
