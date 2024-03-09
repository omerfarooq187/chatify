package com.example.chatify.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.core.graphics.toColorInt
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    MainScreenContents()
}
sealed class NavigationScreen(val route:String) {
    data object ChatScreen: NavigationScreen("chat")
    data object ProfileScreen: NavigationScreen("profile")
}
@Composable
fun MainScreenContents() {
    var selectedItem by remember {
        mutableIntStateOf(0)
    }
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color("#ADD8E6".toColorInt())
            ) {
                navigationItems.forEachIndexed { index, navigationItem ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(navigationItem.screen) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = {
                            Text(text = navigationItem.title)
                        },
                        alwaysShowLabel = false,
                        icon = {
                            BadgedBox(badge = {
                                if (navigationItem.badges != null) {
                                    Badge { Text(text = navigationItem.badges.toString()) }
                                }
                            }) {
                                if (selectedItem == index) {
                                    Icon(
                                        imageVector = navigationItem.selectedIcon,
                                        contentDescription = navigationItem.title
                                    )
                                } else Icon(
                                    imageVector = navigationItem.unSelectedIcon,
                                    contentDescription = navigationItem.title
                                )
                            }
                        }
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it.calculateTopPadding())
        ) {
            NavHost(navController = navController, startDestination = NavigationScreen.ChatScreen.route) {
                composable(NavigationScreen.ChatScreen.route) {
                    ChatScreen()
                }
                composable(NavigationScreen.ProfileScreen.route) {
                    ProfileScreen()
                }
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val badges: Int? = null,
    val screen: String
)

val navigationItems = listOf(
    NavigationItem(
        "Chats",
        Icons.AutoMirrored.Filled.Chat,
        Icons.AutoMirrored.Outlined.Chat,
        3,
        NavigationScreen.ChatScreen.route
    ),
    NavigationItem(
        "Profile",
        Icons.Filled.Person,
        Icons.Outlined.Person,
        screen = NavigationScreen.ProfileScreen.route
    )
)

@Composable
fun ProfileScreen() {
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Back",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Save",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ChatScreen() {
    Column {
        Text(text = "This is chat screen")
    }
}