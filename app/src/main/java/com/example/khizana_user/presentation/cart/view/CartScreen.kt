package com.example.khizana_user.presentation.cart.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import com.example.khizana_user.utils.Result
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.presentation.cart.viewmodel.CartViewModel

@Composable
fun CartScreen(
    customerId: Long,
    viewModel: CartViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val cartResult by viewModel.cartState.collectAsStateWithLifecycle()
    var showClearCartDialog by remember { mutableStateOf(false) }


    LaunchedEffect(customerId) {
        viewModel.loadCart(customerId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            "🛒 Your Cart",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        when (val result = cartResult) {
            Result.Loading -> {
                CircularProgressIndicator()
            }
            is Result.Error -> {
                Text(
                    text = "Failed to load cart: ${result.message}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            is Result.Success -> {
                val cart = result.data
                if (cart.items.isEmpty()) {
                    Text("Your cart is empty.")
                } else {
                    Column {
                        LazyColumn {
                            items(cart.items) { item ->
                                item?.let {
                                    CartItemRow(
                                        item = it,
                                        onAdd = { viewModel.addToCart(customerId, item.variantId) },
                                        onRemove = { viewModel.decrementFromCart(customerId, item.variantId) }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { showClearCartDialog = true },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear cart")
                            Spacer(Modifier.width(8.dp))
                            Text("Clear Cart")
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    if (showClearCartDialog) {
        AlertDialog(
            onDismissRequest = { showClearCartDialog = false },
            title = { Text("Clear Cart") },
            text = { Text("Are you sure you want to clear your cart?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearCart(customerId)
                        showClearCartDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showClearCartDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CartItemRow(
    item: FavoriteItem,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Quantity: ${item.quantity}")
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${(item.price * item.quantity)}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
                }
                IconButton(onClick = onAdd) {
                    Icon(Icons.Default.Add, contentDescription = "Increase quantity")
                }
            }
        }
    }
}