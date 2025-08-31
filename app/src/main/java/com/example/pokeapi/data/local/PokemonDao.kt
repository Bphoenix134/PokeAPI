package com.example.pokeapi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemons: List<PokemonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemon: PokemonEntity)

    @Query("SELECT * FROM pokemon LIMIT :limit OFFSET :offset")
    suspend fun getPokemonList(limit: Int, offset: Int): List<PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE name LIKE :query")
    suspend fun searchPokemon(query: String): List<PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE name = :name")
    suspend fun getPokemonByName(name: String): PokemonEntity?

    @Query("SELECT * FROM pokemon WHERE types Like :type")
    suspend fun filterByType(type: String): List<PokemonEntity>
}