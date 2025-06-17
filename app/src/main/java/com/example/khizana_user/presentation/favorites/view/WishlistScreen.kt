package com.example.khizana_user.presentation.favorites.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.khizana_user.R
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.presentation.AppLogo
import com.example.khizana_user.presentation.TopBarIconButton
import com.example.khizana_user.presentation.favorites.viewmodel.WishlistViewModel
import com.example.khizana_user.presentation.home.view.NoInternetConnectionView
import com.example.khizana_user.presentation.nav.ScreenRoute
import com.example.khizana_user.presentation.profile.view.EmptyState
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.customFontFamily
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    customerId: Long,
    navController: NavController,
    viewModel: WishlistViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val favoritesState by viewModel.favoritesState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val removingIds = remember { mutableStateListOf<Long>() }
    val isLoading by viewModel.loading.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }
    var confirmRemoveItem by remember { mutableStateOf<FavoriteItem?>(null) }

    val connectionState by viewModel.networkState.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(customerId) {
        if (connectionState) {
            viewModel.loadFavorites(customerId)
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(
                stringResource(R.string.clear_all_favorites),
                fontFamily = customFontFamily
            ) },
            text = { Text(stringResource(R.string.are_you_sure_you_want_to_remove_all_items),  fontFamily = customFontFamily,) },
            confirmButton = {
                TextButton(onClick = {
                    showClearDialog = false
                    coroutineScope.launch {
                        viewModel.clearFavorites(customerId)
                    }
                }) { Text(stringResource(R.string.yes),  fontFamily = customFontFamily,) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.no),  fontFamily = customFontFamily,) }
            }
        )
    }

    confirmRemoveItem?.let { item ->
        AlertDialog(
            onDismissRequest = { confirmRemoveItem = null },
            title = { Text(stringResource(R.string.remove_item),  fontFamily = customFontFamily,) },
            text = { Text(stringResource(R.string.do_you_want_to_remove, item.title),  fontFamily = customFontFamily,) },
            confirmButton = {
                TextButton(onClick = {
                    confirmRemoveItem = null
                    removingIds.add(item.variantId)
                    coroutineScope.launch {
                        val result = viewModel.removeFromFavorites(customerId, item.variantId)
                        removingIds.remove(item.variantId)

                        if (result is Result.Error) {
                            println(
                                context.getString(
                                    R.string.failed_to_remove_item,
                                    result.message
                                ))
                        }
                    }
                }) { Text(stringResource(R.string.yes),  fontFamily = customFontFamily,) }
            },
            dismissButton = {
                TextButton(onClick = { confirmRemoveItem = null }) { Text(stringResource(R.string.no),  fontFamily = customFontFamily,) }
            }
        )
    }

    if (!connectionState) {
        NoInternetConnectionView()
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        AppLogo()
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(id = R.color.light_blue)
                    ),
                    actions = {
                        TopBarIconButton(
                            icon = Icons.Default.Home,
                            contentDescription = stringResource(R.string.favorites),
                            onClick = onNavigateToHome
                        )
                        TopBarIconButton(
                            icon = Icons.Default.ShoppingCart,
                            contentDescription = stringResource(R.string.shopping_cart),
                            onClick = onNavigateToCart
                        )
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                val items = favoritesState?.items?.filterNotNull().orEmpty()

                when {
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    items.isEmpty() ->  {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            EmptyState(
                                animationRes = R.raw.no_data,
                                message = "Your wishlist is empty\nAdd items you love to see them here"
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(items, key = { it.variantId }) { item ->
                                FavoriteItemCard(
                                    item = item,
                                    isLoading = removingIds.contains(item.variantId),
                                    onRemoveClick = { confirmRemoveItem = item },
                                    onItemClick = {
                                        navController.navigate(
                                            ScreenRoute.ProductDetails.createRoute(
                                                variantId = item.variantId
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteItemCard(
    item: FavoriteItem,
    isLoading: Boolean = false,
    onRemoveClick: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(enabled = !isLoading) { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = customFontFamily,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${item.price}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = customFontFamily,
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Qty: ${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontFamily = customFontFamily,
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.remove_item),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
