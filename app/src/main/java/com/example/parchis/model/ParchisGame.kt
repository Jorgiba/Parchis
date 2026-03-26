package com.example.parchis.model

import kotlin.random.Random

class ParchisGame(
    val jugadores: List<Jugador>
) {
    val tablero = Tablero()
    var indiceTurnoActual = 0
    var ultimoDado: Int = 0
    var repiteTurno: Boolean = false
    var vecesSextoConsecutivo: Int = 0

    fun obtenerJugadorActual(): Jugador = jugadores[indiceTurnoActual]

    fun lanzarDado(): Int {
        ultimoDado = Random.nextInt(1, 7)
        
        if (ultimoDado == 6) {
            vecesSextoConsecutivo++
            repiteTurno = true
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
        // Reiniciamos repiteTurno para el siguiente lanzamiento
        repiteTurno = false
    }

    // Lógica para verificar si una ficha se puede mover
    fun puedeMoverFicha(ficha: Ficha, pasos: Int): Boolean {
        if (ficha.estado == EstadoFicha.FINALIZADA) return false
        
        // Si está en casa, solo sale con un 5
        if (ficha.estado == EstadoFicha.EN_CASA) {
            return pasos == 5
        }
        
        // Aquí iría la lógica compleja de colisiones, puentes y entrada a meta
        // Por ahora devolvemos true para permitir el avance básico
        return true
    }

    // Ejecutar el movimiento
    fun moverFicha(ficha: Ficha, pasos: Int) {
        if (ficha.estado == EstadoFicha.EN_CASA && pasos == 5) {
            ficha.posicion = Tablero.SALIDAS[ficha.color] ?: 0
            ficha.estado = EstadoFicha.EN_TABLERO
        } else {
            // Lógica de avance por el tablero (simplificada para este paso)
            ficha.posicion += pasos
            // TODO: Gestionar el paso de 68 a 1 y la entrada a pasillos
        }
    }
}