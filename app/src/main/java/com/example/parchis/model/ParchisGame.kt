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
        var xd = arrayOf(1,2,5)
        ultimoDado = xd.random()
        Log.d("ParchisLogic", "🎲 Dado lanzado: $ultimoDado")

        if (ultimoDado == 6) {
            vecesSextoConsecutivo++
            repiteTurno = true
            if (vecesSextoConsecutivo == 3) {
                Log.d("ParchisLogic", "⚠️ TERCER SEIS! El jugador pierde el turno.")
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
        Log.d("ParchisLogic", "🔄 Cambio de turno. Turno de: ${obtenerJugadorActual().nombre}")
    }

    fun puedeMoverFicha(ficha: Ficha, pasos: Int): Boolean {
        if (ficha.estado == EstadoFicha.FINALIZADA) return false
        
        val destino = simularDestino(ficha, pasos)
        
        // Comprobar barreras (2 fichas en destino)
        val fichasDestino = jugadores.flatMap { it.fichas }
            .count { (it.estado == EstadoFicha.EN_TABLERO || it.estado == EstadoFicha.EN_META) && it.posicion == destino }
        
        if (fichasDestino >= 2) {
            Log.d("ParchisLogic", "🚫 Casilla $destino llena. Movimiento bloqueado para ficha ${ficha.id}")
            return false
        }

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
        val jugadorActual = obtenerJugadorActual()
        val posOriginal = if (ficha.estado == EstadoFicha.EN_CASA) "CASA" else ficha.posicion.toString()

        if (ficha.estado == EstadoFicha.EN_CASA && pasos == 5) {
            val salida = Tablero.SALIDAS[ficha.color] ?: 1
            ficha.posicion = salida
            ficha.estado = EstadoFicha.EN_TABLERO
            Log.d("ParchisLogic", "🏠 SALIDA: ${jugadorActual.nombre} saca ficha ${ficha.id} a casilla $salida")
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
                Log.d("ParchisLogic", "🌈 PASILLO: Ficha ${ficha.id} entra en pasillo")
            } else if (posActual >= 100) {
                posActual++
                if (posActual % 100 == 8) {
                    if (i == pasos) {
                        ficha.estado = EstadoFicha.FINALIZADA
                        Log.d("ParchisLogic", "🏁 META: ¡Ficha ${ficha.id} ha llegado!")
                    }
                }
            } else {
                posActual++
                if (posActual > 68) posActual = 1
            }
        }
        
        ficha.posicion = posActual
        Log.d("ParchisLogic", "🏃 MOVIMIENTO: ${jugadorActual.nombre} Ficha ${ficha.id}: $posOriginal -> $posActual")
        
        comprobarSiCome(posActual, ficha.color)
    }

    private fun comprobarSiCome(posicion: Int, miColor: ColorParchis) {
        if (Tablero.SEGUROS.contains(posicion)) return

        jugadores.forEach { jugador ->
            if (jugador.color != miColor) {
                jugador.fichas.forEach { fichaEnemiga ->
                    if (fichaEnemiga.posicion == posicion && fichaEnemiga.estado == EstadoFicha.EN_TABLERO) {
                        Log.d("ParchisLogic", "⚔️ ¡COMIDA! $miColor come a ${jugador.color} en casilla $posicion")
                        fichaEnemiga.posicion = -1
                        fichaEnemiga.estado = EstadoFicha.EN_CASA
                        repiteTurno = true // Regla: Al comer se repite turno (o se cuentan 20)
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
}
