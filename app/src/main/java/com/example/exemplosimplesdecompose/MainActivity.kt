package com.example.exemplosimplesdecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.exemplosimplesdecompose.data.FuelPreferencesRepository
import com.example.exemplosimplesdecompose.ui.theme.ExemploSimplesDeComposeTheme
import com.example.exemplosimplesdecompose.view.FuelStationScreen
import com.example.exemplosimplesdecompose.view.PostoDetailView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ExemploSimplesDeComposeTheme {
                val navController: NavHostController = rememberNavController()
                val repository = remember { FuelPreferencesRepository(applicationContext) }

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        FuelStationScreen(
                            navController = navController,
                            repository = repository
                        )
                    }
                    composable(
                        route = "detail/{postoId}",
                        arguments = listOf(navArgument("postoId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        PostoDetailView(
                            navController = navController,
                            repository = repository,
                            postoId = backStackEntry.arguments?.getString("postoId").orEmpty()
                        )
                    }
                }
            }
        }
    }
}
