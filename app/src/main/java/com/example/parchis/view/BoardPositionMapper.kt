package com.example.parchis.view

import com.example.parchis.model.ColorParchis

object BoardPositionMapper {
    private const val BOARD_SIZE_DP = 360f
    
    // Mapeo de IDs de casillas a coordenadas relativas (0.0 a 1.0)
    // Esto es un ejemplo simplificado de algunas posiciones clave
    private val coords = mutableMapOf<Int, Pair<Float, Float>>()

    init {
        // --- CASAS (Especiales) ---
        // Se calculan dinámicamente en la Activity para las 4 fichas
        
        // --- SALIDAS ---
        coords[5] = Pair(0.42f, 0.85f)   // Salida Amarilla
        coords[22] = Pair(0.15f, 0.42f)  // Salida Azul
        coords[39] = Pair(0.42f, 0.15f)  // Salida Roja
        coords[56] = Pair(0.85f, 0.42f)  // Salida Verde

        // --- ALGUNAS CASILLAS COMUNES (Ejemplo de recorrido) ---
        // Aquí deberías añadir las 68 coordenadas exactas del tablero
        for (i in 1..68) {
            if (!coords.containsKey(i)) {
                // Generamos una espiral o camino temporal si no está definida
                coords[i] = calculateTempCoords(i)
            }
        }
        
        // --- METAS ---
        coords[108] = Pair(0.5f, 0.7f) // Meta Roja (dentro del pasillo)
        coords[208] = Pair(0.3f, 0.5f) // Meta Azul
        coords[308] = Pair(0.5f, 0.3f) // Meta Verde
        coords[408] = Pair(0.7f, 0.5f) // Meta Amarilla
    }

    private fun calculateTempCoords(id: Int): Pair<Float, Float> {
        // Lógica temporal para que las fichas se muevan en círculo mientras no tengas las 68 reales
        val angle = Math.toRadians((id * (360.0 / 68.0)))
        val radius = 0.35f
        return Pair(
            0.5f + (radius * Math.cos(angle)).toFloat(),
            0.5f + (radius * Math.sin(angle)).toFloat()
        )
    }

    fun getPosition(id: Int, boardWidth: Int, boardHeight: Int): Pair<Float, Float> {
        val relativePos = coords[id] ?: Pair(0.5f, 0.5f)
        return Pair(relativePos.first * boardWidth, relativePos.second * boardHeight)
    }
}