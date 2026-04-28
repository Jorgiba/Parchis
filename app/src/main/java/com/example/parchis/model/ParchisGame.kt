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
        fun crearPartidaLocal(
            numHumanos: Int,
            dificultad: DificultadBot,
            usuarioLogueado: UsuarioRegistrado?
        ): List<Jugador> {
            val jugadores = mutableListOf<Jugador>()
            val coloresAleatorios = listOf(
                ColorParchis.AMARILLO,
                ColorParchis.AZUL,
                ColorParchis.ROJO,
                ColorParchis.VERDE
            ).shuffled()

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

        return 5
    }

    fun siguienteTurno() {
        if (!repiteTurno) {
            indiceTurnoActual = (indiceTurnoActual + 1) % jugadores.size
        }
        repiteTurno = false
    }

    fun puedeMoverFicha(ficha: Ficha, pasos: Int): Boolean {
        if (ficha.estado == EstadoFicha.FINALIZADA) return false
        val destino = simularDestino(ficha, pasos)
        val fichasDestino = jugadores.flatMap { it.fichas }
            .count { (it.estado == EstadoFicha.EN_TABLERO || it.estado == EstadoFicha.EN_META) && it.posicion == destino }
        if (fichasDestino >= 2) return false

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

    // --- MÉTODOS PARA IA (BOTS) ---

    /**
     * Calcula en qué posición quedaría una ficha sin moverla realmente.
     */
    fun simularDestino(ficha: Ficha, pasos: Int): Int {
        var pos = ficha.posicion
        if (ficha.estado == EstadoFicha.EN_CASA) return Tablero.SALIDAS[ficha.color] ?: 0

        for (i in 1..pasos) {
            if (pos == Tablero.ENTRADAS_PASILLO[ficha.color]) {
                return when (ficha.color) {
                    ColorParchis.ROJO -> 100 + (pasos - i + 1)
                    ColorParchis.AZUL -> 200 + (pasos - i + 1)
                    ColorParchis.VERDE -> 300 + (pasos - i + 1)
                    ColorParchis.AMARILLO -> 400 + (pasos - i + 1)
                }
            }
            pos++
            if (pos > 68) pos = 1
        }
        return pos
    }

    /**
     * Comprueba si hay una ficha de otro color en esa posición (y si es vulnerable).
     */
    fun hayFichaEnemiga(posicion: Int, miColor: ColorParchis): Boolean {
        // En los seguros no se puede comer
        if (Tablero.SEGUROS.contains(posicion)) return false

        // Buscamos en todos los jugadores si alguien tiene una ficha en esa posición
        return jugadores.any { jugador ->
            jugador.color != miColor && jugador.fichas.any { it.posicion == posicion }
        }
    }
}
