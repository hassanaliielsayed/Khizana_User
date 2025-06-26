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
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.khizana_user.R
import com.example.khizana_user.presentation.AppLogo
import com.example.khizana_user.presentation.TopBarIconButton
import com.example.khizana_user.presentation.home.view.SharedModifiers
import com.example.khizana_user.presentation.profile.view.StatusBadge
import com.example.khizana_user.utils.customFontFamily
import com.example.khizana_user.utils.toCurrentCurrency


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: Long,
    viewModel: OrderViewModel = hiltViewModel(),
    onNavigateToSetting: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val state by viewModel.orderDetails.collectAsStateWithLifecycle()
    val productImages by viewModel.productImages.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(orderId) {
        viewModel.fetchOrderDetails(orderId)
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
                        icon = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.favorites),
                        onClick = onNavigateToSetting
                    )
                    TopBarIconButton(
                        icon = Icons.Default.ShoppingCart,
                        contentDescription = stringResource(R.string.shopping_cart),
                        onClick = onNavigateToCart
                    )
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
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp,
                            color = colorResource(id = R.color.dark_blue)
                        )
                    }
                }

                is Result.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        val order = result.data


                        Text(
                            text = stringResource(R.string.order_details),
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = colorResource(id = R.color.black)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE3F2FD)
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.order, order.id),
                                        fontFamily = customFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = colorResource(id = R.color.white)
                                    )

                                    Text(
                                        text = order.createdAt.formatAsShortDate(context),
                                        fontFamily = customFontFamily,
                                        fontSize = 16.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = stringResource(R.string.total_amount1),
                                            fontFamily = customFontFamily,
                                            fontSize = 16.sp,
                                            color = Color.DarkGray
                                        )
                                        Text(
                                            text = order.totalPrice.toCurrentCurrency(),
                                            fontFamily = customFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = colorResource(id = R.color.black)
                                        )
                                    }

                                    StatusBadge(status = order.financialStatus)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = stringResource(R.string.order_items),
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = customFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = colorResource(id = R.color.black)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            order.items.forEach { item ->
                                LaunchedEffect(item.productId) {
                                    viewModel.fetchProductImage(item.productId)
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = colorResource(R.color.dark_blue)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val imageUrl = productImages[item.productId]

                                        if (!imageUrl.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = item.title,
                                                modifier = SharedModifiers.circleImageModifier(100.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color.LightGray),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Image,
                                                    contentDescription = null,
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontSize = 20.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                fontFamily = customFontFamily,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            OrderDetailRow(
                                                label = stringResource(R.string.brand),
                                                value = item.vendor ?: "",
                                                fontSize = 16.sp
                                            )

                                            OrderDetailRow(
                                                label = stringResource(R.string.price1),
                                                value = "${item.price} ${order.currency}",
                                                fontSize = 16.sp
                                            )

                                            OrderDetailRow(
                                                label = stringResource(R.string.quantity),
                                                value = item.quantity.toString(),
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                is Result.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = stringResource(R.string.error),
                                tint = Color.Red,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.error, result.message),
                                color = Color.Red,
                                fontFamily = customFontFamily,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderDetailRow(label: String, value: String, fontSize: TextUnit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = customFontFamily,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontFamily = customFontFamily,
            fontSize = fontSize,
            color = Color.Black
        )
    }
}