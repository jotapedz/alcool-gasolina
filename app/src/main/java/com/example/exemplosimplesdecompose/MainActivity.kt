package com.example.exemplosimplesdecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.exemplosimplesdecompose.data.FuelPreferencesRepository
import com.example.exemplosimplesdecompose.ui.theme.ExemploSimplesDeComposeTheme
import com.example.exemplosimplesdecompose.view.AlcoolGasolinaPreco
import com.example.exemplosimplesdecompose.view.PostoDetailView
import com.example.exemplosimplesdecompose.view.Welcome

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExemploSimplesDeComposeTheme {
                val navController: NavHostController = rememberNavController()
                val repository = remember { FuelPreferencesRepository(applicationContext) }

                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") { Welcome(navController) }
                    composable("main") { AlcoolGasolinaPreco(navController, repository) }
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

@Composable
fun Greeting(name: String) {
    androidx.compose.material3.Text(text = stringResource(R.string.hello_name, name))
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExemploSimplesDeComposeTheme {
        Greeting("Android")
    }
}
