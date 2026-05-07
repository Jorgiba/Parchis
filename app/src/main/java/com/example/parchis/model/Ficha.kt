package com.example.parchis.model

data class Ficha(
    val id: Int,
    val color: ColorParchis,
    var posicion: Int = -1,
    var estado: EstadoFicha = EstadoFicha.EN_CASA
)

enum class ColorParchis {
    ROJO,
    AZUL,
    VERDE,
    AMARILLO
}
enum class EstadoFicha {
    EN_CASA,
    EN_TABLERO,
    EN_META,
    FINALIZADA
}