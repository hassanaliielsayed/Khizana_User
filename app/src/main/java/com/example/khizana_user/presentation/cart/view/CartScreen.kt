package com.example.khizana_user.presentation.cart.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.khizana_user.presentation.home.view.SharedModifiers
import com.example.khizana_user.presentation.profile.view.EmptyState
import com.example.khizana_user.utils.ConfirmationDialog
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.customFontFamily
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
                            contentDescription = stringResource(R.string.home),
                            onClick = onNavigateToHome
                        )
                        TopBarIconButton(
                            icon = Icons.Default.Favorite,
                            contentDescription = stringResource(R.string.favorites),
                            onClick = onNavigateToFavorite
                        )
                    }
                )
            }
        ) { padding ->
            when (val result = cartResult) {
                Result.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is Result.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            stringResource(R.string.failed_to_load_cart, result.message),
                            color = Color.Red,
                            fontFamily = customFontFamily
                        )
                    }
                }

                is Result.Success -> {
                    val cart = result.data
                    totalPrice = cart.items.sumOf { (it?.price ?: 0.0) * (it?.quantity ?: 0) }

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
                                message = stringResource(R.string.your_cart_is_empty_add_items_to_see_them_here)
                            )
                        }
                    } else {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(stringResource(R.string.your_cart), style = MaterialTheme.typography.titleLarge,  fontFamily = customFontFamily)
                                Button(
                                    onClick = { showClearCartDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.content_color))
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.clear_cart)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(R.string.clear_cart), fontFamily = customFontFamily)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 200.dp, max = 500.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(cart.items) { item ->
                                    item?.let {
                                        CartItemColumn(
                                            item = it,
                                            onAdd = {
                                                viewModel.addToCart(
                                                    customerId,
                                                    item.variantId
                                                )
                                            },
                                            onRemove = {
                                                viewModel.decrementFromCart(
                                                    customerId,
                                                    item.variantId
                                                )
                                            },
                                            onDelete = { itemToDelete = it }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Divider(
                                color = colorResource(R.color.dark_blue),
                                thickness = 2.dp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    stringResource(R.string.total),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = customFontFamily
                                )
                                Text(
                                    text = totalPrice.toCurrentCurrency(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(R.color.content_color),
                                    fontFamily = customFontFamily
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { onCheckoutClick(totalPrice) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(
                                        id = R.color.dark_blue
                                    )
                                )
                            ) {
                                Text(
                                    stringResource(R.string.proceed_to_checkout),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontFamily = customFontFamily
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    ConfirmationDialog(
        showDialog = showClearCartDialog,
        onDismiss = { showClearCartDialog = false },
        onConfirm = { viewModel.clearCart(customerId) },
        title = stringResource(R.string.clear_cart),
        text = stringResource(R.string.are_you_sure_you_want_to_clear_your_cart),
        confirmText = stringResource(R.string.color)
    )

    ConfirmationDialog(
        showDialog = itemToDelete != null,
        onDismiss = { itemToDelete = null },
        onConfirm = {
            itemToDelete?.let { item ->
                viewModel.removeFromCart(customerId, item.variantId)
            }
        },
        title = stringResource(R.string.remove_item),
        text = stringResource(R.string.are_you_sure_you_want_to_remove_this_item_from_your_cart),
        confirmText = stringResource(R.string.remove_item)
    )
}
@Composable
fun CartItemColumn(
    item: FavoriteItem,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(2.dp, colorResource(R.color.dark_blue)),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.white)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = SharedModifiers.circleImageModifier(100.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        modifier = Modifier.weight(1f),
                        fontFamily = customFontFamily
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.remove_item),
                            tint = colorResource(R.color.content_color)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(60.dp)
                        .background(colorResource(R.color.dark_blue))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.total_amount, (item.price * item.quantity).toCurrentCurrency()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = customFontFamily
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.quantity),
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = customFontFamily
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = stringResource(R.string.decrease_quantity),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = item.quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontFamily = customFontFamily,
                            fontSize = 20.sp
                        )

                        IconButton(
                            onClick = onAdd,
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.increase_quantity),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}