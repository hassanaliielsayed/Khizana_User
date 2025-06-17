
package com.example.khizana_user.presentation.order.view

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.khizana_user.R
import com.example.khizana_user.presentation.AppLogo
import com.example.khizana_user.presentation.TopBarIconButton
import com.example.khizana_user.presentation.home.view.NoInternetConnectionView
import com.example.khizana_user.presentation.profile.view.StatusBadge
import com.example.khizana_user.utils.customFontFamily
import com.example.khizana_user.utils.toCurrentCurrency
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrderViewModel = hiltViewModel(),
    customerId: Long,
    navController: NavHostController,
    onNavigateToSetting: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val state by viewModel.orders.collectAsState()
    val connectionState by viewModel.networkState.collectAsState()

    LaunchedEffect(Unit) {
        if (connectionState) {
            viewModel.fetchOrders(customerId)
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
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.your_orders),
                                fontFamily = customFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                modifier = Modifier
                                    .padding(vertical = 24.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = colorResource(id = R.color.black)
                            )

                            if (result.data.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingBag,
                                            contentDescription = stringResource(R.string.no_orders),
                                            tint = Color.LightGray,
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = stringResource(R.string.no_orders_yet),
                                            fontFamily = customFontFamily,
                                            fontSize = 20.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(result.data) { order ->
                                        OrderItem(order, navController)
                                    }
                                }
                            }
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
}

@Composable
fun OrderItem(order: Orders, navController: NavHostController) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("${ScreenRoute.OrderDetails.route}/${order.id}")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
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
                        text = stringResource(R.string.total_amount),
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
}

fun String.formatAsShortDate(context: Context): String {
    return try {
        val inputFormat = SimpleDateFormat(context.getString(R.string.yyyy_mm_dd), Locale.getDefault())
        val outputFormat = SimpleDateFormat(context.getString(R.string.mmm_dd_yyyy), Locale.getDefault())

        inputFormat.parse(this)?.let { date ->
            outputFormat.format(date)
        } ?: this
    } catch (e: Exception) {
        this
    }
}

