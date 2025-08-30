package com.example.pokeapi.data.remote.dto

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val sprites: SpritesDto,
    val types: List<TypeDto>,
    val height: Int,
    val weight: Int,
    val abilities: List<AbilityDto>
)