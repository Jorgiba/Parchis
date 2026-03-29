package com.example.parchis.model

abstract class Usuario(
    val username: String? = null
) {
    abstract fun esRegistrado(): Boolean
}

class UsuarioRegistrado(
    username: String,
    var partidasJugadas: Int = 0,
    var victorias: Int = 0,
    var derrotas: Int = 0,
    var fichasComidas: Int = 0,
    val historialPartidas: MutableList<Partida> = mutableListOf()
) : Usuario(username) {
    override fun esRegistrado() = true

    fun agregarPartidaAlHistorial(partida: Partida) {
        historialPartidas.add(partida)
        actualizarEstadisticas(partida)
    }

    private fun actualizarEstadisticas(partida: Partida) {
        this.partidasJugadas++
        when (partida.resultado) {
            ResultadoPartida.VICTORIA -> this.victorias++
            ResultadoPartida.DERROTA -> this.derrotas++
            ResultadoPartida.ABANDONADA -> { this.derrotas++ }
        }
    }
}

class UsuarioNoRegistrado : Usuario() {
    override fun esRegistrado() = false
}