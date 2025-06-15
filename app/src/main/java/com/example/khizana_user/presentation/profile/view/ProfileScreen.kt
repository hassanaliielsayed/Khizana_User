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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
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
import com.example.khizana_user.domain.model.FavoriteItem
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
                                fontWeight = FontWeight.Bold,
                                fontFamily = customFontFamily,
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
                                fontWeight = FontWeight.Bold,
                                fontFamily = customFontFamily,
                            )
                        }

                        when (val result = orderState) {
                            is Result.Loading -> {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            }

                            is Result.Success -> {

                                Spacer(modifier = Modifier.height(20.dp))
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
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = customFontFamily,
                                    )

                                    if ( result.data.size>2) {
                                        Text(
                                            text = "See more",
                                            color = colorResource(id = R.color.black),
                                            fontSize = 14.sp,
                                            fontFamily = customFontFamily,
                                            modifier = Modifier.clickable {
                                                navController.navigate(ScreenRoute.Orders.route)
                                            }
                                        )
                                    }
                                }

                                if (result.data.isEmpty()) {

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        LottieAnimation(
                                            composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_data)).value,
                                            iterations = LottieConstants.IterateForever,
                                            modifier = Modifier
                                                .size(200.dp)
                                                .padding(bottom = 16.dp)
                                        )
                                    }

                                } else {

                                    val ordersToShow = result.data.take(2)
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    ) {
                                        ordersToShow.forEach { order ->
                                            OrderCard(order = order, onClick = {
                                                navController.navigate("${ScreenRoute.OrderDetails.route}/${order.id}")
                                            })
                                        }
                                    }
                                }
                            }

                            is Result.Error -> {
                                Text(
                                    text = "Failed to load orders.",
                                    color = Color.Red,
                                    fontFamily = customFontFamily,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

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
                                fontFamily = customFontFamily,
                                fontWeight = FontWeight.Bold
                            )

                            if ((favoritesState?.items?.size ?: 0) > 4) {
                                Text(
                                    text = "See more",
                                    color = colorResource(id = R.color.black),
                                    fontSize = 14.sp,
                                    fontFamily = customFontFamily,
                                    modifier = Modifier.clickable {
                                        navController.navigate(ScreenRoute.Favorites.route)
                                    }
                                )
                            }
                        }

                        if (favoritesState?.items.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                LottieAnimation(
                                    composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_data)).value,
                                    iterations = LottieConstants.IterateForever,
                                    modifier = Modifier
                                        .size(200.dp)
                                        .padding(bottom = 16.dp)
                                )
                            }
                        } else {
                            val favoritesToShow = favoritesState?.items?.filterNotNull()?.take(4) ?: emptyList()

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                favoritesToShow.forEach { fav ->
                                    FavoriteCard(fav = fav, onClick = {
                                        navController.navigate(
                                            ScreenRoute.ProductDetails.createRoute(
                                                variantId = fav.variantId
                                            )
                                        )
                                    })
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
                        fontFamily = customFontFamily,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(
                        onClick = {
                            navController.navigate(ScreenRoute.Login.route)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue))
                    ) {
                        Text(text = "Sign In / Register", color = Color.Black,  fontFamily = customFontFamily,)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Orders, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "Order ID: ${order.id}",
                fontWeight = FontWeight.Bold,
                fontFamily = customFontFamily,
            )
            Text(
                "Total: ${order.totalPrice} EGP",
                color = Color.DarkGray,
                fontFamily = customFontFamily,
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FavoriteCard(fav: FavoriteItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            GlideImage(
                model = fav.imageUrl,
                contentDescription = fav.title,
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(3.dp)
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "Product: ${fav.title}",
                fontWeight = FontWeight.Bold,
                fontFamily = customFontFamily,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp)
            )
        }
    }
}
