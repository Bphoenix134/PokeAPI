package com.example.pokeapi.data.local

import androidx.room.TypeConverters

class Converters {
    @TypeConverters
    fun fromList(list: List<String>): String = list.joinToString(",")
    @TypeConverters
    fun toList(string: String): List<String> = string.split(",")
}