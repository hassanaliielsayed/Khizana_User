package com.example.khizana_user.presentation.cart.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.khizana_user.utils.ConfirmationDialog
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.toCurrentCurrency
@Composable
fun CartScreen(
    customerId: Long,
    modifier: Modifier = Modifier,
    onCheckoutClick: (Double) -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val cartResult by viewModel.cartState.collectAsStateWithLifecycle()
    var showClearCartDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<FavoriteItem?>(null) }
    var totalPrice by remember { mutableStateOf(0.0) }

    LaunchedEffect(customerId) {
        viewModel.loadCart(customerId)
    }

    when (val result = cartResult) {
        Result.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Result.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Failed to load cart: ${result.message}", color = Color.Red)
            }
        }

        is Result.Success -> {
            val cart = result.data
            totalPrice = cart.items.sumOf { (it?.price ?: 0.0) * (it?.quantity ?: 0) }

            if (cart.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Your cart is empty.")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Top section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🛒 Your Cart", style = MaterialTheme.typography.titleLarge)
                        Button(
                            onClick = { showClearCartDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear cart")
                            Spacer(Modifier.width(8.dp))
                            Text("Clear Cart")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cart.items) { item ->
                            item?.let {
                                CartItemColumn(
                                    item = it,
                                    onAdd = { viewModel.addToCart(customerId, item.variantId) },
                                    onRemove = {
                                        viewModel.decrementFromCart(customerId, item.variantId)
                                    },
                                    onDelete = { itemToDelete = it }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Fixed Checkout Footer
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

                    Spacer(modifier = Modifier.height(16.dp))

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

    // Dialogs
    ConfirmationDialog(
        showDialog = showClearCartDialog,
        onDismiss = { showClearCartDialog = false },
        onConfirm = { viewModel.clearCart(customerId) },
        title = "Clear Cart",
        text = "Are you sure you want to clear your cart?",
        confirmText = "Clear"
    )

    ConfirmationDialog(
        showDialog = itemToDelete != null,
        onDismiss = { itemToDelete = null },
        onConfirm = {
            itemToDelete?.let { item ->
                viewModel.removeFromCart(customerId, item.variantId)
            }
        },
        title = "Remove Item",
        text = "Are you sure you want to remove this item from your cart?",
        confirmText = "Remove"
    )
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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