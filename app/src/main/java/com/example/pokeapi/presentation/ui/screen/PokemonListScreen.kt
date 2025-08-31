package com.example.pokeapi.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.pokeapi.R
import com.example.pokeapi.presentation.ui.component.FilterMenu
import com.example.pokeapi.presentation.ui.component.PokemonItem
import com.example.pokeapi.presentation.ui.component.SearchBar
import com.example.pokeapi.presentation.viewmodel.PokemonViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    viewModel: PokemonViewModel = hiltViewModel(),
    navigateToDetail: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val isFiltering by viewModel.isFiltering.collectAsState()
    val pokemonList = viewModel.pokemonList.collectAsLazyPagingItems()
    val searchResults by viewModel.searchResults.collectAsState()
    val filterResults by viewModel.filterResults.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    val types by viewModel.types.collectAsState()
    val selectedTypes by viewModel.selectedTypes.collectAsState()

    if (isRefreshing) {
        LaunchedEffect(Unit) {
            viewModel.refreshPokemonList()
            while (pokemonList.loadState.refresh is LoadState.Loading) {
                delay(100)
            }
            isRefreshing = false
            Log.d("PokemonListScreen", "Refresh completed, loadState: ${pokemonList.loadState.refresh}")
        }
    }

    LaunchedEffect(showFilterSheet) {
        if (showFilterSheet) {
            searchQuery = TextFieldValue("")
            viewModel.clearSearch()
        }
    }

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { newValue ->
                        searchQuery = newValue
                        viewModel.searchPokemon("%${newValue.text}%")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(65.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { showFilterSheet = true }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_filter),
                        contentDescription = stringResource(R.string.filter)
                    )
                }
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                Log.d("PokemonListScreen", "Pull-to-refresh triggered")
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Log.d("PokemonListScreen", "searchQuery: ${searchQuery.text}, filterResults: ${filterResults.size}, selectedTypes: $selectedTypes, isFiltering: $isFiltering, pokemonList size: ${pokemonList.itemCount}")

                val displayList = when {
                    filterResults.isNotEmpty() -> {
                        Log.d("PokemonListScreen", "Displaying filterResults: ${filterResults.size} items")
                        filterResults
                    }
                    searchResults.isNotEmpty() -> {
                        Log.d("PokemonListScreen", "Displaying searchResults: ${searchResults.size} items")
                        searchResults
                    }
                    else -> {
                        Log.d("PokemonListScreen", "Displaying pokemonList: ${pokemonList.itemCount} items")
                        pokemonList.itemSnapshotList.items
                    }
                }

                if (displayList.isEmpty()) {
                    when {
                        pokemonList.loadState.refresh is LoadState.Loading || isRefreshing -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = stringResource(R.string.loading),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 16.dp)
                            )
                        }
                        pokemonList.loadState.refresh is LoadState.Error -> {
                            val errorMessage = (pokemonList.loadState.refresh as LoadState.Error).error.message ?: stringResource(R.string.error_loading_data)
                            Text(
                                text = errorMessage,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 16.dp)
                            )
                        }
                        isFiltering -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = stringResource(R.string.loading),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 16.dp)
                            )
                        }
                        filterResults.isEmpty() && selectedTypes.isNotEmpty() -> {
                            Text(
                                text = stringResource(R.string.no_pokemon_found),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 16.dp)
                            )
                        }
                        searchQuery.text.isNotEmpty() -> {
                            Text(
                                text = stringResource(R.string.no_pokemon_found),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 16.dp)
                            )
                        }
                        else -> {
                            Text(
                                text = stringResource(R.string.no_pokemon),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 16.dp)
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(displayList) { pokemon ->
                            PokemonItem(
                                pokemon = pokemon,
                                onClick = { navigateToDetail(pokemon.name) }
                            )
                        }
                    }
                }
            }
        }

        FilterMenu(
            types = types,
            selectedTypes = selectedTypes,
            onSelectedTypesChange = { newSelectedTypes ->
                viewModel.applyFilter(newSelectedTypes.toList())
            },
            onApply = {
                viewModel.applyFilter(selectedTypes.toList())
                showFilterSheet = false
            },
            onReset = {
                viewModel.resetFilter()
                showFilterSheet = false
            },
            showFilterSheet = showFilterSheet,
            onDismiss = { showFilterSheet = false }
        )
    }
}