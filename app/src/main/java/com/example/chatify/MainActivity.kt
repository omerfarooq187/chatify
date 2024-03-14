package com.example.chatify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatify.screens.LoginScreen
import com.example.chatify.screens.MainScreen
import com.example.chatify.screens.MessagesScreen
import com.example.chatify.screens.NavigationScreen
import com.example.chatify.screens.SignupScreen
import com.example.chatify.ui.theme.ChatifyTheme
import com.example.chatify.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

sealed class DestinationScreen(val route:String) {
    data object MainScreen: DestinationScreen("main")
    data object SignupScreen: DestinationScreen("signup")
    data object LoginScreen: DestinationScreen("login")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color.Transparent.toArgb()
            )
        )
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            ChatifyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(viewModel)
                }
            }
        }
    }
}

@Composable
fun App(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = DestinationScreen.SignupScreen.route) {
        composable(DestinationScreen.SignupScreen.route) {
            SignupScreen(viewModel,navController)
        }
        composable(DestinationScreen.LoginScreen.route) {
            LoginScreen(viewModel, navController)
        }
        composable(DestinationScreen.MainScreen.route) {
            MainScreen(viewModel, navController)
        }

        composable("${NavigationScreen.MessagesScreen.route}/{userId}") { backStackEntry ->
            MessagesScreen(backStackEntry.arguments?.getString("userId") ?: "",navController, viewModel)
        }

    }
}