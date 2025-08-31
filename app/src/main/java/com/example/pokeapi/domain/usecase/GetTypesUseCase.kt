package com.example.pokeapi.domain.usecase

import com.example.pokeapi.domain.repository.PokemonRepository
import javax.inject.Inject

class GetTypesUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(): List<String> {
        return repository.getTypes()
    }
}