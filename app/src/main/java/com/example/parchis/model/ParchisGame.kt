package com.example.parchis.model

import android.util.Log
import kotlin.random.Random

class ParchisGame(
    val jugadores: List<Jugador>
) {
    val tablero = Tablero()
    var indiceTurnoActual = if (jugadores.isNotEmpty()) Random.nextInt(jugadores.size) else 0
    var ultimoDado: Int = 0
    var repiteTurno: Boolean = false
    var vecesSextoConsecutivo: Int = 0
    var movimientosExtra: Int = 0

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

            val ordenAntihorario = mapOf(
                ColorParchis.AMARILLO to 1,
                ColorParchis.AZUL to 2,
                ColorParchis.ROJO to 3,
                ColorParchis.VERDE to 4
            )

            return jugadores.sortedBy { ordenAntihorario[it.color] ?: 0}
        }
    }

    fun obtenerJugadorActual(): Jugador = jugadores[indiceTurnoActual]

    fun lanzarDado(): Int {
        val dadoFisico = Random.nextInt(1, 7)
        Log.d("ParchisLogic", "Dado lanzado: $dadoFisico")

        if (dadoFisico == 6) {
            vecesSextoConsecutivo++
            repiteTurno = true
            if (vecesSextoConsecutivo == 3) {
                vecesSextoConsecutivo = 0
                repiteTurno = false
                return 6
            }
        } else {
            vecesSextoConsecutivo = 0
            repiteTurno = false
        }

        val jugadorActual = obtenerJugadorActual()
        val fichasEnCasa = jugadorActual.fichas.count { it.estado == EstadoFicha.EN_CASA }
        ultimoDado = dadoFisico

        if (dadoFisico == 6 && fichasEnCasa == 0) return 12
        return dadoFisico
    }

    fun siguienteTurno() {
        if (!repiteTurno) {
            indiceTurnoActual = (indiceTurnoActual + 1) % jugadores.size
        }
        repiteTurno = false
        movimientosExtra = 0
    }

    fun puedeMoverFicha(ficha: Ficha, pasos: Int): Boolean {
        if (ficha.estado == EstadoFicha.FINALIZADA) return false

        if (ficha.estado == EstadoFicha.EN_CASA) {
            if (pasos != 5) return false
            val salida = Tablero.SALIDAS[ficha.color] ?: 1
            val fichasEnSalida = jugadores.flatMap { it.fichas }
                .count { it.posicion == salida && (it.estado == EstadoFicha.EN_TABLERO || it.estado == EstadoFicha.EN_META) }
            return fichasEnSalida < 2
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
            } else if (posActual >= 100) {
                posActual++
                if (posActual % 100 > 8) return false
            } else {
                posActual++
                if (posActual > 68) posActual = 1
            }

            if (i < pasos && hayBarrera(posActual)) return false
        }

        // Casilla de meta (pasillo X08): sin límite de fichas
        if (posActual >= 100 && posActual % 100 == 8) return true

        val fichasDestino = jugadores.flatMap { it.fichas }
            .count { it.posicion == posActual && (it.estado == EstadoFicha.EN_TABLERO || it.estado == EstadoFicha.EN_META) }

        return fichasDestino < 2
    }

    fun moverFicha(ficha: Ficha, pasos: Int) {
        var posDestino = -1
        var acabaDeSalir = false

        if (ficha.estado == EstadoFicha.EN_CASA && pasos == 5) {
            posDestino = Tablero.SALIDAS[ficha.color] ?: 1
            ficha.posicion = posDestino
            ficha.estado = EstadoFicha.EN_TABLERO
            acabaDeSalir = true
            Log.d("ParchisLogic", "🏠 SALIDA: saca ficha ${ficha.id} a casilla $posDestino")
        } else {
            var pos = ficha.posicion
            val entrada = Tablero.ENTRADAS_PASILLO[ficha.color] ?: -1
            for (i in 1..pasos) {
                if (pos == entrada) {
                    pos = when (ficha.color) {
                        ColorParchis.ROJO -> 101; ColorParchis.AZUL -> 201
                        ColorParchis.VERDE -> 301; ColorParchis.AMARILLO -> 401
                    }
                    ficha.estado = EstadoFicha.EN_META
                } else if (pos >= 100) {
                    pos++
                    if (pos % 100 == 8 && i == pasos) {
                        ficha.estado = EstadoFicha.FINALIZADA
                        movimientosExtra = 10
                    }
                } else {
                    pos++
                    if (pos > 68) pos = 1
                }
            }
            ficha.posicion = pos
            posDestino = pos
        }

        if (posDestino != -1) comprobarSiCome(posDestino, ficha.color, acabaDeSalir)
    }

    private fun comprobarSiCome(posicion: Int, miColor: ColorParchis, acabaDeSalir: Boolean = false) {
        val miSalida = Tablero.SALIDAS[miColor] == posicion
        if (Tablero.SEGUROS.contains(posicion) && !(miSalida && acabaDeSalir)) return

        jugadores.forEach { j ->
            if (j.color != miColor) {
                j.fichas.forEach { f ->
                    if (f.posicion == posicion && f.estado == EstadoFicha.EN_TABLERO) {
                        f.posicion = -1
                        f.estado = EstadoFicha.EN_CASA
                        jugadores.find { it.color == miColor }?.fichasComidasEnEstaPartida++
                        movimientosExtra = 20
                    }
                }
            }
        }
    }

    fun simularDestino(ficha: Ficha, pasos: Int): Int {
        var pos = ficha.posicion
        if (ficha.estado == EstadoFicha.EN_CASA) return Tablero.SALIDAS[ficha.color] ?: 0

        val entradaPasillo = Tablero.ENTRADAS_PASILLO[ficha.color] ?: -1
        val basePassillo = when (ficha.color) {
            ColorParchis.ROJO -> 100
            ColorParchis.AZUL -> 200
            ColorParchis.VERDE -> 300
            ColorParchis.AMARILLO -> 400
        }

        for (i in 1..pasos) {
            if (pos == entradaPasillo) {
                // Entra al pasillo: posición base + pasos restantes
                pos = basePassillo + (pasos - i + 1)
                return pos
            }
            if (pos >= 100) {
                pos++
            } else {
                pos++
                if (pos > 68) pos = 1
            }
        }
        return pos
    }

    fun hayFichaEnemiga(posicion: Int, miColor: ColorParchis): Boolean {
        if (Tablero.SEGUROS.contains(posicion)) return false
        return jugadores.any { jugador ->
            jugador.color != miColor && jugador.fichas.any { it.posicion == posicion && it.estado == EstadoFicha.EN_TABLERO }
        }
    }

    fun hayBarrera(posicion: Int): Boolean {
        if (!Tablero.SEGUROS.contains(posicion)) return false

        return jugadores.any { jugador ->
            jugador.fichas.count {
                it.posicion == posicion && (it.estado == EstadoFicha.EN_TABLERO || it.estado == EstadoFicha.EN_META)
            } >= 2
        }
    }
}
