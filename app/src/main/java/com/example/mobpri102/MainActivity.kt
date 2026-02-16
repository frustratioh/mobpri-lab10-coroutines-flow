package com.example.mobpri102

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobpri102.ui.theme.CoroutinesFlowLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoroutinesFlowLabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("coroutines") { CoroutinesScreen() }
        composable("flow") { FlowScreen() }
        composable("stateflow") { StateFlowScreen() }
        composable("sharedflow") { SharedFlowScreen() }
        composable("errorhandling") { ErrorHandlingScreen() }
    }
}

@Composable
fun HomeScreen(navController: androidx.navigation.NavController) {
    val screens = listOf(
        "coroutines" to "Базовые корутины",
        "flow" to "Flow",
        "stateflow" to "StateFlow",
        "sharedflow" to "SharedFlow",
        "errorhandling" to "Обработка ошибок"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(screens) { (route, title) ->
            Button(
                onClick = { navController.navigate(route) },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp)
            ) {
                Text(title)
            }
        }
    }
}