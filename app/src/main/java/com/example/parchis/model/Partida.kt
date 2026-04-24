package com.example.parchis.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "partidas")
data class Partida(
    @PrimaryKey val id: String,
    val fecha: Date,
    val resultado: ResultadoPartida,
    val jugadores: List<String>,
    val usernameUsuario : String
)

enum class ResultadoPartida {
    VICTORIA, DERROTA, ABANDONADA
}