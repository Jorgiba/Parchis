package com.example.parchis.model

object BotStrategy {

    fun decidirMovimiento(bot: Bot, dado: Int, juego: ParchisGame): Ficha? {
        return when (bot.dificultad) {
            DificultadBot.FACIL -> decidirMovimientoFacil(bot, dado, juego)
            DificultadBot.MEDIA -> decidirMovimientoMedio(bot, dado, juego)
            DificultadBot.DIFICIL -> decidirMovimientoDificil(bot, dado, juego)
        }
    }

    fun decidirMovimientoFacil(bot: Bot, dado: Int, juego: ParchisGame): Ficha? {
        val fichasMovibles = bot.fichas.filter { ficha ->
            juego.puedeMoverFicha(ficha, dado)
        }
        return if (fichasMovibles.isNotEmpty()) {
            fichasMovibles.random()
        } else {
            null
        }
    }

    fun decidirMovimientoMedio(bot: Bot, dado: Int, juego: ParchisGame): Ficha? {
        val fichasMovibles = bot.fichas.filter { ficha ->
            juego.puedeMoverFicha(ficha, dado)
        }
        if (fichasMovibles.isEmpty()) return null

        return fichasMovibles.maxByOrNull { ficha ->
            var puntuacion = 0
            val destino = juego.simularDestino(ficha, dado)

            if (juego.hayFichaEnemiga(destino, bot.color)) {
                puntuacion += 100
            }
            if (destino % 100 == 8 && destino >= 100) {
                puntuacion += 80
            }
            if (ficha.estado == EstadoFicha.EN_CASA && dado == 5) {
                puntuacion += 60
            }
            if (Tablero.SEGUROS.contains(destino)) {
                puntuacion += 40
            }
            puntuacion += dado

            puntuacion
        }
    }

    fun decidirMovimientoDificil(bot: Bot, dado: Int, juego: ParchisGame): Ficha? {
        val fichasMovibles = bot.fichas.filter { ficha ->
            juego.puedeMoverFicha(ficha, dado)
        }

        if (fichasMovibles.isEmpty()) return null
        if (fichasMovibles.size == 1) return fichasMovibles.first()

        return fichasMovibles.maxByOrNull { evaluarMovimientoDificil(it, dado, juego) }
    }

    private fun evaluarMovimientoDificil(ficha: Ficha, pasos: Int, game: ParchisGame): Int {
        var puntuacion = 0
        val destino = game.simularDestino(ficha, pasos)
        val color = ficha.color

        if (destino >= 100 && destino % 100 == 8) return 10000
        if (ficha.estado == EstadoFicha.EN_CASA && pasos == 5) return 8000
        if (game.hayFichaEnemiga(destino, color)) return 5000

        val destinoEsSeguro = Tablero.SEGUROS.contains(destino) || destino >= 100
        val estabaEnPeligro = enemigoAcechando(ficha.posicion, color, game)
        val estaraEnPeligro = !destinoEsSeguro && enemigoAcechando(destino, color, game)

        if (estabaEnPeligro && !estaraEnPeligro) puntuacion += 2000

        if (estaraEnPeligro) puntuacion -= 3000

        if (destinoEsSeguro && !Tablero.SEGUROS.contains(ficha.posicion)) puntuacion += 500

        if (destino >= 100 && ficha.posicion < 100) puntuacion += 1000

        val formaraBarrera = destinoEsSeguro && game.jugadores.any {
            it.color == color && it.fichas.any { f ->
                f.posicion == destino && f != ficha && (f.estado == EstadoFicha.EN_TABLERO || f.estado == EstadoFicha.EN_META)
            }
        }
        if (formaraBarrera) {
            puntuacion += 1500
        }
        if (!destinoEsSeguro && enemigoAAlcance(destino, color, game)) {
            puntuacion += 300
        }
        puntuacion += calcularProgreso(destino, color)
        return puntuacion
    }

    private fun enemigoAcechando(
        miPosicion: Int,
        miColor: ColorParchis,
        game: ParchisGame
    ): Boolean {
        if (miPosicion < 1 || miPosicion > 68) return false

        for (i in 1..6) {
            var posRevisar = miPosicion - i
            if (posRevisar <= 0) posRevisar += 68
            if (game.hayFichaEnemiga(posRevisar, miColor)) return true
        }
        return false
    }

    private fun enemigoAAlcance(
        miPosicion: Int,
        miColor: ColorParchis,
        game: ParchisGame
    ): Boolean {
        if (miPosicion < 1 || miPosicion > 68) return false

        for (i in 1..6) {
            var posRevisar = miPosicion + i
            if (posRevisar > 68) posRevisar -= 68
            if (game.hayFichaEnemiga(
                    posRevisar,
                    miColor
                ) && !Tablero.SEGUROS.contains(posRevisar)
            ) return true
        }
        return false
    }

    private fun calcularProgreso(posicion: Int, color: ColorParchis): Int {
        if (posicion >= 100) return 500
        if (posicion <= 0) return 0

        val salida = Tablero.SALIDAS[color] ?: 1
        var progreso = posicion - salida
        if (progreso < 0) progreso += 68

        return progreso
    }
}