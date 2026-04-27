package com.example.parchis.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Room
import android.content.Context
import com.example.parchis.model.Partida
import com.example.parchis.model.UsuarioRegistrado

@Database(entities = [UsuarioRegistrado::class, Partida::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "parchis_database"
                )
                .fallbackToDestructiveMigration() // Esto borrará la DB vieja y creará la nueva con la versión 2
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
