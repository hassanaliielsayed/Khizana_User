package com.example.khizana_user.presentation.favorites.view

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.presentation.favorites.viewmodel.WishlistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    customerId: Long,
    navController: NavController,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val favorites by viewModel.favoritesState.collectAsState()

    LaunchedEffect(customerId) {
        viewModel.loadFavorites(customerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Favorites") },
                actions = {
                    if (!favorites?.items.isNullOrEmpty()) {
                        TextButton(onClick = { viewModel.clearFavorites(customerId) }) {
                            Text("Clear All")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {

            when {
                favorites == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                favorites!!.items.isEmpty() -> {
                    Text("No favorites found.", modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(favorites!!.items) { item ->
                            if (item != null) {
                                FavoriteItemCard(
                                    item = item,
                                    onRemoveClick = {
                                        viewModel.removeFromFavorites(customerId, item.variantId)
                                    },
                                    onItemClick = {
                                        navController.navigate("productDetails/${item.variantId}")
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
