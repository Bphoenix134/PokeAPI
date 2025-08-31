package com.example.pokeapi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.usecase.FilterPokemonUseCase
import com.example.pokeapi.domain.usecase.GetPokemonDetailUseCase
import com.example.pokeapi.domain.usecase.GetPokemonListUseCase
import com.example.pokeapi.domain.usecase.GetTypesUseCase
import com.example.pokeapi.domain.usecase.SearchPokemonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.pokeapi.domain.repository.PokemonRepository

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val getPokemonListUseCase: GetPokemonListUseCase,
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase,
    private val searchPokemonUseCase: SearchPokemonUseCase,
    private val filterPokemonUseCase: FilterPokemonUseCase,
    private val getTypesUseCase: GetTypesUseCase,
    private val repository: PokemonRepository
) : ViewModel() {
    val pokemonList: Flow<PagingData<Pokemon>> = getPokemonListUseCase(20, 0).cachedIn(viewModelScope)

    private val _detail = MutableStateFlow<DetailState>(DetailState.Loading)
    val detail: StateFlow<DetailState> = _detail

    private val _searchResults = MutableStateFlow<List<Pokemon>>(emptyList())
    val searchResults: StateFlow<List<Pokemon>> = _searchResults

    private val _filterResults = MutableStateFlow<List<Pokemon>>(emptyList())
    val filterResults: StateFlow<List<Pokemon>> = _filterResults

    private val _types = MutableStateFlow<List<String>>(emptyList())
    val types: StateFlow<List<String>> = _types.asStateFlow()

    private val _isFiltering = MutableStateFlow(false)
    val isFiltering: StateFlow<Boolean> = _isFiltering.asStateFlow()

    // Добавляем StateFlow для управления состоянием выбранных типов
    private val _selectedTypes = MutableStateFlow<Set<String>>(emptySet())
    val selectedTypes: StateFlow<Set<String>> = _selectedTypes.asStateFlow()

    init {
        loadTypes()
    }

    private fun loadTypes() {
        viewModelScope.launch {
            _types.value = getTypesUseCase()
            if (_types.value.isEmpty()) {
                _types.value = listOf("normal", "fighting", "flying", "poison", "ground", "rock", "bug", "ghost", "steel", "fire", "water", "grass", "electric", "psychic", "ice", "dragon", "dark", "fairy")
            }
        }
    }

    fun getPokemonDetail(name: String) {
        viewModelScope.launch {
            _detail.value = DetailState.Loading
            try {
                val detail = getPokemonDetailUseCase(name)
                _detail.value = DetailState.Success(detail)
            } catch (e: Exception) {
                _detail.value = DetailState.Error("Pokémon not found")
            }
        }
    }

    fun searchPokemon(query: String) {
        viewModelScope.launch {
            _filterResults.value = emptyList()
            _searchResults.value = searchPokemonUseCase(query)
        }
    }

    fun applyFilter(types: List<String>) {
        viewModelScope.launch {
            _isFiltering.value = types.isNotEmpty()
            _searchResults.value = emptyList()
            _filterResults.value = filterPokemonUseCase(types)
            _selectedTypes.value = types.toSet()
            _isFiltering.value = false
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
    }

    fun resetFilter() {
        _filterResults.value = emptyList()
        _selectedTypes.value = emptySet()
        _isFiltering.value = false
    }

    suspend fun refreshPokemonList() {
        _searchResults.value = emptyList()
        _filterResults.value = emptyList()
        _selectedTypes.value = emptySet()
        _isFiltering.value = false
        repository.invalidate()
    }
}