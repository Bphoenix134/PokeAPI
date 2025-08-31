package com.example.pokeapi.domain.usecase

import com.example.pokeapi.domain.model.PokemonDetail
import com.example.pokeapi.domain.repository.PokemonRepository
import javax.inject.Inject

class GetPokemonDetailUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(name: String): PokemonDetail {
        return repository.getPokemonDetail(name)
    }
}