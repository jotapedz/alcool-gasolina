package com.example.alcoolgasolina

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
import com.example.alcoolgasolina.data.FuelPreferencesRepository
import com.example.alcoolgasolina.ui.theme.AlcoolGasolinaTheme
import com.example.alcoolgasolina.view.FuelStationScreen
import com.example.alcoolgasolina.view.PostoDetailView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AlcoolGasolinaTheme {
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
