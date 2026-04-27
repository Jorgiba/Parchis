package com.example.parchis.database

import androidx.room.*
import com.example.parchis.model.Partida
import com.example.parchis.model.UsuarioRegistrado

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios")
    suspend fun obtenerTodosLosUsuarios(): List<UsuarioRegistrado>

    @Query("SELECT * FROM usuarios WHERE username = :name")
    suspend fun obtenerUsuario(name: String): UsuarioRegistrado?

    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun obtenerUsuarioPorEmail(email: String): UsuarioRegistrado?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioRegistrado)

    @Update
    suspend fun actualizarUsuario(usuario: UsuarioRegistrado)

    @Query("SELECT * FROM partidas WHERE usernameUsuario = :username")
    suspend fun obtenerHistorial(username: String): List<Partida>

    @Insert
    suspend fun insertarPartida(partida: Partida)
}
