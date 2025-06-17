package com.example.khizana_user.presentation.cart.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.khizana_user.R
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.presentation.AppLogo
import com.example.khizana_user.presentation.TopBarIconButton
import com.example.khizana_user.presentation.cart.viewmodel.CartViewModel
import com.example.khizana_user.presentation.home.view.NoInternetConnectionView
import com.example.khizana_user.presentation.profile.view.EmptyState
import com.example.khizana_user.utils.ConfirmationDialog
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.toCurrentCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    customerId: Long,
    modifier: Modifier = Modifier,
    onCheckoutClick: (Double) -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToFavorite: () -> Unit
) {
    val cartResult by viewModel.cartState.collectAsStateWithLifecycle()
    var showClearCartDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<FavoriteItem?>(null) }
    var totalPrice by remember { mutableStateOf(0.0) }
    val connectionState by viewModel.networkState.collectAsState()

    LaunchedEffect(customerId) {
        if (connectionState) {
            viewModel.loadCart(customerId)
        }
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
                            icon = Icons.Default.Favorite,
                            contentDescription = stringResource(R.string.shopping_cart),
                            onClick = onNavigateToFavorite
                        )
                    }
                )
            }
        ) { paddingValues ->
            when (val result = cartResult) {
                Result.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is Result.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Failed to load cart: ${result.message}", color = Color.Red)
                    }
                }

                is Result.Success -> {
                    val cart = result.data
                    totalPrice = cart.items.sumOf { (it?.price ?: 0.0) * (it?.quantity ?: 0) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        if (cart.items.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                EmptyState(
                                    animationRes = R.raw.empity_cart,
                                    message = "Your Cart is empty\nAdd items to see them here"
                                )
                            }
                        } else {
                            CartItemsList(
                                items = cart.items,
                                onAdd = { item -> viewModel.addToCart(customerId, item.variantId) },
                                onRemove = { item ->
                                    viewModel.decrementFromCart(
                                        customerId,
                                        item.variantId
                                    )
                                },
                                onDelete = { item -> itemToDelete = item },
                                modifier = Modifier.weight(1f)
                            )

                            CartSummary(
                                totalPrice = totalPrice,
                                onCheckoutClick = { onCheckoutClick(totalPrice) },
                                modifier = Modifier.padding(16.dp)
                            )
                        }
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
private fun CartItemsList(
    items: List<FavoriteItem?>,
    onAdd: (FavoriteItem) -> Unit,
    onRemove: (FavoriteItem) -> Unit,
    onDelete: (FavoriteItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            item?.let {
                CartItemCard(
                    item = it,
                    onAdd = { onAdd(it) },
                    onRemove = { onRemove(it) },
                    onDelete = { onDelete(it) }
                )
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: FavoriteItem,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            // Product Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Title and Delete Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete item",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Price
                Text(
                    text = item.price.toCurrentCurrency(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Quantity Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total: ${(item.price * item.quantity).toCurrentCurrency()}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    QuantitySelector(
                        quantity = item.quantity,
                        onIncrease = onAdd,
                        onDecrease = onRemove,
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onDecrease,
            modifier = Modifier.size(32.dp),
            enabled = quantity > 1
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease quantity"
            )
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = onIncrease,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase quantity"
            )
        }
    }
}

@Composable
private fun CartSummary(
    totalPrice: Double,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            onClick = onCheckoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                "Proceed to Checkout",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}