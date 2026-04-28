package com.example.parchis.view

object BoardPositionMapper {

    // El tablero real de tu imagen tiene 19x19 casillas.
    // 8 casillas por brazo + 3 del centro + 8 del otro brazo = 19
    private const val GRID_SIZE = 19
    private const val S = 1.0f / GRID_SIZE
    private const val C0 = S / 2.0f

    private val coords = mutableMapOf<Int, Pair<Float, Float>>()

    init {
        // Función para obtener el centro de una fila/columna concreta (0 a 18)
        fun pos(index: Int): Float = C0 + index * S

        // --- CIRCUITO COMÚN (1-68) ---
        // Se recorre en sentido antihorario.

        // 1 a 8: Brazo Inferior, columna derecha (subiendo)
        for (i in 1..8) coords[i] = Pair(pos(10), pos(19 - i))

        // 9: Esquina interior inferior derecha
        coords[9] = Pair(pos(10), pos(10))

        // 10 a 17: Brazo Derecho, fila inferior (yéndose a la derecha)
        for (i in 1..8) coords[9 + i] = Pair(pos(10 + i), pos(10))

        // 18 a 25: Brazo Derecho, fila superior (volviendo a la izquierda hacia el centro)
        for (i in 1..8) coords[17 + i] = Pair(pos(19 - i), pos(8))

        // 26: Esquina interior superior derecha
        coords[26] = Pair(pos(10), pos(8))

        // 27 a 34: Brazo Superior, columna derecha (subiendo hacia el borde)
        for (i in 1..8) coords[26 + i] = Pair(pos(10), pos(8 - i))

        // 35 a 42: Brazo Superior, columna izquierda (bajando hacia el centro)
        // La salida Roja (39) cae automáticamente en pos(8), pos(4).
        for (i in 1..8) coords[34 + i] = Pair(pos(8), pos(i - 1))

        // 43: Esquina interior superior izquierda
        coords[43] = Pair(pos(8), pos(8))

        // 44 a 51: Brazo Izquierdo, fila superior (yéndose a la izquierda al borde)
        for (i in 1..8) coords[43 + i] = Pair(pos(8 - i), pos(8))

        // 52 a 59: Brazo Izquierdo, fila inferior (volviendo a la derecha hacia el centro)
        for (i in 1..8) coords[51 + i] = Pair(pos(i - 1), pos(10))

        // 60: Esquina interior inferior izquierda
        coords[60] = Pair(pos(8), pos(10))

        // 61 a 68: Brazo Inferior, columna izquierda (bajando hacia el borde)
        for (i in 1..8) coords[60 + i] = Pair(pos(8), pos(10 + i))

        // --- PASILLOS DE META ---
        // Se recorren desde el borde exterior hacia el centro (pos 9, 9)

        // Rojo (101-108): Brazo Superior, columna central, bajando
        for (i in 1..8) coords[100 + i] = Pair(pos(9), pos(i - 1))

        // Azul (201-208): Brazo Derecho, fila central, yendo a la izquierda
        for (i in 1..8) coords[200 + i] = Pair(pos(19 - i), pos(9))

        // Verde (301-308): Brazo Izquierdo, fila central, yendo a la derecha
        for (i in 1..8) coords[300 + i] = Pair(pos(i - 1), pos(9))

        // Amarillo (401-408): Brazo Inferior, columna central, subiendo
        for (i in 1..8) coords[400 + i] = Pair(pos(9), pos(19 - i))

        val orig5 = coords[5]!!
        // Modifica los ceros. Ejemplo: 0.02f o -0.015f
        coords[5] = Pair(orig5.first + 0.06f, orig5.second + 0.025f)

        // SALIDA AZUL (Casilla 22)
        val orig22 = coords[22]!!
        coords[22] = Pair(orig22.first + 0.025f, orig22.second - 0.06f)

        // SALIDA ROJO (Casilla 39)
        val orig39 = coords[39]!!
        coords[39] = Pair(orig39.first - 0.06f, orig39.second - 0.025f)

        // SALIDA VERDE (Casilla 56)
        val orig56 = coords[56]!!
        coords[56] = Pair(orig56.first - 0.025f, orig56.second + 0.06f)
    }

    fun getPosition(id: Int, boardWidth: Int, boardHeight: Int): Pair<Float, Float> {
        val relativePos = coords[id] ?: Pair(0.5f, 0.5f)

        // 1. Encontrar el tamaño real de la imagen cuadrada dentro del contenedor rectangular
        val boardSize = minOf(boardWidth, boardHeight)

        // 2. Calcular el espacio vacío (padding) que deja Android para centrar la imagen
        val offsetX = (boardWidth - boardSize) / 2f
        val offsetY = (boardHeight - boardSize) / 2f

        // 3. Ajuste del grosor del borde marrón de tu diseño.
        // Como la cuadrícula no empieza justo en el píxel 0 de la imagen, aplicamos un margen.
        // 0.02f equivale a un 2% del tamaño del tablero.
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