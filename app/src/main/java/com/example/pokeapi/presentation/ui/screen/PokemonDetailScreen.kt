package com.example.pokeapi.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pokeapi.presentation.viewmodel.PokemonViewModel
import com.example.pokeapi.R
import com.example.pokeapi.presentation.ui.component.DetailInfoRow
import com.example.pokeapi.presentation.viewmodel.DetailState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    name: String,
    viewModel: PokemonViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    viewModel.getPokemonDetail(name)
    val detailState by viewModel.detail.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name.replaceFirstChar { it.uppercase() }) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (detailState) {
            is DetailState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.loading))
                }
            }
            is DetailState.Success -> {
                val pokemon = (detailState as DetailState.Success).detail
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.size(200.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        AsyncImage(
                            model = pokemon.imageUrl,
                            contentDescription = pokemon.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailInfoRow(stringResource(R.string.types), pokemon.types.joinToString(", ") { it.replaceFirstChar { c -> c.uppercase() } })
                    HorizontalDivider()
                    DetailInfoRow(stringResource(R.string.height), "${pokemon.height / 10.0} m")
                    HorizontalDivider()
                    DetailInfoRow(stringResource(R.string.weight), "${pokemon.weight / 10.0} kg")
                    HorizontalDivider()
                    DetailInfoRow(stringResource(R.string.abilities), pokemon.abilities.joinToString(", ") { it.replaceFirstChar { c -> c.uppercase() } })
                }
            }
            is DetailState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text((detailState as DetailState.Error).message)
                }
            }
        }
    }
}