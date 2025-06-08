package com.example.khizana_user.presentation.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute(
    val route: String,
    @Contextual val icon: ImageVector? = null,
    val label: String? = null
) {
    @Serializable object Login : ScreenRoute("login", Icons.Default.Login, "Login")
    @Serializable object Register : ScreenRoute("register", Icons.Default.Login, "Register")
    @Serializable object Home : ScreenRoute("home", Icons.Default.Home, "Home")
    @Serializable object Favorites : ScreenRoute("favorites", Icons.Default.Favorite, "Favorites")
    @Serializable object Cart : ScreenRoute("cart", Icons.Default.ShoppingCart, "Cart")
    @Serializable object Settings : ScreenRoute("settings", Icons.Default.Settings, "Settings")
    @Serializable object Category : ScreenRoute("category", Icons.Default.Category, "Category")

    @Serializable
    object ProductDetails : ScreenRoute("productDetails?productId={productId}&variantId={variantId}") {
        fun createRoute(productId: Long? = null, variantId: Long? = null): String {
            val params = mutableListOf<String>()
            productId?.let { params.add("productId=$it") }
            variantId?.let { params.add("variantId=$it") }
            return "productDetails?${params.joinToString("&")}"
        }
    }

}
