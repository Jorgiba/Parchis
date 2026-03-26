package com.example.parchis.model

data class Ficha(
    val id: Int,
    val color: ColorParchis,
    var posicion: Int = -1, // -1 significa en casa
    var estado: EstadoFicha = EstadoFicha.EN_CASA
)

enum class EstadoFicha {
    EN_CASA,
    EN_TABLERO,
    EN_META,
    FINALIZADA
}