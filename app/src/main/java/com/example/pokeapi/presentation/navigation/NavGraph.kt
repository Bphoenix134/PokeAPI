package com.example.pokeapi.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pokeapi.presentation.ui.PokemonDetailScreen
import com.example.pokeapi.presentation.ui.PokemonFilterScreen
import com.example.pokeapi.presentation.ui.PokemonListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            PokemonListScreen(
                navigateToDetail = { name -> navController.navigate("detail/$name") },
                navigateToFilter = { navController.navigate("filter") }
            )
        }
        composable("detail/{name}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            PokemonDetailScreen(
                name = name,
                navigateBack = { navController.popBackStack() }
            )
        }
        composable("filter") {
            PokemonFilterScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}