package com.example.pokeapi.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pokeapi.presentation.ui.screen.PokemonDetailScreen
import com.example.pokeapi.presentation.ui.screen.PokemonListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            PokemonListScreen(
                navigateToDetail = { name -> navController.navigate("detail/$name") }
            )
        }
        composable("detail/{name}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            PokemonDetailScreen(
                name = name,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}