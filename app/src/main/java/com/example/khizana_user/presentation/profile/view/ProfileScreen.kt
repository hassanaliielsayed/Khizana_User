package com.example.khizana_user.presentation.profile.view

import com.example.khizana_user.utils.Result
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.khizana_user.R
import com.example.khizana_user.domain.model.Orders
import com.example.khizana_user.presentation.auth.viewmodel.AuthViewModel
import com.example.khizana_user.presentation.favorites.viewmodel.WishlistViewModel
import com.example.khizana_user.presentation.nav.ScreenRoute
import com.example.khizana_user.presentation.order.viewmodel.OrderViewModel
import com.example.khizana_user.utils.customFontFamily
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun ProfileScreen(
    customerId: Long,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
    navController: NavHostController,
    onNavigateToSetting: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val currentCustomer by authViewModel.currentCustomer.collectAsState()
    val orderState by orderViewModel.orders.collectAsState()
    val favoritesState by wishlistViewModel.favoritesState.collectAsState()

    LaunchedEffect(Unit) {
        orderViewModel.fetchOrders(customerId)
        wishlistViewModel.loadFavorites(customerId)
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
                    IconButton(onClick = onNavigateToSetting) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = onNavigateToCart) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = stringResource(R.string.shopping_cart),
                            tint = Color.Black
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        if (Firebase.auth.currentUser != null && !Firebase.auth.currentUser!!.email.isNullOrBlank()) {
            if (currentCustomer != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(20.dp),
                            Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.person),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(100.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 2.dp, start = 20.dp, end = 20.dp),
                            Arrangement.Center
                        ) {
                            Text(
                                text = "Name : ${currentCustomer?.name ?: ""}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val configuration = LocalConfiguration.current
                        val screenWidth = configuration.screenWidthDp.dp

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(
                                    top = if (screenWidth < 600.dp) 2.dp else 10.dp,
                                    start = if (screenWidth < 600.dp) 10.dp else 20.dp,
                                    end = if (screenWidth < 600.dp) 10.dp else 20.dp
                                ),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Email : ${currentCustomer?.email ?: "N/A"}",
                                fontSize = if (screenWidth < 600.dp) 14.sp else 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        when (val result = orderState) {
                            is Result.Loading -> {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            }

                            is Result.Success -> {
                                if (result.data.isEmpty()) {
                                    Text(
                                        text = "No orders yet.",
                                        fontSize = 16.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                } else {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Orders",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        if (result.data.isNotEmpty()) {
                                            Text(
                                                text = "See more",
                                                color = colorResource(id = R.color.black),
                                                fontSize = 14.sp,
                                                modifier = Modifier.clickable {
                                                    navController.navigate(ScreenRoute.Orders.route)
                                                }
                                            )
                                        }
                                    }

                                    val ordersToShow = result.data.take(2)
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    ) {
                                        ordersToShow.forEach { order ->
                                            Card(
                                                shape = RoundedCornerShape(12.dp),
                                                elevation = CardDefaults.cardElevation(6.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color(0xFFE3F2FD)
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Text(
                                                        "Order ID: ${order.id}",
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        "Total: ${order.totalPrice} EGP",
                                                        color = Color.DarkGray
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            is Result.Error -> {
                                Text(
                                    text = "Failed to load orders.",
                                    color = Color.Red,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        favoritesState?.let { favoriteList ->
                            if (favoriteList.items.isNullOrEmpty()) {
                                Text(
                                    text = "No favorites yet.",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Favorites",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    if (favoriteList.items.isNotEmpty()) {
                                        Text(
                                            text = "See more",
                                            color = colorResource(id = R.color.black),
                                            fontSize = 14.sp,
                                            modifier = Modifier.clickable {
                                                navController.navigate(ScreenRoute.Favorites.route)
                                            }
                                        )
                                    }
                                }

                                val favoritesToShow = favoriteList.items.filterNotNull().take(4)

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    favoritesToShow.forEach { fav ->
                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            elevation = CardDefaults.cardElevation(6.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFFFFF3E0)
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row (modifier = Modifier.padding(8.dp)) {
                                                GlideImage(
                                                    model = fav.imageUrl,
                                                    contentDescription = fav.title,
                                                    modifier = Modifier
                                                        .size(80.dp)
                                                        .padding(8.dp)
                                                        .clip(RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                                Text(
                                                    "Product: ${fav.title}",
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LottieAnimation(
                        composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.login_animation)).value,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .size(270.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Please sign in or register to access your profile.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(
                        onClick = {
                            navController.navigate(ScreenRoute.Login.route)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue))
                    ) {
                        Text(text = "Sign In / Register", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                }
            }
        }
    }
}
