package com.example.pokeapi.domain.usecase

import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.repository.PokemonRepository
import javax.inject.Inject

class FilterPokemonUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(types: List<String>): List<Pokemon> {
        return repository.filterByTypes(types)
    }
}