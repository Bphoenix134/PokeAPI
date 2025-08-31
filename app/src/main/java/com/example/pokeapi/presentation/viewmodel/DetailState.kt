package com.example.pokeapi.presentation.viewmodel

import com.example.pokeapi.domain.model.PokemonDetail

sealed class DetailState {
    object Loading : DetailState()
    data class Success(val detail: PokemonDetail) : DetailState()
    data class Error(val message: String) : DetailState()
}