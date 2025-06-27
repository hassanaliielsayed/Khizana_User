package com.example.khizana_user.presentation.home.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.khizana_user.R
import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.model.Product
import com.example.khizana_user.presentation.AppLogo
import com.example.khizana_user.presentation.TopBarIconButton
import com.example.khizana_user.presentation.auth.viewmodel.AuthViewModel
import com.example.khizana_user.presentation.favorites.viewmodel.WishlistViewModel
import com.example.khizana_user.presentation.home.viewModel.HomeViewModel
import com.example.khizana_user.presentation.home.viewModel.SearchFocusType
import com.example.khizana_user.presentation.nav.ScreenRoute
import com.example.khizana_user.presentation.productdetails.view.GuestLoginDialog
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.customFontFamily
import com.example.khizana_user.utils.isGuestUser
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    navController: NavHostController,
    onNavigateToFavorites: () -> Unit,
    onNavigateToCart: () -> Unit,
    customerId: Long
) {
    val brands by viewModel.brands.collectAsStateWithLifecycle()
    val couponState by viewModel.coupons.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsState(initial = "")
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val searchFocusType by viewModel.searchFocusType.collectAsStateWithLifecycle()
    val currentCustomer by authViewModel.currentCustomer.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    val brandFocusRequester = remember { BringIntoViewRequester() }
    val productFocusRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    val favoriteStates = remember { mutableStateMapOf<Long, Boolean>() }
    val togglingStates = remember { mutableStateMapOf<Long, Boolean>() }
    var showGuestDialog by remember { mutableStateOf(false) }
    var guestAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val favoritesState by wishlistViewModel.favoritesState.collectAsStateWithLifecycle()

    var expanded by remember { mutableStateOf(false) }
    var selectedVendor by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    LaunchedEffect(customerId) {
        wishlistViewModel.loadFavorites(customerId)

    }

    LaunchedEffect(selectedVendor) {
        selectedVendor?.let { viewModel.fetchProductsByVendor(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToProduct.collect { id ->
            navController.navigate(ScreenRoute.ProductDetails.createRoute(id))
        }
    }

    LaunchedEffect(searchFocusType) {
        searchFocusType?.let { focus ->
            coroutineScope.launch {
                when (focus) {
                    SearchFocusType.BRAND -> brandFocusRequester.bringIntoView()
                    SearchFocusType.PRODUCT -> productFocusRequester.bringIntoView()
                }
                viewModel.setFocus(null)
            }
        }
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {

            HomeScreenTopSection(
                currentCustomer = currentCustomer,
                searchQuery = searchQuery,
                onSearchQueryChange = { query ->
                    viewModel.updateSearchQuery(query)
                    expanded = true
                },
                expanded = expanded,
                suggestions = suggestions,
                onSuggestionClick = { suggestion ->
                    if (suggestion.startsWith(context.getString(R.string.brand))) {
                        val brandName =
                            suggestion.removePrefix(context.getString(R.string.brand)).trim()
                        viewModel.updateSearchQuery(brandName)
                        viewModel.fetchProductsByVendor(brandName)
                        viewModel.setFocus(SearchFocusType.BRAND)
                    } else {
                        viewModel.updateSearchQuery(suggestion)
                        viewModel.setFocus(SearchFocusType.PRODUCT)
                    }
                    expanded = false
                    focusManager.clearFocus()
                },
                onClearClick = {
                    viewModel.updateSearchQuery("")
                    expanded = false
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (couponState) {
                is Result.Error -> Text(
                    stringResource(R.string.error_loading_coupons),
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = customFontFamily,
                )

                is Result.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is Result.Success -> CouponCarousel((couponState as Result.Success<List<Coupon>>).data)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.brands),
                fontSize = 25.sp,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(id = R.color.black),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
                    .bringIntoViewRequester(brandFocusRequester)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(brands) { brand ->
                    Brands(
                        brands = brand,
                        onClick = {
                            selectedVendor = brand.title
                            viewModel.fetchProductsByVendor(brand.title)
                        },
                        isSelected = selectedVendor == brand.title
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredProducts.isEmpty() && selectedVendor != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_data)).value,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(200.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filteredProducts.forEach { product ->

                        val isFavorite =
                            favoritesState?.items?.any { it?.variantId == product.variantId } == true
                        val isToggling = togglingStates[product.id] ?: false

                        ProductItem(
                            product = product,
                            isFavorite = isFavorite,
                            isToggling = isToggling,
                            onClick = {
                                navController.navigate(
                                    ScreenRoute.ProductDetails.createRoute(
                                        product.id
                                    )
                                )
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
                                        is Result.Success<*> -> favoriteStates[product.id] =
                                            !wasFavorite

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


@Composable
fun NoInternetConnectionView() {
    // 1. Get the composition result
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.no_internet)
    )

    // 2. Animate the composition
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3. Display the animation
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.size(200.dp)
        )

        Text(
            stringResource(R.string.no_internet_connection),
            style = MaterialTheme.typography.titleMedium,
            fontFamily = customFontFamily,
        )
        Text(
            stringResource(R.string.please_check_your_connection),
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = customFontFamily,
        )
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Brands(
    brands: Brand,
    onClick: () -> Unit = {},
    isSelected: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .size(140.dp)
            .clickable { onClick() },
        border = if (isSelected) BorderStroke(
            2.dp,
            colorResource(id = R.color.dark_blue)
        ) else BorderStroke(2.dp, colorResource(id = R.color.black))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.white))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlideImage(
                model = brands.imageUrl ?: "",
                contentDescription = stringResource(R.string.brand_image),
                modifier = SharedModifiers.roundedImageModifier(80.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    isToggling: Boolean,
    onToggleFavorite: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(1.dp, colorResource(R.color.content_color)),
        modifier = modifier
            .then(SharedModifiers.cardModifier(100.dp))
            .clickable { onClick() }

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(R.color.white))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlideImage(
                    model = product.productImage,
                    contentDescription = product.productTitle,
                    modifier = SharedModifiers.roundedImageModifier(80.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(colorResource(R.color.dark_blue))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    product.productTitle,
                    fontWeight = FontWeight.Bold,
                    fontFamily = customFontFamily,
                )
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
            ) {
                if (isToggling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) colorResource(R.color.content_color) else Color.Black
                    )
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
@Composable
fun CouponCarousel(copuons: List<Coupon>) {

    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2)
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var selectedCoupon: Coupon? by remember { mutableStateOf(null) }

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000L)
            val nextPage = pagerState.currentPage + 1
            pagerState.scrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
            .padding(horizontal = 16.dp)
    ) {
        HorizontalPager(
            count = Int.MAX_VALUE,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val actualIndex = page % copuons.size
            val coupon = copuons[actualIndex]

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        selectedCoupon = coupon
                        showDialog = true
                    }
            ) {
                Image(
                    painter = painterResource(id = coupon.img),
                    contentDescription = coupon.title,
                    contentScale = ContentScale.FillBounds,
                    modifier = SharedModifiers.couponImageModifier()
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(copuons.size) { dotIndex ->
                        val isSelected = pagerState.currentPage % copuons.size == dotIndex
                        DotIndicator(
                            isSelected = isSelected,
                            onClick = {
                                scope.launch {
                                    pagerState.scrollToPage(
                                        pagerState.currentPage - (pagerState.currentPage % copuons.size) + dotIndex
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog && selectedCoupon != null) {
        val context = LocalContext.current
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.congrats), fontFamily = customFontFamily) },
            text = {
                Text(
                    selectedCoupon?.title?.let {
                        stringResource(R.string.use_coupon_code, it)
                    } ?: "",
                    fontFamily = customFontFamily,
                )
            },
            confirmButton = {
                Row {
                    TextButton(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = colorResource(R.color.content_color)
                        )
                    ) {
                        Text(stringResource(R.string.close), fontFamily = customFontFamily)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = {
                            val clip = ClipData.newPlainText(
                                context.getString(R.string.coupon_code),
                                selectedCoupon?.title ?: ""
                            )
                            clipboardManager.setPrimaryClip(clip)
                            Toast.makeText(
                                context,
                                context.getString(R.string.coupon_copied_to_clipboard),
                                Toast.LENGTH_SHORT,
                            )
                                .show()
                            showDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = colorResource(R.color.content_color)
                        )
                    ) {
                        Text(stringResource(R.string.copy), fontFamily = customFontFamily)
                    }
                }
            }
        )
    }
}

@Composable
fun DotIndicator(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val size = if (isSelected) 12.dp else 8.dp
    val color = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
    )
}