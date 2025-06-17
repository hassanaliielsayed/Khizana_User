package com.example.khizana_user.presentation.productdetails.view

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.khizana_user.domain.model.ProductDetails
import com.example.khizana_user.presentation.cart.viewmodel.CartViewModel
import com.example.khizana_user.presentation.favorites.viewmodel.WishlistViewModel
import com.example.khizana_user.presentation.home.view.NoInternetConnectionView
import com.example.khizana_user.presentation.productdetails.viewmodel.ProductDetailsViewModel
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.isGuestUser
import com.example.khizana_user.utils.toCurrentCurrency
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
fun ProductDetailsScreen(
    productId: Long? = null,
    variantId: Long? = null,
    customerId: Long,
    navController: NavController,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val productState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val connectionState by viewModel.networkState.collectAsState()

    var isFavorite by remember { mutableStateOf<Boolean?>(null) }
    var isToggling by remember { mutableStateOf(false) }

    var showGuestDialog by remember { mutableStateOf(false) }
    var guestAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    var selectedColor by remember { mutableStateOf<String?>(null) }
    var selectedSize by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(customerId) {
        if (connectionState) {
            wishlistViewModel.loadFavorites(customerId)
        }
    }

    LaunchedEffect(productId, variantId) {
        if (connectionState) {
            when {
                productId != null -> viewModel.loadProduct(productId)
                variantId != null -> viewModel.loadProductByVariant(variantId)
            }
        }
    }

    if (!connectionState) {
        NoInternetConnectionView()
    } else {
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

                    LaunchedEffect(product.variantId) {
                        isFavorite = wishlistViewModel.isFavorite(
                            customerId,
                            product.variantId ?: return@LaunchedEffect
                        )
                    }

                    ProductDetailsContent(
                        product = product,
                        isFavorite = isFavorite ?: false,
                        isToggling = isToggling || isFavorite == null,
                        selectedColor = selectedColor,
                        selectedSize = selectedSize,
                        onColorSelected = { selectedColor = it },
                        onSizeSelected = { selectedSize = it },
                        onToggleFavorite = {
                            val variantId = product.variantId ?: return@ProductDetailsContent

                            if (isGuestUser()) {
                                guestAction = {}
                                showGuestDialog = true
                                return@ProductDetailsContent
                            }

                            if (isToggling || isFavorite == null) return@ProductDetailsContent

                            isToggling = true
                            coroutineScope.launch {
                                val wasFavorite = isFavorite ?: false
                                val result = if (wasFavorite)
                                    wishlistViewModel.removeFromFavorites(customerId, variantId)
                                else
                                    wishlistViewModel.addToFavorites(customerId, variantId)

                                when (result) {
                                    is Result.Success<*> -> isFavorite = !wasFavorite
                                    is Result.Error -> Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                    else -> {}
                                }

                                isToggling = false
                            }
                        },
                        onAddToCart = {
                            val id = product.variantId ?: return@ProductDetailsContent
                            when {
                                isGuestUser() -> {
                                    guestAction = {}
                                    showGuestDialog = true
                                }
                                selectedColor.isNullOrBlank() || selectedSize.isNullOrBlank() -> {
                                    Toast.makeText(context, "Please select color and size", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    cartViewModel.addToCart(customerId, id)
                                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }

            if (showGuestDialog) {
                GuestLoginDialog(
                    onDismiss = { showGuestDialog = false },
                    onLoginClick = {
                        showGuestDialog = false
                        navController.navigate("login")
                    },
                    onContinueClick = {
                        showGuestDialog = false
                        guestAction?.invoke()
                    }
                )
            }
        }
    }
}

@Composable
fun ProductDetailsContent(
    product: ProductDetails,
    isFavorite: Boolean,
    isToggling: Boolean,
    selectedColor: String?,
    selectedSize: String?,
    onColorSelected: (String) -> Unit,
    onSizeSelected: (String) -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCart: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()
    val zoomScales = remember { mutableStateMapOf<Int, Float>() }

    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(3000)
            val next = (pagerState.currentPage + 1) % product.images.size
            pagerState.animateScrollToPage(next)
        }
    }

    val colorMap = mapOf(
        "black" to Color.Black, "white" to Color.White, "blue" to Color(0xFF2196F3),
        "red" to Color(0xFFF44336), "orange" to Color(0xFFFF9800),
        "green" to Color(0xFF4CAF50), "magenta" to Color(0xFFFF00FF)
    )

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(contentAlignment = Alignment.TopCenter) {
            Column {
                HorizontalPager(
                    count = product.images.size,
                    state = pagerState,
                    userScrollEnabled = (zoomScales[pagerState.currentPage] ?: 1f) <= 1f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) { page ->
                    var scale by remember { mutableStateOf(1f) }
                    var offsetX by remember { mutableStateOf(0f) }
                    var offsetY by remember { mutableStateOf(0f) }

                    zoomScales[page] = scale

                    AsyncImage(
                        model = product.images[page],
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale = (scale * zoom).coerceIn(1f, 4f)
                                    offsetX += pan.x
                                    offsetY += pan.y
                                }
                            }
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offsetX,
                                translationY = offsetY
                            )
                            .clickable {
                                selectedImageUrl = product.images[page]
                                showDialog = true
                            }
                    )
                }

                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp),
                    activeColor = Color.Black,
                    inactiveColor = Color.LightGray
                )
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(22.dp)
                    .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
            ) {
                if (isToggling) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color.Red else Color.Black
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
                            .clickable { onColorSelected(colorName) }
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
                            .clickable { onSizeSelected(size) }
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

    if (showDialog && selectedImageUrl != null) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black)
            ) {
                var scale by remember { mutableStateOf(1f) }
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }

                AsyncImage(
                    model = selectedImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 6f)
                                offsetX += pan.x
                                offsetY += pan.y
                                if (scale <= 1.01f) {
                                    scale = 1f
                                    offsetX = 0f
                                    offsetY = 0f
                                    showDialog = false
                                }
                            }
                        }
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                )
            }
        }
    }
}

@Composable
fun GuestLoginDialog(
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Login Required") },
        text = { Text("You need to log in to use this feature. Do you want to log in now?") },
        confirmButton = {
            TextButton(onClick = onLoginClick) {
                Text("Login")
            }
        },
        dismissButton = {
            TextButton(onClick = onContinueClick) {
                Text("Continue as Guest")
            }
        }
    )
}
