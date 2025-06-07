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
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favorites by wishlistViewModel.favoritesState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(customerId) {
        Log.d("ProductDetails", "Loading favorites for customerId: $customerId")
        wishlistViewModel.loadFavorites(customerId)
    }

    val isInitiallyFavorite = remember(favorites, variantId, productId) {
        val id = variantId ?: productId
        val fav = favorites?.items?.any { it!!.variantId == id } ?: false
        Log.d("ProductDetails", "Initial favorite check for ID $id: $fav")
        fav
    }

    var isFavorite by remember { mutableStateOf(isInitiallyFavorite) }

    LaunchedEffect(productId, variantId) {
        when {
            productId != null -> {
                Log.d("ProductDetails", "Loading product by productId: $productId")
                viewModel.loadProduct(productId)
            }
            variantId != null -> {
                Log.d("ProductDetails", "Loading product by variantId: $variantId")
                viewModel.loadProductByVariant(variantId)
            }
        }
    }

    when (val result = state) {
        is ProductDetailsViewModel.Result.Loading -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        is ProductDetailsViewModel.Result.Error -> Text("Error: ${result.message}", color = Color.Red)
        is ProductDetailsViewModel.Result.Success -> {
            ProductDetailsContent(
                product = result.data,
                isFavorite = isFavorite,
                onToggleFavorite = {
                    val id = result.data.variantId
                    if (id == null) {
                        Log.e("ProductDetails", "VariantId is null. Cannot toggle favorite.")
                        return@ProductDetailsContent
                    }
                    val alreadyInFavorites = favorites?.items?.any { it!!.variantId == id } ?: false
                    if (!isFavorite && !alreadyInFavorites) {
                        isFavorite = true
                        wishlistViewModel.addToFavorites(customerId, id)
                    } else if (isFavorite && alreadyInFavorites) {
                        isFavorite = false
                        wishlistViewModel.removeFromFavorites(customerId, id)
                    }
                },
                onAddToCart = {
                    val id = result.data.variantId
                    if (id == null) {
                        Log.e("ProductDetails", "VariantId is null. Cannot add to cart.")
                        return@ProductDetailsContent
                    }
                    cartViewModel.addToCart(customerId, id)
                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun ProductDetailsContent(
    product: ProductDetails,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onAddToCart: () -> Unit
) {
    var selectedSize by remember { mutableStateOf<String?>(null) }
    var selectedColor by remember { mutableStateOf<String?>(null) }

    val colorMap = mapOf(
        "black" to Color(0xFF000000),
        "white" to Color(0xFFFFFFFF),
        "blue" to Color(0xFF2196F3),
        "red" to Color(0xFFF44336),
        "orange" to Color(0xFFFF9800),
        "green" to Color(0xFF4CAF50),
        "magenta" to Color(0xFFFF00FF)
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

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(product.images.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (selected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (selected) Color.Black else Color.LightGray)
                    )
                }
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.Black
                )
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
            Text("  (${product.rating})", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("$${product.price.toCurrentCurrency()}", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E88E5))
        Spacer(modifier = Modifier.height(16.dp))

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
                            .border(2.dp, if (selectedColor == colorName) Color.Black else Color.Transparent, CircleShape)
                            .clickable {
                                selectedColor = colorName
                                Log.d("ProductDetails", "Selected color: $colorName")
                            }
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
                            .clickable {
                                selectedSize = size
                                Log.d("ProductDetails", "Selected size: $size")
                            }
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