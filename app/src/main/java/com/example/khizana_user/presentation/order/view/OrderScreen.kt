package com.example.khizana_user.presentation.order.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.khizana_user.presentation.order.viewmodel.OrderViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.khizana_user.domain.model.Orders
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.navigation.NavHostController
import com.example.khizana_user.presentation.nav.ScreenRoute
import com.example.khizana_user.utils.Result
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.khizana_user.R
import com.example.khizana_user.utils.customFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrderViewModel = hiltViewModel(),
    customerId: Long,
    navController: NavHostController
) {
    val state by viewModel.orders.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchOrders(customerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.project_name),
                        fontFamily = customFontFamily,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.dark_blue)),
                actions = {

                    IconButton(onClick = {

                    }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = {

                    }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val result = state) {
                is Result.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is Result.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            "Your Orders",
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(result.data) { order ->
                                OrderItem(order, navController)
                            }
                        }
                    }
                }

                is Result.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Error: ${result.message}", color = Color.Red,  fontFamily = customFontFamily,)
                    }
                }
            }
        }
    }
}


@Composable
fun OrderItem(order: Orders, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable {
                navController.navigate("${ScreenRoute.OrderDetails.route}/${order.id}")
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(
                0xFFE3F2FD
            )
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Order ID: ${order.id}",  fontFamily = customFontFamily,  fontWeight = FontWeight.Bold,)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Price: ${order.totalPrice} ${order.currency}",  fontFamily = customFontFamily,)
        }
    }
}



