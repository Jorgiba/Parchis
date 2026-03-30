package com.example.parchis.model

import java.util.Date

data class Partida(
    val id: String,
    val fecha: Date,
    val resultado: ResultadoPartida,
    val jugadores: List<String>
)

enum class ResultadoPartida {
    VICTORIA, DERROTA, ABANDONADA
}