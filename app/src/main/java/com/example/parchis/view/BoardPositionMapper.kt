package com.example.parchis.view

object BoardPositionMapper {

    private const val GRID_SIZE = 19
    private const val S = 1.0f / GRID_SIZE
    private const val C0 = S / 2.0f

    private val coords = mutableMapOf<Int, Pair<Float, Float>>()

    init {

        val factorPaso = 0.96f

        fun pos(index: Int): Float {
            val distanciaAlCentro = index - 9
            return 0.5f + (distanciaAlCentro * S * factorPaso)
        }

        val perdidaEnSalida = 5 * S * (1.0f - factorPaso)

        val dSide = 0.06f
        val dOut = 0.025f + perdidaEnSalida


        // BRAZO INFERIOR (Amarillo)
        for (i in 0..7) coords[60 + i] = Pair(pos(8) - dSide, pos(11 + i) + dOut)
        coords[68] = Pair(pos(9), pos(18) + dOut)
        for (i in 0..7) coords[1 + i] = Pair(pos(10) + dSide, pos(18 - i) + dOut)

        // BRAZO DERECHO (Azul)
        for (i in 0..7) coords[9 + i] = Pair(pos(11 + i) + dOut, pos(10) + dSide)
        coords[17] = Pair(pos(18) + dOut, pos(9))
        for (i in 0..7) coords[18 + i] = Pair(pos(18 - i) + dOut, pos(8) - dSide)

        // BRAZO SUPERIOR (Rojo)
        for (i in 0..7) coords[26 + i] = Pair(pos(10) + dSide, pos(7 - i) - dOut)
        coords[34] = Pair(pos(9), pos(0) - dOut)
        for (i in 0..7) coords[35 + i] = Pair(pos(8) - dSide, pos(0 + i) - dOut)

        // BRAZO IZQUIERDO (Verde)
        for (i in 0..7) coords[43 + i] = Pair(pos(7 - i) - dOut, pos(8) - dSide)
        coords[51] = Pair(pos(0) - dOut, pos(9))
        for (i in 0..7) coords[52 + i] = Pair(pos(0 + i) - dOut, pos(10) + dSide)



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