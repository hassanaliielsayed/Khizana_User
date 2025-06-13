package com.example.khizana_user.presentation.productdetails.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.khizana_user.domain.model.ProductDetails
import com.example.khizana_user.presentation.cart.viewmodel.CartViewModel
import com.example.khizana_user.presentation.favorites.viewmodel.WishlistViewModel
import com.example.khizana_user.presentation.productdetails.viewmodel.ProductDetailsViewModel
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.isGuestUser
import com.example.khizana_user.utils.toCurrentCurrency
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@Composable
fun ProductDetailsScreen(
    productId: Long? = null,
    variantId: Long? = null,
    customerId: Long,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val productState by viewModel.state.collectAsStateWithLifecycle()
    val favorites by wishlistViewModel.favoritesState.collectAsStateWithLifecycle()
    val favoriteStatus by wishlistViewModel.toggleFavoriteState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(customerId) {
        wishlistViewModel.loadFavorites(customerId)
    }

    LaunchedEffect(favorites, productId, variantId) {
        val id = variantId ?: productId ?: return@LaunchedEffect
        val isFav = favorites?.items?.any { it?.variantId == id } ?: false
        wishlistViewModel.setInitialFavoriteStatus(isFav)
    }

    LaunchedEffect(productId, variantId) {
        when {
            productId != null -> viewModel.loadProduct(productId)
            variantId != null -> viewModel.loadProductByVariant(variantId)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val result = productState) {
            is ProductDetailsViewModel.Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is ProductDetailsViewModel.Result.Error -> {
                Text("Error: ${result.message}", color = Color.Red, modifier = Modifier.align(Alignment.Center))
            }

            is ProductDetailsViewModel.Result.Success -> {
                val product = result.data
                ProductDetailsContent(
                    product = product,
                    favoriteStatus = favoriteStatus,
                    onToggleFavorite = {
                        val id = product.variantId ?: return@ProductDetailsContent
                        val current = (favoriteStatus as? Result.Success)?.data ?: false

                        if (isGuestUser()) {
                            Toast.makeText(context, "Please sign in to add to favorites", Toast.LENGTH_SHORT).show()
                        } else if (favoriteStatus !is Result.Loading) {
                            wishlistViewModel.toggleFavorite(customerId, id, current)
                        }
                    },
                    onAddToCart = {
                        val id = product.variantId ?: return@ProductDetailsContent

                        if (isGuestUser()) {
                            Toast.makeText(context, "Please sign in to add items to cart", Toast.LENGTH_SHORT).show()
                        } else {
                            cartViewModel.addToCart(customerId, id)
                            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                        }
                    }

                )
            }
        }
    }
}

@Composable
fun ProductDetailsContent(
    product: ProductDetails,
    favoriteStatus: Result<Boolean>,
    onToggleFavorite: () -> Unit,
    onAddToCart: () -> Unit
) {
    var selectedSize by remember { mutableStateOf<String?>(null) }
    var selectedColor by remember { mutableStateOf<String?>(null) }

    val colorMap = mapOf(
        "black" to Color.Black, "white" to Color.White, "blue" to Color(0xFF2196F3),
        "red" to Color(0xFFF44336), "orange" to Color(0xFFFF9800),
        "green" to Color(0xFF4CAF50), "magenta" to Color(0xFFFF00FF)
    )

    val pagerState = rememberPagerState(initialPage = 0)

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box {
            HorizontalPager(
                count = product.images.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) { page ->
                AsyncImage(
                    model = product.images[page],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
            ) {
                when (favoriteStatus) {
                    is Result.Loading -> CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )

                    is Result.Success -> {
                        val isFav = favoriteStatus.data
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFav) "Unfavorite" else "Favorite",
                            tint = if (isFav) Color.Red else Color.Black
                        )
                    }

                    is Result.Error -> Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(product.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) { i ->
                val filled = i < product.rating.toInt()
                Icon(Icons.Default.Star, contentDescription = null, tint = if (filled) Color(0xFFFFC107) else Color.LightGray)
            }
            Text("  (${product.rating})", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "$${product.price.toCurrentCurrency()}",
            fontSize = 20.sp,
            color = Color(0xFF1E88E5),
            fontWeight = FontWeight.SemiBold
        )

        if (product.colors.isNotEmpty()) {
            Text("Color", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(product.colors) { colorName ->
                    val colorHex = colorMap[colorName.lowercase()] ?: Color.Gray
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colorHex)
                            .border(
                                2.dp,
                                if (selectedColor == colorName) Color.Black else Color.Transparent,
                                CircleShape
                            )
                            .clickable { selectedColor = colorName }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (product.sizes.isNotEmpty()) {
            Text("Available Sizes", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(product.sizes) { size ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                2.dp,
                                if (size == selectedSize) Color(0xFF1E88E5) else Color.LightGray,
                                RoundedCornerShape(12.dp)
                            )
                            .background(if (size == selectedSize) Color(0xFF1E88E5) else Color.White)
                            .clickable { selectedSize = size }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(size, color = if (size == selectedSize) Color.White else Color.Black)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(product.description, fontSize = 14.sp, lineHeight = 20.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAddToCart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
        ) {
            Text("Add to Cart", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
