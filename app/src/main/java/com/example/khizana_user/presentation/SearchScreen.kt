package com.example.khizana_user.presentation

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
import com.example.khizana_user.utils.customFontFamily
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.khizana_user.R
import com.example.khizana_user.presentation.nav.ScreenRoute


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateToFavorites: () -> Unit,
    onNavigateToCart: () -> Unit,
    navController: NavHostController,
) {

    val products by viewModel.products.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getAllProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.project_name),
                        fontFamily = customFontFamily,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.dark_blue)),
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            tint = Color.Black,
                            contentDescription = stringResource(R.string.shopping_cart)
                        )
                    }

                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = stringResource(R.string.favorites),
                            tint = Color.Black
                        )
                    }
                }
            )
        }

    ) { padding ->

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    viewModel.filterProductsBySearch(query)
                },

                label = { Text("Search For Products") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(products) { product ->
                    ProductItem(product = product,
                        onClick = {
                            navController.navigate(ScreenRoute.ProductDetails.createRoute(product.id))
                        }
                    )
                }
            }

        }

    }
}

