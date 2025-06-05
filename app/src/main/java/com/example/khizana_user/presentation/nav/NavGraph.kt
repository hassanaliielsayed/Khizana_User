package com.example.khizana_user.presentation.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.khizana_user.presentation.auth.view.LoginScreen
import com.example.khizana_user.presentation.auth.view.RegisterScreen
import com.example.khizana_user.presentation.home.view.HomeScreen
import com.example.khizana_user.presentation.productdetails.view.ProductDetailsScreen
import com.example.khizana_user.presentation.setting.view.AboutUs
import com.example.khizana_user.presentation.setting.view.ContactsScreen
import com.example.khizana_user.presentation.setting.view.SettingScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ScreenRoute.Login.route) {

        composable(ScreenRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(ScreenRoute.Home.route) {
                        popUpTo(ScreenRoute.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(ScreenRoute.Register.route)
                }
            )
        }

        composable(ScreenRoute.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(ScreenRoute.Home.route) {
                        popUpTo(ScreenRoute.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack(ScreenRoute.Login.route, inclusive = false)
                }
            )
        }

        composable(ScreenRoute.Home.route) {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                HomeScreen(navController = navController, paddingValues = innerPadding)
            }
        }

        composable(ScreenRoute.Favorites.route) {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                // TODO: Replace with your FavoritesScreen
                Text("Favorites", modifier = Modifier.padding(innerPadding))
            }
        }

        composable(ScreenRoute.Cart.route) {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                // TODO: Replace with your CartScreen
                Text("Cart", modifier = Modifier.padding(innerPadding))
            }
        }

        composable(ScreenRoute.Settings.route) {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                SettingScreen(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    onContactUsClick = { navController.navigate("contact") },
                    onAboutUsClick = { navController.navigate("about") }
                )
            }
        }

        composable(ScreenRoute.ProductDetails.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            id?.let {
                ProductDetailsScreen(productId = it)
            }
        }

        composable("contact") {
            ContactsScreen()
        }

        composable("about") {
            AboutUs()
        }
    }
}
