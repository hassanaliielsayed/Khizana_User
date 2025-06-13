package com.example.khizana_user.presentation.order.view

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
import com.example.khizana_user.utils.Result


@Composable
fun OrdersScreen(viewModel: OrderViewModel = hiltViewModel(), customerId: Long) {
    val state by viewModel.orders.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchOrders(customerId)
    }

    when (val result = state) {
        is Result.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Result.Success -> {
            LazyColumn {
                items(result.data) { order ->
                    OrderItem(order)
                }
            }
        }

        is Result.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${result.message}", color = Color.Red)
            }
        }
    }

}

@Composable
fun OrderItem(order: Orders) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Order ID: ${order.id}")
            Text(text = "Email: ${order.email}")
            Text(text = "Created at: ${order.createdAt}")
            Text(text = "Price: ${order.totalPrice} ${order.currency}")
            Text(text = "Status: ${order.financialStatus}")
        }
    }
}


