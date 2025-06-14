@file:Suppress("UNREACHABLE_CODE")

package com.example.khizana_user.presentation.nav

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.khizana_user.presentation.auth.view.LoginScreen
import com.example.khizana_user.presentation.auth.view.RegisterScreen
import com.example.khizana_user.presentation.auth.view.VerifyEmailScreen
import com.example.khizana_user.presentation.category.view.CategoryScreen
import com.example.khizana_user.presentation.auth.viewmodel.AuthViewModel
import com.example.khizana_user.presentation.cart.view.CartScreen
import com.example.khizana_user.presentation.cart.view.CheckoutScreen
import com.example.khizana_user.presentation.cart.view.OrderSuccessScreen
import com.example.khizana_user.presentation.cart.viewmodel.LocationViewModel
import com.example.khizana_user.presentation.favorites.view.WishlistScreen
import com.example.khizana_user.presentation.home.view.HomeScreen
import com.example.khizana_user.presentation.cart.view.MapScreen
import com.example.khizana_user.presentation.order.view.OrdersScreen
import com.example.khizana_user.presentation.productdetails.view.ProductDetailsScreen
import com.example.khizana_user.presentation.profile.view.ProfileScreen
import com.example.khizana_user.presentation.setting.view.AboutUs
import com.example.khizana_user.presentation.setting.view.ContactsScreen
import com.example.khizana_user.presentation.setting.view.SettingScreen
import com.example.khizana_user.utils.isGuestUser
import com.google.android.gms.maps.model.LatLng

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
                    navController.navigate("verify_email") {
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
                if (customer != null && !isGuestUser()) {
                    WishlistScreen(
                        customerId = customer.id,
                        navController = navController
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate(ScreenRoute.Login.route) {
                            popUpTo(ScreenRoute.Favorites.route) { inclusive = true }
                        }
                    }
                    Text("Redirecting to login...", modifier = Modifier.padding(innerPadding))
                }
            }
        }

        composable(ScreenRoute.Cart.route) {
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { innerPadding ->
                val customer = authViewModel.currentCustomer.collectAsStateWithLifecycle().value

                if (customer != null && !isGuestUser()) {
                    CartScreen(
                        customerId = customer.id,
                        viewModel = hiltViewModel(),
                        modifier = Modifier.padding(innerPadding),
                        onCheckoutClick = { totalPrice ->
                            navController.navigate("checkout/${customer.id}/$totalPrice")
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate(ScreenRoute.Login.route) {
                            popUpTo(ScreenRoute.Cart.route) { inclusive = true }
                        }
                    }
                    Text("Redirecting to login...", modifier = Modifier.padding(innerPadding))
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

        composable(ScreenRoute.Profile.route) {
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { innerPadding ->
                ProfileScreen(
                    customerId = 7858653888625,
                    modifier = Modifier.padding(innerPadding),
                    navController = navController
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

        composable("order_success") {
            OrderSuccessScreen(
                onBackToHomeClick = {
                    navController.popBackStack("home", inclusive = false)
                },
                onContactUsClick = {
                    navController.navigate("contact")
                }
            )
        }

        composable("checkout/{customerId}/{totalPrice}",
            arguments = listOf(
                navArgument("customerId") { type = NavType.LongType },
                navArgument("totalPrice") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getLong("customerId") ?: return@composable
            val totalPrice = backStackEntry.arguments?.getString("totalPrice")?.toDoubleOrNull() ?: 0.0

            val locationViewModel: LocationViewModel = hiltViewModel()

            val selectedLocation = backStackEntry.savedStateHandle
                .getLiveData<Pair<LatLng, String>>("selected_location")
                .observeAsState()

            selectedLocation.value?.let { (latLng, address) ->
                LaunchedEffect(latLng) {
                    locationViewModel.updateAddress(address, latLng)
                    backStackEntry.savedStateHandle.remove<Pair<LatLng, String>>("selected_location")
                }
            }

            CheckoutScreen(
                customerId = customerId,
                totalPrice = totalPrice,
                onBackClick = { navController.popBackStack() },
                onPlaceOrderClick = {},
                onAddressClick = {
                    navController.navigate("map")
                },
                onPaymentMethodClick = {},
                onNavigateToOrderSuccess = {
                    navController.navigate("order_success")
                }
            )
        }


        composable("map") {
            MapScreen(
                navController = navController
            )
        }

        composable("verify_email") {
            VerifyEmailScreen(
                onLoginComplete = {
                    Log.d("AppNavGraph", "Email verified. Navigating to Home.")
                    navController.navigate(ScreenRoute.Home.route) {
                        popUpTo("verify_email") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate(ScreenRoute.Login.route) {
                        popUpTo("verify_email") { inclusive = true }
                    }
                }
            )
        }
//https://mad45-sv-and4.myshopify.com/admin/api/2024-01/orders.json?customer_id=7858653888625&status=any
        composable(ScreenRoute.Orders.route) {
            OrdersScreen(customerId = 7858653888625)
        }

    }
}
