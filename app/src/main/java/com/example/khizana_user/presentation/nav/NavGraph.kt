package com.example.khizana_user.presentation.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.khizana_user.presentation.auth.view.LoginScreen
import com.example.khizana_user.presentation.auth.view.RegisterScreen
import com.example.khizana_user.presentation.home.view.HomeScreen
import com.example.khizana_user.presentation.productdetails.view.ProductDetailsScreen

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
            HomeScreen(navController = navController)
        }

        composable(ScreenRoute.ProductDetails.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            id?.let {
                ProductDetailsScreen(productId = it)
            }
        }
    }
}
