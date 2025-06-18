package com.example.khizana_user.presentation.favorites.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.khizana_user.R
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.presentation.AppLogo
import com.example.khizana_user.presentation.TopBarIconButton
import com.example.khizana_user.presentation.favorites.viewmodel.WishlistViewModel
import com.example.khizana_user.presentation.home.view.NoInternetConnectionView
import com.example.khizana_user.presentation.home.view.SharedModifiers
import com.example.khizana_user.presentation.nav.ScreenRoute
import com.example.khizana_user.presentation.profile.view.EmptyState
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.customFontFamily
import com.example.khizana_user.utils.toCurrentCurrency
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    customerId: Long,
    navController: NavController,
    viewModel: WishlistViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToCard: () -> Unit
) {
    val favoritesState by viewModel.favoritesState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val removingIds = remember { mutableStateListOf<Long>() }

    var showClearDialog by remember { mutableStateOf(false) }
    var confirmRemoveItem by remember { mutableStateOf<FavoriteItem?>(null) }

    val connectionState by viewModel.networkState.collectAsState()

    val context = LocalContext.current

    val isLoading by viewModel.loading.collectAsState()

    LaunchedEffect(customerId) {
        if (connectionState) {
            viewModel.loadFavorites(customerId)
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.clear_all_favorites), fontFamily = customFontFamily) },
            text = { Text(stringResource(R.string.are_you_sure_you_want_to_remove_all_items),fontFamily = customFontFamily) },
            confirmButton = {
                TextButton(onClick = {
                    showClearDialog = false
                    coroutineScope.launch {
                        viewModel.clearFavorites(customerId)
                    }
                }) { Text(stringResource(R.string.yes),fontFamily = customFontFamily) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.no),fontFamily = customFontFamily) }
            }
        )
    }

    // Confirm Remove Dialog
    confirmRemoveItem?.let { item ->
        AlertDialog(
            onDismissRequest = { confirmRemoveItem = null },
            title = { Text(stringResource(R.string.remove_item),fontFamily = customFontFamily) },
            text = { Text(stringResource(R.string.do_you_want_to_remove, item.title),fontFamily = customFontFamily) },
            confirmButton = {
                TextButton(onClick = {
                    confirmRemoveItem = null
                    removingIds.add(item.variantId)
                    coroutineScope.launch {
                        val result = viewModel.removeFromFavorites(customerId, item.variantId)
                        removingIds.remove(item.variantId)

                        if (result is Result.Error) {
                            println(context.getString(R.string.failed_to_remove_item, result.message))
                        }
                    }
                }) { Text(stringResource(R.string.yes),fontFamily = customFontFamily) }
            },
            dismissButton = {
                TextButton(onClick = { confirmRemoveItem = null }) { Text(stringResource(R.string.no),fontFamily = customFontFamily) }
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
                            contentDescription = stringResource(R.string.home),
                            onClick = onNavigateToHome
                        )
                        TopBarIconButton(
                            icon = Icons.Default.ShoppingCart,
                            contentDescription = stringResource(R.string.shopping_cart),
                            onClick = onNavigateToCard
                        )
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {

                val items = favoritesState?.items?.filterNotNull().orEmpty()

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.your_favorites),
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = customFontFamily
                        )

                        TextButton(
                            onClick = { showClearDialog = true }
                        ) {
                            Text(
                                stringResource(R.string.clear_all_favorites),
                                fontFamily = customFontFamily
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    when {
                        isLoading -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        }
                        items.isEmpty() -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                EmptyState(
                                    animationRes = R.raw.no_data,
                                    message = stringResource(R.string.your_favorite_is_empty_add_items_you_love_to_see_them_here)
                                )
                            }
                        }
                        else -> {
                            FavoritesList(
                                items = items,
                                removingIds = removingIds,
                                onRemoveClick = { confirmRemoveItem = it },
                                onItemClick = {
                                    navController.navigate(
                                        ScreenRoute.ProductDetails.createRoute(
                                            variantId = it.variantId
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

@Composable
private fun FavoritesList(
    items: List<FavoriteItem>,
    removingIds: List<Long>,
    onRemoveClick: (FavoriteItem) -> Unit,
    onItemClick: (FavoriteItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.variantId }) { item ->
            FavoriteItemCard(
                item = item,
                isLoading = removingIds.contains(item.variantId),
                onRemoveClick = { onRemoveClick(item) },
                onItemClick = { onItemClick(item) }
            )
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(enabled = !isLoading) { onItemClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.dark_blue).copy(alpha = 0.9f)
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
                modifier = SharedModifiers.circleImageModifier(80.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(colorResource(R.color.white))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    fontFamily = customFontFamily,
                    fontSize = 20.sp,
                    color = Color.White
                )

                Text(
                    text = stringResource(R.string.price, item.price.toCurrentCurrency()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = customFontFamily,
                    fontSize = 18.sp
                )

                Text(
                    text = stringResource(R.string.quantity, item.quantity),
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(R.color.content_color),
                    fontFamily = customFontFamily,
                    fontSize = 16.sp
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
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.remove_item),
                        tint = colorResource(R.color.content_color)
                    )
                }
            }
        }
    }
}