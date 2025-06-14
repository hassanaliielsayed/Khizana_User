package com.example.khizana_user.presentation.order.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.khizana_user.utils.Result
import com.example.khizana_user.presentation.order.viewmodel.OrderViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.khizana_user.R
import com.example.khizana_user.utils.customFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: Long,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val state by viewModel.orderDetails.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.fetchOrderDetails(orderId)
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
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is Result.Success -> {
                    val order = result.data
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(
                                        0xFFE3F2FD
                                    )
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Order #${order.id}", style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Date: ${order.createdAt}", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Status: ${order.financialStatus}", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Total: ${order.totalPrice} ${order.currency}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Items", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(order.items) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(
                                        0xFFE3F2FD
                                    )
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!item.imageUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = item.imageUrl,
                                            contentDescription = item.title,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.LightGray),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            item.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            "Price: ${item.price} ${order.currency}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text("Quantity: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }

                is Result.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${result.message}", color = Color.Red)
                    }
                }
            }
        }
    }
}

