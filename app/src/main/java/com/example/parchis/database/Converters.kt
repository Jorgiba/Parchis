package com.example.parchis.database

import androidx.room.TypeConverter
import com.example.parchis.model.ResultadoPartida
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toList(value: String): List<String> = value.split(",")

    @TypeConverter
    fun fromResultadoPartida(value: ResultadoPartida): String = value.name

    @TypeConverter
    fun toResultadoPartida(value: String): ResultadoPartida = ResultadoPartida.valueOf(value)
}
