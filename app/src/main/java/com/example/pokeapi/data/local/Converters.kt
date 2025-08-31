package com.example.pokeapi.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(list: List<String>): String = list.joinToString(",")

    @TypeConverter
    fun toList(string: String): List<String> = string.split(",")
}