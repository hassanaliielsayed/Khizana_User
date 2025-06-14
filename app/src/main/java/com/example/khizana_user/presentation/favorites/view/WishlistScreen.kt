package com.example.khizana_user.presentation.favorites.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.presentation.favorites.viewmodel.WishlistViewModel
import com.example.khizana_user.presentation.home.view.NoInternetConnectionView
import com.example.khizana_user.presentation.nav.ScreenRoute
import com.example.khizana_user.utils.Result
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    customerId: Long,
    navController: NavController,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val favoritesState by viewModel.favoritesState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val removingIds = remember { mutableStateListOf<Long>() }

    var showClearDialog by remember { mutableStateOf(false) }
    var confirmRemoveItem by remember { mutableStateOf<FavoriteItem?>(null) }

    val connectionState by viewModel.networkState.collectAsState()

    LaunchedEffect(customerId) {
        if (connectionState) {
            viewModel.loadFavorites(customerId)
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Favorites") },
            text = { Text("Are you sure you want to remove all items?") },
            confirmButton = {
                TextButton(onClick = {
                    showClearDialog = false
                    coroutineScope.launch {
                        viewModel.clearFavorites(customerId)
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("No") }
            }
        )
    }

    // Confirm Remove Dialog
    confirmRemoveItem?.let { item ->
        AlertDialog(
            onDismissRequest = { confirmRemoveItem = null },
            title = { Text("Remove Item") },
            text = { Text("Do you want to remove ${item.title}?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmRemoveItem = null
                    removingIds.add(item.variantId)
                    coroutineScope.launch {
                        val result = viewModel.removeFromFavorites(customerId, item.variantId)
                        removingIds.remove(item.variantId)

                        if (result is Result.Error) {
                            println("Failed to remove item: ${result.message}")
                        }
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { confirmRemoveItem = null }) { Text("No") }
            }
        )
    }
    if (!connectionState) {
        NoInternetConnectionView()
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Wishlist") },
                    actions = {
                        if (!favoritesState?.items.isNullOrEmpty()) {
                            TextButton(onClick = { showClearDialog = true }) {
                                Text("Clear All")
                            }
                        }
                    }
                )
            }
        ) { padding ->
            val items = favoritesState?.items?.filterNotNull().orEmpty()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (val state = favoritesState) {
                    null -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    else -> {
                        val items = state.items?.filterNotNull().orEmpty()

                        if (items.isEmpty()) {
                            Text(
                                "No favorites found",
                                modifier = Modifier.align(Alignment.Center),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        } else {
                            LazyColumn {
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
        elevation = CardDefaults.cardElevation()
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
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                Text(text = "Price: $${item.price}")
                Text(text = "Quantity: ${item.quantity}")
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                IconButton(onClick = onRemoveClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
