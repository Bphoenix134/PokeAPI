package com.example.pokeapi.data.remote

import com.example.pokeapi.data.remote.dto.PokemonDetailResponse
import com.example.pokeapi.data.remote.dto.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonListDetail(@Path("name") name: String): PokemonDetailResponse
}