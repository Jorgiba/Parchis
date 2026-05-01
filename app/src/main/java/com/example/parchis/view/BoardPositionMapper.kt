package com.example.parchis.view

object BoardPositionMapper {

    private const val GRID_SIZE = 19
    private const val S = 1.0f / GRID_SIZE
    private const val C0 = S / 2.0f

    private val coords = mutableMapOf<Int, Pair<Float, Float>>()

    init {
        // --- 🔧 RUEDAS DE CALIBRACIÓN 🔧 ---
        // Ahora sí, si bajas el factorPaso (ej: 0.95f), las casillas se aprietan
        // pero gracias al anclaje, la casilla de salida NO se mueve.
        val factorPaso = 0.96f

        fun pos(index: Int): Float {
            val distanciaAlCentro = index - 9
            return 0.5f + (distanciaAlCentro * S * factorPaso)
        }

        // Las salidas (ej: 5, 22, 39, 56) están exactamente a 5 casillas del centro.
        val perdidaEnSalida = 5 * S * (1.0f - factorPaso)

        val dSide = 0.06f
        val dOut = 0.025f + perdidaEnSalida

        // --- CIRCUITO COMÚN (1-68) EXACTO A TU IMAGEN ---

        // BRAZO INFERIOR (Amarillo)
        // Columna izquierda (8 casillas): 60 a 67
        for (i in 0..7) coords[60 + i] = Pair(pos(8) - dSide, pos(11 + i) + dOut)
        // Seguro del borde (Tapón central): 68
        coords[68] = Pair(pos(9), pos(18) + dOut)
        // Columna derecha (8 casillas): 1 a 8
        for (i in 0..7) coords[1 + i] = Pair(pos(10) + dSide, pos(18 - i) + dOut)

        // BRAZO DERECHO (Azul)
        // Fila inferior (8 casillas): 9 a 16
        for (i in 0..7) coords[9 + i] = Pair(pos(11 + i) + dOut, pos(10) + dSide)
        // Seguro del borde (Tapón central): 17
        coords[17] = Pair(pos(18) + dOut, pos(9))
        // Fila superior (8 casillas): 18 a 25
        for (i in 0..7) coords[18 + i] = Pair(pos(18 - i) + dOut, pos(8) - dSide)

        // BRAZO SUPERIOR (Rojo)
        // Columna derecha (8 casillas): 26 a 33
        for (i in 0..7) coords[26 + i] = Pair(pos(10) + dSide, pos(7 - i) - dOut)
        // Seguro del borde (Tapón central): 34 (El que rodeaste en la imagen)
        coords[34] = Pair(pos(9), pos(0) - dOut)
        // Columna izquierda (8 casillas): 35 a 42
        for (i in 0..7) coords[35 + i] = Pair(pos(8) - dSide, pos(0 + i) - dOut)

        // BRAZO IZQUIERDO (Verde)
        // Fila superior (8 casillas): 43 a 50
        for (i in 0..7) coords[43 + i] = Pair(pos(7 - i) - dOut, pos(8) - dSide)
        // Seguro del borde (Tapón central): 51
        coords[51] = Pair(pos(0) - dOut, pos(9))
        // Fila inferior (8 casillas): 52 a 59
        for (i in 0..7) coords[52 + i] = Pair(pos(0 + i) - dOut, pos(10) + dSide)


        // --- PASILLOS DE META ---

        // Rojo (101-108)
        for (i in 0..7) coords[101 + i] = Pair(pos(9), pos(1 + i) - dOut)

        // Azul (201-208)
        for (i in 0..7) coords[201 + i] = Pair(pos(17 - i) + dOut, pos(9))

        // Verde (301-308)
        for (i in 0..7) coords[301 + i] = Pair(pos(1 + i) - dOut, pos(9))

        // Amarillo (401-408)
        for (i in 0..7) coords[401 + i] = Pair(pos(9), pos(17 - i) + dOut)
    }

    fun getPosition(id: Int, boardWidth: Int, boardHeight: Int): Pair<Float, Float> {
        val relativePos = coords[id] ?: Pair(0.5f, 0.5f)

        val boardSize = minOf(boardWidth, boardHeight)
        val offsetX = (boardWidth - boardSize) / 2f
        val offsetY = (boardHeight - boardSize) / 2f

        val margenBorde = 0.02f

        val usableSize = boardSize * (1.0f - 2 * margenBorde)
        val finalOffsetX = offsetX + boardSize * margenBorde
        val finalOffsetY = offsetY + boardSize * margenBorde

        val px = finalOffsetX + (relativePos.first * usableSize)
        val py = finalOffsetY + (relativePos.second * usableSize)

        return Pair(px, py)
    }
}