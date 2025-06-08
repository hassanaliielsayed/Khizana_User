@file:Suppress("UNREACHABLE_CODE")

package com.example.khizana_user.presentation.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.khizana_user.presentation.auth.view.LoginScreen
import com.example.khizana_user.presentation.auth.view.RegisterScreen
import com.example.khizana_user.presentation.category.view.CategoryScreen
import com.example.khizana_user.presentation.auth.viewmodel.AuthViewModel
import com.example.khizana_user.presentation.cart.view.CartScreen
import com.example.khizana_user.presentation.cart.view.CheckoutScreen
import com.example.khizana_user.presentation.favorites.view.WishlistScreen
import com.example.khizana_user.presentation.home.view.HomeScreen
import com.example.khizana_user.presentation.productdetails.view.ProductDetailsScreen
import com.example.khizana_user.presentation.setting.view.AboutUs
import com.example.khizana_user.presentation.setting.view.ContactsScreen
import com.example.khizana_user.presentation.setting.view.SettingScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val customer = authViewModel.currentCustomer.collectAsStateWithLifecycle().value

    val startDestination = if (customer != null) ScreenRoute.Home.route else ScreenRoute.Login.route

    NavHost(navController = navController, startDestination = startDestination) {

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
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { innerPadding ->
                HomeScreen(navController = navController,
                    paddingValues = innerPadding,
                    onNavigateToFavorites = {
                        navController.navigate(ScreenRoute.Favorites.route)
                    },
                    onNavigateToCart = {
                        navController.navigate(ScreenRoute.Cart.route)
                    })
            }
        }

        composable(ScreenRoute.Category.route) {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                CategoryScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToFavorites = {
                        navController.navigate(ScreenRoute.Favorites.route)
                    },
                    onNavigateToCart = {
                        navController.navigate(ScreenRoute.Cart.route)
                    }
                )
            }
        }

        composable(ScreenRoute.Favorites.route) {
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { innerPadding ->
                if (customer != null) {
                    WishlistScreen(
                        customerId = customer.id,
                        navController = navController
                    )
                } else {
                    Text("Please log in to view favorites", modifier = Modifier.padding(innerPadding))
                }
            }
        }

        composable(ScreenRoute.Cart.route) {
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { innerPadding ->
                val customer = authViewModel.currentCustomer.collectAsStateWithLifecycle().value
                if (customer != null) {
                    CartScreen(
                        customerId = customer.id,
                        viewModel = hiltViewModel(),
                        modifier = Modifier.padding(innerPadding),
                        onCheckoutClick = { totalPrice ->
                            navController.navigate("checkout/$totalPrice") }
                    )
                } else {
                    Text("Please log in to access your cart", modifier = Modifier.padding(innerPadding))
                }
            }
        }

        composable(ScreenRoute.Settings.route) {
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { innerPadding ->
                SettingScreen(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    onContactUsClick = { navController.navigate("contact") },
                    onAboutUsClick = { navController.navigate("about") }
                )
            }
        }

        composable(
            route = ScreenRoute.ProductDetails.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument("variantId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId")?.takeIf { it != -1L }
            val variantId = backStackEntry.arguments?.getLong("variantId")?.takeIf { it != -1L }

            if (customer != null) {
                ProductDetailsScreen(
                    productId = productId,
                    variantId = variantId,
                    customerId = customer.id
                )
            } else {
                Text("User not logged in")
            }
        }



        composable("contact") {
            ContactsScreen()
        }

        composable("about") {
            AboutUs()
        }

        composable("checkout/{totalPrice}") { backStackEntry ->
            val totalPrice = backStackEntry.arguments?.getString("totalPrice")?.toDoubleOrNull() ?: 0.0
            CheckoutScreen(
                totalPrice = totalPrice,
                onBackClick = { navController.popBackStack() },
                onPlaceOrderClick = {},
                onAddressClick = {  },
                onPaymentMethodClick = { },
            )
        }
    }
}
