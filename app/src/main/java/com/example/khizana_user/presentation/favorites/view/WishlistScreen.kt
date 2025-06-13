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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    customerId: Long,
    navController: NavController,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val favorites by viewModel.favoritesState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val connectionState by viewModel.networkState.collectAsState()

    LaunchedEffect(customerId) {
        if (connectionState) {
            viewModel.loadFavorites(customerId)
        }
    }

    if (!connectionState) {
        NoInternetConnectionView()
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Favorites") },
                    actions = {
                        val items = favorites?.items.orEmpty().filterNotNull()
                        if (items.isNotEmpty()) {
                            TextButton(onClick = { viewModel.clearFavorites(customerId) }) {
                                Text("Clear All")
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                val items = favorites?.items.orEmpty().filterNotNull()

                when {
                    favorites == null -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    items.isEmpty() -> {
                        Text("No favorites found.", modifier = Modifier.align(Alignment.Center))
                    }

                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(items) { item ->
                                FavoriteItemCard(
                                    item = item,
                                    onRemoveClick = {
                                        coroutineScope.launch {
                                            viewModel.toggleFavorite(
                                                customerId = customerId,
                                                variantId = item.variantId,
                                                isCurrentlyFavorite = true
                                            )
                                        }
                                    },
                                    onItemClick = {
                                        navController.navigate(
                                            ScreenRoute.ProductDetails.createRoute(variantId = item.variantId)
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
    onRemoveClick: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onItemClick() },
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
                Text(text = "Variant ID: ${item.variantId}")
                Text(text = "Quantity: ${item.quantity}")
            }

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
