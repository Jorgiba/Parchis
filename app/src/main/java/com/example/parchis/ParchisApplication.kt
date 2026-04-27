package com.example.parchis

import android.app.Application
import com.example.parchis.database.AppDatabase
import com.example.parchis.model.Partida
import com.example.parchis.model.ResultadoPartida
import com.example.parchis.model.UsuarioRegistrado
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Date

class ParchisApplication : Application() {
    // Punto 5: Propiedad lazy para la base de datos
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    
    // Punto 9: Ámbito para tareas de inicialización (opcional)
    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        
        // --- INICIALIZACIÓN DE FIREBASE Y APP CHECK ---
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        // Punto 9: Poblar la base de datos si es necesario
        applicationScope.launch {
            val dao = database.usuarioDao()
            if (dao.obtenerUsuario("Ejemplo") == null) {
                val user = UsuarioRegistrado("Ejemplo")
                user.fichasComidas = 65
                dao.insertarUsuario(user)
                
                // Insertar algunas partidas de prueba para el usuario Ejemplo
                dao.insertarPartida(Partida("1", Date(), ResultadoPartida.VICTORIA, listOf("Ejemplo", "Bot1", "Bot2", "Bot3"), "Ejemplo"))
                dao.insertarPartida(Partida("2", Date(), ResultadoPartida.DERROTA, listOf("Ejemplo", "Jugador2", "Bot1", "Bot2"), "Ejemplo"))
            }
        }
    }
}
