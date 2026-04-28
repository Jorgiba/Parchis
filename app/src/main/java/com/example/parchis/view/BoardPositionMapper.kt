package com.example.parchis.view

object BoardPositionMapper {

    private const val GRID_SIZE = 19
    private const val S = 1.0f / GRID_SIZE
    private const val C0 = S / 2.0f

    private val coords = mutableMapOf<Int, Pair<Float, Float>>()

    init {
        fun pos(index: Int): Float = C0 + index * S

        val dSide = 0.06f
        val dOut = 0.025f

        // --- CIRCUITO COMÚN (1-68) ---

        // BRAZO INFERIOR (Amarillo)
        for (i in 1..8) coords[i] = Pair(pos(10) + dSide, pos(19 - i) + dOut) // Columna derecha (Salida Amarilla)
        for (i in 1..8) coords[60 + i] = Pair(pos(8) - dSide, pos(10 + i) + dOut) // Columna izquierda bajando

        // BRAZO DERECHO (Azul)
        for (i in 1..8) coords[9 + i] = Pair(pos(10 + i) + dOut, pos(10) + dSide) // Fila inferior
        for (i in 1..8) coords[17 + i] = Pair(pos(19 - i) + dOut, pos(8) - dSide) // Fila superior (Salida Azul)

        // BRAZO SUPERIOR (Rojo)
        for (i in 1..8) coords[26 + i] = Pair(pos(10) + dSide, pos(8 - i) - dOut) // Columna derecha subiendo
        for (i in 1..8) coords[34 + i] = Pair(pos(8) - dSide, pos(i - 1) - dOut) // Columna izquierda (Salida Roja)

        // BRAZO IZQUIERDO (Verde)
        for (i in 1..8) coords[43 + i] = Pair(pos(8 - i) - dOut, pos(8) - dSide) // Fila superior yendo a izq
        for (i in 1..8) coords[51 + i] = Pair(pos(i - 1) - dOut, pos(10) + dSide) // Fila inferior (Salida Verde)

        // ESQUINAS INTERIORES
        coords[9] = Pair(pos(10) + dSide, pos(10) + dSide)
        coords[26] = Pair(pos(10) + dSide, pos(8) - dSide)
        coords[43] = Pair(pos(8) - dSide, pos(8) - dSide)
        coords[60] = Pair(pos(8) - dSide, pos(10) + dSide)

        // --- PASILLOS DE META (Centro de los brazos) ---

        // Rojo (101-108)
        for (i in 1..8) coords[100 + i] = Pair(pos(9), pos(i - 1) - dOut)

        // Azul (201-208)
        for (i in 1..8) coords[200 + i] = Pair(pos(19 - i) + dOut, pos(9))

        // Verde (301-308)
        for (i in 1..8) coords[300 + i] = Pair(pos(i - 1) - dOut, pos(9))

        // Amarillo (401-408)
        for (i in 1..8) coords[400 + i] = Pair(pos(9), pos(19 - i) + dOut)

    }
    fun getPosition(id: Int, boardWidth: Int, boardHeight: Int): Pair<Float, Float> {
        val relativePos = coords[id] ?: Pair(0.5f, 0.5f)

        // 1. Encontrar el tamaño real de la imagen cuadrada dentro del contenedor rectangular
        val boardSize = minOf(boardWidth, boardHeight)

        // 2. Calcular el espacio vacío (padding) que deja Android para centrar la imagen
        val offsetX = (boardWidth - boardSize) / 2f
        val offsetY = (boardHeight - boardSize) / 2f

        // 3. Ajuste del grosor del borde marrón de tu diseño.
        val margenBorde = 0.02f

        val usableSize = boardSize * (1.0f - 2 * margenBorde)
        val finalOffsetX = offsetX + boardSize * margenBorde
        val finalOffsetY = offsetY + boardSize * margenBorde

        // 4. Calcular la coordenada final en píxeles de la pantalla
        val px = finalOffsetX + (relativePos.first * usableSize)
        val py = finalOffsetY + (relativePos.second * usableSize)

        return Pair(px, py)
    }
}