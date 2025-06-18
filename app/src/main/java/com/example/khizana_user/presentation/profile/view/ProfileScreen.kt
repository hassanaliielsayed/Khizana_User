package com.example.khizana_user.presentation.profile.view

import com.example.khizana_user.utils.Result
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.khizana_user.R
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.domain.model.Orders
import com.example.khizana_user.presentation.AppLogo
import com.example.khizana_user.presentation.TopBarIconButton
import com.example.khizana_user.presentation.auth.viewmodel.AuthViewModel
import com.example.khizana_user.presentation.favorites.viewmodel.WishlistViewModel
import com.example.khizana_user.presentation.home.view.NoInternetConnectionView
import com.example.khizana_user.presentation.home.view.SharedModifiers
import com.example.khizana_user.presentation.nav.ScreenRoute
import com.example.khizana_user.presentation.order.view.formatAsShortDate
import com.example.khizana_user.presentation.order.viewmodel.OrderViewModel
import com.example.khizana_user.utils.customFontFamily
import com.example.khizana_user.utils.toCurrentCurrency
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
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
    val connectionState by orderViewModel.networkState.collectAsState()

    LaunchedEffect(Unit) {
        if (connectionState) {
            orderViewModel.fetchOrders(customerId)
            wishlistViewModel.loadFavorites(customerId)
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
            },
        ) { innerPadding ->
            if (Firebase.auth.currentUser != null && !Firebase.auth.currentUser!!.email.isNullOrBlank()) {
                if (currentCustomer != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                shape = CircleShape,
                                elevation = CardDefaults.cardElevation(8.dp),
                                modifier = Modifier.size(120.dp)
                            ) {

                                Image(
                                    painter = painterResource(id = R.drawable.user),
                                    contentDescription = stringResource(R.string.user_image),
                                    modifier = SharedModifiers.circleImageModifier(150.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = currentCustomer?.name ?: "",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = customFontFamily,
                                color = colorResource(id = R.color.dark_blue)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = currentCustomer?.email ?: "N/A",
                                fontSize = 18.sp,
                                color = Color.Gray,
                                fontFamily = customFontFamily,
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        SectionHeader(
                            title = stringResource(R.string.recent_orders),
                            seeMoreVisible = ((orderState as? Result.Success)?.data?.size ?: 0) > 2,
                            onSeeMoreClick = { navController.navigate(ScreenRoute.Orders.route) }
                        )

                        when (val result = orderState) {
                            is Result.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
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
                                if (result.data.isEmpty()) {
                                    EmptyState(
                                        animationRes = R.raw.no_data,
                                        message = stringResource(R.string.you_haven_t_placed_any_orders_yet)
                                    )
                                } else {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        result.data.take(2).forEach { order ->
                                            OrderCard(order = order) {
                                                navController.navigate("${ScreenRoute.OrderDetails.route}/${order.id}")
                                            }
                                        }
                                    }
                                }
                            }

                            is Result.Error -> {
                                ErrorState(message = stringResource(
                                    R.string.failed_to_load_orders,
                                    result.message
                                ))
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        SectionHeader(
                            title = stringResource(R.string.your_favorites),
                            seeMoreVisible = (favoritesState?.items?.size ?: 0) > 4,
                            onSeeMoreClick = { navController.navigate(ScreenRoute.Favorites.route) }
                        )

                        when {

                            favoritesState?.items.isNullOrEmpty() -> {
                                EmptyState(
                                    animationRes = R.raw.no_data,
                                    message = stringResource(R.string.you_don_t_have_any_favorites_yet)
                                )
                            }

                            else -> {
                                val favoritesToShow =
                                    favoritesState?.items?.filterNotNull()?.take(4) ?: emptyList()
                                LazyRow(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    items(favoritesToShow) { fav ->
                                        FavoriteCard(fav = fav) {
                                            navController.navigate(
                                                ScreenRoute.ProductDetails.createRoute(
                                                    variantId = fav.variantId
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                } else {
                    LoadingState()
                }
            } else {
                UnauthenticatedState(
                    onLoginClick = { navController.navigate(ScreenRoute.Login.route) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    seeMoreVisible: Boolean,
    onSeeMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = customFontFamily,
            color = colorResource(id = R.color.black)
        )

        if (seeMoreVisible) {
            Text(
                text = stringResource(R.string.see_all),
                color = colorResource(id = R.color.black),
                fontSize = 12.sp,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable { onSeeMoreClick() }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun OrderCard(order: Orders, onClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.dark_blue)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.order, order.id),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = customFontFamily,
                )

                Text(
                    text = order.createdAt.formatAsShortDate(context),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = customFontFamily,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.total_amount1),
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        fontFamily = customFontFamily,
                    )
                    Text(
                        text = order.totalPrice.toCurrentCurrency(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                    )
                }

                StatusBadge(status = order.financialStatus)
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        stringResource(R.string.paid) -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        stringResource(R.string.pending) -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
        else -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = customFontFamily,
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun FavoriteCard(fav: FavoriteItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.fav_card)
        ),
        modifier = Modifier
            .width(280.dp)
            .padding(bottom = 70.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(80.dp)
            ) {
                GlideImage(
                    model = fav.imageUrl,
                    contentDescription = fav.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = fav.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = customFontFamily,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = fav.price.toCurrentCurrency(),
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.content_color),
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun EmptyState(animationRes: Int, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes)).value,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            fontSize = 18.sp,
            color = Color.Gray,
            fontFamily = customFontFamily,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun ErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = stringResource(R.string.error),
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            color = Color.Red,
            fontFamily = customFontFamily,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingState() {
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

@Composable
private fun UnauthenticatedState(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.login_animation)).value,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(280.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.please_sign_in_to_access_your_profile),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontFamily = customFontFamily,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.dark_blue),
                contentColor = Color.White
            ),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .height(48.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.sign_in_register),
                fontSize = 18.sp,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}