package com.example.parchis.model

import kotlin.random.Random

class ParchisGame(
    val jugadores: List<Jugador>
) {
    val tablero = Tablero()
    var indiceTurnoActual = if (jugadores.isNotEmpty()) Random.nextInt(jugadores.size) else 0
    var ultimoDado: Int = 0
    var repiteTurno: Boolean = false
    var vecesSextoConsecutivo: Int = 0

    companion object {
        fun crearPartidaLocal(numHumanos: Int, dificultad: DificultadBot, usuarioLogueado: UsuarioRegistrado?): List<Jugador> {
            val jugadores = mutableListOf<Jugador>()
            val coloresAleatorios = listOf(ColorParchis.AMARILLO, ColorParchis.AZUL, ColorParchis.ROJO, ColorParchis.VERDE).shuffled()

            for (i in 0 until numHumanos) {
                if (i == 0 && usuarioLogueado != null) {
                    jugadores.add(JugadorRegistrado(usuarioLogueado, coloresAleatorios[i]))
                } else {
                    jugadores.add(JugadorHumano("Jugador ${i + 1}", coloresAleatorios[i]))
                }
            }

            for (i in numHumanos until 4) {
                jugadores.add(Bot("Bot ${i + 1}", coloresAleatorios[i], dificultad))
            }
            
            return jugadores.sortedBy { it.color.ordinal }
        }
    }

    fun obtenerJugadorActual(): Jugador = jugadores[indiceTurnoActual]

    fun lanzarDado(): Int {
        ultimoDado = Random.nextInt(1, 7)
        
        if (ultimoDado == 6) {
            vecesSextoConsecutivo++
            repiteTurno = true
            if (vecesSextoConsecutivo == 3) {
                vecesSextoConsecutivo = 0
                repiteTurno = false
            }
        } else {
            vecesSextoConsecutivo = 0
            repiteTurno = false
        }
        
        return ultimoDado
    }

    fun siguienteTurno() {
        if (!repiteTurno) {
            indiceTurnoActual = (indiceTurnoActual + 1) % jugadores.size
        }
        repiteTurno = false
    }

    fun puedeMoverFicha(ficha: Ficha, pasos: Int): Boolean {
        if (ficha.estado == EstadoFicha.FINALIZADA) return false
        
        if (ficha.estado == EstadoFicha.EN_CASA) {
            return pasos == 5
        }
        
        if (ficha.estado == EstadoFicha.EN_META) {
            val posicionEnPasillo = ficha.posicion % 100
            return posicionEnPasillo + pasos <= 8
        }

        return true
    }

    fun moverFicha(ficha: Ficha, pasos: Int) {
        if (ficha.estado == EstadoFicha.EN_CASA && pasos == 5) {
            ficha.posicion = Tablero.SALIDAS[ficha.color] ?: 1
            ficha.estado = EstadoFicha.EN_TABLERO
            return
        }

        var posActual = ficha.posicion
        val entradaPasillo = Tablero.ENTRADAS_PASILLO[ficha.color] ?: -1

        for (i in 1..pasos) {
            if (posActual == entradaPasillo) {
                posActual = when (ficha.color) {
                    ColorParchis.ROJO -> 101
                    ColorParchis.AZUL -> 201
                    ColorParchis.VERDE -> 301
                    ColorParchis.AMARILLO -> 401
                }
                ficha.estado = EstadoFicha.EN_META
            } else if (posActual >= 100) {
                posActual++
                if (posActual % 100 == 8) { 
                    if (i == pasos) {
                        ficha.estado = EstadoFicha.FINALIZADA
                    }
                }
            } else {
                posActual++
                if (posActual > 68) posActual = 1
            }
        }
        ficha.posicion = posActual
    }

    // --- LÓGICA DE BOTS ---

    fun decidirMovimientoBot(bot: Bot, dado: Int): Ficha? {
        val fichasMovibles = bot.fichas.filter { puedeMoverFicha(it, dado) }
        if (fichasMovibles.isEmpty()) return null

        return when (bot.dificultad) {
            DificultadBot.FACIL -> decidirFacil(fichasMovibles)
            DificultadBot.MEDIA -> decidirMedia(fichasMovibles, dado)
            DificultadBot.DIFICIL -> decidirDificil(fichasMovibles, dado)
        }
    }

    private fun decidirFacil(fichas: List<Ficha>): Ficha {
        return fichas.random()
    }

    private fun decidirMedia(fichas: List<Ficha>, dado: Int): Ficha {
        if (dado == 5) {
            val fichaEnCasa = fichas.find { it.estado == EstadoFicha.EN_CASA }
            if (fichaEnCasa != null) return fichaEnCasa
        }

        val enPasilloMeta = fichas.filter { it.estado == EstadoFicha.EN_META }
        if (enPasilloMeta.isNotEmpty()) {
            return enPasilloMeta.maxByOrNull { it.posicion % 100 }!!
        }

        return fichas.maxByOrNull { it.posicion } ?: fichas.random()
    }

    private fun decidirDificil(fichas: List<Ficha>, dado: Int): Ficha {
        for (ficha in fichas) {
            val posicionDestino = simularDestino(ficha, dado)
            if (hayFichaEnemiga(posicionDestino, ficha.color)) {
                return ficha
            }
        }

        val paraEntrarMeta = fichas.find { it.estado == EstadoFicha.EN_META && (it.posicion % 100) + dado == 8 }
        if (paraEntrarMeta != null) return paraEntrarMeta

        if (dado == 5) {
            val fichaEnCasa = fichas.find { it.estado == EstadoFicha.EN_CASA }
            if (fichaEnCasa != null) return fichaEnCasa
        }

        return decidirMedia(fichas, dado)
    }

    private fun simularDestino(ficha: Ficha, pasos: Int): Int {
        var pos = ficha.posicion
        if (ficha.estado == EstadoFicha.EN_CASA) return Tablero.SALIDAS[ficha.color] ?: 0
        
        for (i in 1..pasos) {
            if (pos == Tablero.ENTRADAS_PASILLO[ficha.color]) return 100
            pos++
            if (pos > 68) pos = 1
        }
        return pos
    }

    private fun hayFichaEnemiga(posicion: Int, miColor: ColorParchis): Boolean {
        return false
    }
}