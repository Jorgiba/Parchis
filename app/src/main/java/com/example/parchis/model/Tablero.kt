package com.example.parchis.model

class Tablero {
    // Parchís standard board has 68 tiles in the common circuit
    val casillasComunes = List(68) { i -> Casilla(i + 1) }

    // Corridors for each color (usually 7 tiles + goal)
    val pasillos = mapOf(
        ColorParchis.ROJO to List(8) { i -> Casilla(100 + i, ColorParchis.ROJO) },
        ColorParchis.AZUL to List(8) { i -> Casilla(200 + i, ColorParchis.AZUL) },
        ColorParchis.VERDE to List(8) { i -> Casilla(300 + i, ColorParchis.VERDE) },
        ColorParchis.AMARILLO to List(8) { i -> Casilla(400 + i, ColorParchis.AMARILLO) }
    )

    companion object {
        // Safe tiles (Seguros) in a standard 68-tile board
        val SEGUROS = listOf(5, 12, 17, 22, 29, 34, 39, 46, 51, 56, 63, 68)
        
        // Start positions (Salidas)
        val SALIDAS = mapOf(
            ColorParchis.AMARILLO to 5,
            ColorParchis.AZUL to 22,
            ColorParchis.ROJO to 39,
            ColorParchis.VERDE to 56
        )
        
        // Entrance to corridors
        val ENTRADAS_PASILLO = mapOf(
            ColorParchis.AMARILLO to 68,
            ColorParchis.AZUL to 17,
            ColorParchis.ROJO to 34,
            ColorParchis.VERDE to 51
        )
    }
}

data class Casilla(
    val id: Int,
    val colorMeta: ColorParchis? = null,
    val fichas: MutableList<Ficha> = mutableListOf()
) {
    fun esSeguro(): Boolean = Tablero.SEGUROS.contains(id) || colorMeta != null
    fun estaLlena(): Boolean = fichas.size >= 2
}