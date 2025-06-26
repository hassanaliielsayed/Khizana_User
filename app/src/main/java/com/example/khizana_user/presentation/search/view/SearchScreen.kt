package com.example.khizana_user.presentation.search.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.khizana_user.presentation.category.view.ProductItem
import com.example.khizana_user.presentation.category.viewModel.CategoryViewModel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.khizana_user.R
import com.example.khizana_user.presentation.AppLogo
import com.example.khizana_user.presentation.TopBarIconButton
import com.example.khizana_user.presentation.favorites.viewmodel.WishlistViewModel
import com.example.khizana_user.presentation.nav.ScreenRoute
import com.example.khizana_user.presentation.productdetails.view.GuestLoginDialog
import com.example.khizana_user.presentation.profile.view.EmptyState
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.isGuestUser
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateToFavorites: () -> Unit,
    onNavigateToCart: () -> Unit,
    navController: NavHostController,
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
    customerId: Long
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val favoriteStates = remember { mutableStateMapOf<Long, Boolean>() }
    val togglingStates = remember { mutableStateMapOf<Long, Boolean>() }
    var showGuestDialog by remember { mutableStateOf(false) }
    var guestAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val favoritesState by wishlistViewModel.favoritesState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(customerId) {
        wishlistViewModel.loadFavorites(customerId)

    }

    LaunchedEffect(Unit) {
        viewModel.getAllProducts()
    }

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
                        icon = Icons.Default.Favorite,
                        contentDescription = stringResource(R.string.favorites),
                        onClick = onNavigateToFavorites
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {

            SearchBar(
                query = searchQuery,
                onQueryChange = { query ->
                    searchQuery = query
                    viewModel.filterProductsBySearch(query)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))


            if (products.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EmptyState(
                        animationRes = R.raw.no_data,
                        message = stringResource(R.string.no_product_found)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products) { product ->
                        val isFavorite = favoritesState?.items?.any { it?.variantId == product.variantId } == true
                        val isToggling = togglingStates[product.id] ?: false
                        ProductItem(
                            product = product,
                            isFavorite = isFavorite,
                            isToggling = isToggling,
                            onClick = {
                                navController.navigate(ScreenRoute.ProductDetails.createRoute(product.id))
                            },
                            onToggleFavorite = {
                                val variantId = product.variantId ?: return@ProductItem

                                if (isGuestUser()) {
                                    guestAction = {}
                                    showGuestDialog = true
                                    return@ProductItem
                                }

                                if (togglingStates[product.id] == true) return@ProductItem

                                togglingStates[product.id] = true

                                coroutineScope.launch {
                                    val wasFavorite = favoriteStates[product.id] ?: false
                                    val result = if (wasFavorite)
                                        wishlistViewModel.removeFromFavorites(customerId, variantId)
                                    else
                                        wishlistViewModel.addToFavorites(customerId, variantId)

                                    when (result) {
                                        is Result.Success<*> -> favoriteStates[product.id] = !wasFavorite
                                        is Result.Error -> Toast.makeText(
                                            context,
                                            result.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        else -> {}
                                    }

                                    togglingStates[product.id] = false
                                }
                            }

                        )
                    }
                }
                if (showGuestDialog) {
                    GuestLoginDialog(
                        onDismiss = { showGuestDialog = false },
                        onLoginClick = {
                            showGuestDialog = false
                            navController.navigate(context.getString(R.string.login))
                        },
                        onContinueClick = {
                            showGuestDialog = false
                            guestAction?.invoke()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = {
            Text(
                text = stringResource(R.string.search_for_products),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        shape = MaterialTheme.shapes.large,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}
