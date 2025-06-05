package com.example.khizana_user.presentation.nav

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        ScreenRoute.Home,
        ScreenRoute.Favorites,
        ScreenRoute.Cart,
        ScreenRoute.Settings
    )

    NavigationBar(containerColor = Color.White) {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { screen.icon?.let { Icon(it, contentDescription = screen.label) } },
                label = { Text(screen.label ?: "") },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(ScreenRoute.Home.route)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}