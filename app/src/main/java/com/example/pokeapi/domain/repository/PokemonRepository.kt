package com.example.pokeapi.domain.repository

import androidx.paging.PagingData
import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.model.PokemonDetail
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonList(limit: Int, offset: Int): Flow<PagingData<Pokemon>>
    suspend fun getPokemonDetail(name: String): PokemonDetail
    suspend fun searchPokemon(query: String): List<Pokemon>
    suspend fun filterByTypes(types: List<String>): List<Pokemon>
    suspend fun getTypes(): List<String>
    fun invalidate()
}