package com.example.parchis.model

abstract class Jugador(
    val nombre: String,
    val color: ColorParchis
) {
    val fichas: List<Ficha> = List(4) { i -> Ficha(i, color) }
    var fichasComidasEnEstaPartida: Int = 0
    
    abstract fun esBot(): Boolean
}

class JugadorHumano(nombre: String, color: ColorParchis) : Jugador(nombre, color) {
    override fun esBot() = false
}

class JugadorRegistrado(val usuario: UsuarioRegistrado, color: ColorParchis) : Jugador(usuario.username!!, color) {
    override fun esBot() = false
}

class Bot(nombre: String, color: ColorParchis, val dificultad: DificultadBot) : Jugador(nombre, color) {
    override fun esBot() = true
}

enum class DificultadBot {
    FACIL, MEDIA, DIFICIL
}
