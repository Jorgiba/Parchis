package com.example.parchis.model

abstract class Usuario(
    val email: String? = null
) {
    abstract fun esRegistrado(): Boolean
}

class UsuarioRegistrado(
    email: String,
    val estadisticas: Estadisticas = Estadisticas()
) : Usuario(email) {
    override fun esRegistrado() = true
}

class UsuarioNoRegistrado : Usuario() {
    override fun esRegistrado() = false
}