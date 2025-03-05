package com.example.task3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.task3.ui.HomeScreen
import com.example.task3.ui.PlayerScreen
import com.example.task3.ui.LoadingScreen
import com.example.task3.ui.theme.MusicAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicAppTheme {
                MusicApp()
            }
        }
    }
}

@Composable
fun MusicApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "loading") {
        composable("loading") { LoadingScreen(navController) }
        composable("home") { HomeScreen(navController = navController) }
        composable("player/{trackIndex}") { backStackEntry ->
            val trackIndex = backStackEntry.arguments?.getString("trackIndex")?.toIntOrNull() ?: 0
            PlayerScreen(navController, trackIndex)
        }
    }
}

