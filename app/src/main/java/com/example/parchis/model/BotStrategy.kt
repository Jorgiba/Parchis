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
        // Por ahora usamos la lógica media
        return decidirMovimientoMedio(bot, dado, juego)
    }
}
