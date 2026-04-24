package com.example.parchis.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

abstract class Usuario {
    abstract fun esRegistrado(): Boolean
}

@Entity(tableName = "usuarios")
data class UsuarioRegistrado(
    @PrimaryKey val username: String,
    var email: String = "", // Nuevo campo para vincular con Firebase
    var partidasJugadas: Int = 0,
    var victorias: Int = 0,
    var derrotas: Int = 0,
    var fichasComidas: Int = 0
) : Usuario() {
    
    @Ignore
    var historialPartidas: MutableList<Partida> = mutableListOf()

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
