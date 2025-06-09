package com.example.khizana_user.presentation.cart.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.presentation.cart.viewmodel.CartViewModel
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.toCurrentCurrency

@Composable
fun CartScreen(
    customerId: Long,
    onCheckoutClick: (Double) -> Unit,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "🛒 Your Cart",
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                onClick = { showClearCartDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Clear cart")
                Spacer(Modifier.width(8.dp))
                Text("Clear Cart")
            }
        }

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
                                    CartItemColumn(
                                        item = it,
                                        onAdd = { viewModel.addToCart(customerId, item.variantId) },
                                        onRemove = {
                                            viewModel.decrementFromCart(
                                                customerId,
                                                item.variantId
                                            )
                                        },
                                        onDelete = { viewModel.removeFromCart(customerId, item.variantId) }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Divider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                thickness = 1.dp
                            )
                            Spacer(Modifier.height(8.dp))

                            val totalPrice = cart.items.sumOf {
                                (it?.price ?: 0.00) * (it?.quantity ?: 0)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = totalPrice.toCurrentCurrency(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = { onCheckoutClick(totalPrice) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Proceed to Checkout", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }

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
fun CartItemColumn(
    item: FavoriteItem,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .padding(8.dp)

    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(size = 8.dp))
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        "Quantity: ${item.quantity}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row {
                        IconButton(onClick = onRemove) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
                        }
                        IconButton(onClick = onAdd) {
                            Icon(Icons.Default.Add, contentDescription = "Increase quantity")
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete item")
                        }
                    }

                }

                Text(
                    text = "Price   ${(item.price * item.quantity).toCurrentCurrency()}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

            }
        }
    }
}