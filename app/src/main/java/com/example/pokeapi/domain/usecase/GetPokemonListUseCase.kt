package com.example.pokeapi.domain.usecase

import androidx.paging.PagingData
import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonListUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(limit: Int, offset: Int): Flow<PagingData<Pokemon>> {
        return repository.getPokemonList(limit, offset)
    }
}