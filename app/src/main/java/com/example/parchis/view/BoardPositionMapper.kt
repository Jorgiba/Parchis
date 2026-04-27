package com.example.parchis.view

object BoardPositionMapper {

    private val coords = mutableMapOf<Int, Pair<Float, Float>>()

    init {
        for (i in 1..68) {
            coords[i] = calculateCommonPath(i)
        }

        // --- PASILLOS (Centro exacto 0.5f) ---
        // Rojo (101-108) - Brazo Superior
        for (i in 1..7) coords[100 + i] = Pair(0.5f, 0.44f - (i * 0.052f))
        coords[108] = Pair(0.5f, 0.42f)

        // Azul (201-208) - Brazo Derecho
        for (i in 1..7) coords[200 + i] = Pair(0.56f + (i * 0.052f), 0.5f)
        coords[208] = Pair(0.58f, 0.5f)

        // Verde (301-308) - Brazo Izquierdo
        for (i in 1..7) coords[300 + i] = Pair(0.44f - (i * 0.052f), 0.5f)
        coords[308] = Pair(0.42f, 0.5f)

        // Amarillo (401-408) - Brazo Inferior
        for (i in 1..7) coords[400 + i] = Pair(0.5f, 0.56f + (i * 0.052f))
        coords[408] = Pair(0.5f, 0.58f)
    }

    private fun calculateCommonPath(id: Int): Pair<Float, Float> {
        return when (id) {
            // Lado Amarillo (Salida 5) - Brazo Inferior Derecho
            in 1..8 -> Pair(0.56f, 0.60f + (id - 1) * 0.052f)
            9 -> Pair(0.92f, 0.56f) // Esquina Inferior Derecha

            // Lado Azul (Salida 22) - Brazo Derecho
            in 10..16 -> Pair(0.92f - (id - 10) * 0.052f, 0.56f)
            17 -> Pair(0.92f, 0.5f) // Entrada Azul
            in 18..25 -> Pair(0.92f - (id - 18) * 0.052f, 0.44f)
            26 -> Pair(0.56f, 0.08f) // Esquina Superior Derecha

            // Lado Rojo (Salida 39) - Brazo Superior
            in 27..33 -> Pair(0.56f, 0.40f - (id - 27) * 0.052f)
            34 -> Pair(0.5f, 0.08f) // Entrada Rojo
            in 35..42 -> Pair(0.44f, 0.08f + (id - 35) * 0.052f)
            43 -> Pair(0.08f, 0.44f) // Esquina Superior Izquierda

            // Lado Verde (Salida 56) - Brazo Izquierdo
            in 44..50 -> Pair(0.08f + (id - 44) * 0.052f, 0.44f)
            51 -> Pair(0.08f, 0.5f) // Entrada Verde
            in 52..59 -> Pair(0.08f + (id - 52) * 0.052f, 0.56f)
            60 -> Pair(0.44f, 0.92f) // Esquina Inferior Izquierda

            // Vuelta al Amarillo
            in 61..67 -> Pair(0.44f, 0.92f - (id - 61) * 0.052f)
            68 -> Pair(0.5f, 0.92f) // Entrada Amarillo

            else -> Pair(0.5f, 0.5f)
        }
    }

    fun getPosition(id: Int, boardWidth: Int, boardHeight: Int): Pair<Float, Float> {
        val relativePos = coords[id] ?: Pair(0.5f, 0.5f)
        return Pair(relativePos.first * boardWidth, relativePos.second * boardHeight)
    }
}