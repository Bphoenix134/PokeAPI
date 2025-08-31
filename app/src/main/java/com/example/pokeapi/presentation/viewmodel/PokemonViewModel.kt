package com.example.pokeapi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.model.PokemonDetail
import com.example.pokeapi.domain.usecase.FilterPokemonUseCase
import com.example.pokeapi.domain.usecase.GetPokemonDetailUseCase
import com.example.pokeapi.domain.usecase.GetPokemonListUseCase
import com.example.pokeapi.domain.usecase.SearchPokemonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val getPokemonListUseCase: GetPokemonListUseCase,
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase,
    private val searchPokemonUseCase: SearchPokemonUseCase,
    private val filterPokemonUseCase: FilterPokemonUseCase
) : ViewModel() {
    val pokemonList: Flow<PagingData<Pokemon>> = getPokemonListUseCase(20, 0).cachedIn(viewModelScope)

    private val _detail = MutableStateFlow<PokemonDetail?>(null)
    val detail: StateFlow<PokemonDetail?> = _detail

    private val _searchResults = MutableStateFlow<List<Pokemon>>(emptyList())
    val searchResults: StateFlow<List<Pokemon>> = _searchResults

    private val _filterResults = MutableStateFlow<List<Pokemon>>(emptyList())
    val filterResults: StateFlow<List<Pokemon>> = _filterResults

    fun getPokemonDetail(name: String) {
        viewModelScope.launch {
            try {
                _detail.value = getPokemonDetailUseCase(name)
            } catch (e: Exception) {
                _detail.value = null
            }
        }
    }

    fun searchPokemon(query: String) {
        viewModelScope.launch {
            _searchResults.value = searchPokemonUseCase(query)
        }
    }

    fun filterByType(type: String) {
        viewModelScope.launch {
            _filterResults.value = filterPokemonUseCase(type)
        }
    }
}