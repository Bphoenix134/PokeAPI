package com.example.pokeapi.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.pokeapi.presentation.navigation.NavGraph
import com.example.pokeapi.ui.theme.PokeAPITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokeAPITheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}