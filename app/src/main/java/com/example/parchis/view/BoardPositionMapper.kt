package com.example.parchis.view

import com.example.parchis.model.ColorParchis

object BoardPositionMapper {
    private const val BOARD_SIZE_DP = 360f
    
    private val coords = mutableMapOf<Int, Pair<Float, Float>>()

    init {

        coords[5] = Pair(0.42f, 0.85f)
        coords[22] = Pair(0.15f, 0.42f)
        coords[39] = Pair(0.42f, 0.15f)
        coords[56] = Pair(0.85f, 0.42f)

        for (i in 1..68) {
            if (!coords.containsKey(i)) {
                coords[i] = calculateTempCoords(i)
            }
        }
        
        // --- METAS ---
        coords[108] = Pair(0.5f, 0.7f)
        coords[208] = Pair(0.3f, 0.5f)
        coords[308] = Pair(0.5f, 0.3f)
        coords[408] = Pair(0.7f, 0.5f)
    }

    private fun calculateTempCoords(id: Int): Pair<Float, Float> {
        return Pair(0f, 0f)
    }

    fun getPosition(id: Int, boardWidth: Int, boardHeight: Int): Pair<Float, Float> {
        val relativePos = coords[id] ?: Pair(0.5f, 0.5f)
        return Pair(relativePos.first * boardWidth, relativePos.second * boardHeight)
    }
}