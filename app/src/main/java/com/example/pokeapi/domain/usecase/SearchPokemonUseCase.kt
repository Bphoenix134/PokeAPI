package com.example.pokeapi.domain.usecase

import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.repository.PokemonRepository
import javax.inject.Inject

class SearchPokemonUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(query: String): List<Pokemon> {
        return repository.searchPokemon(query)
    }
}