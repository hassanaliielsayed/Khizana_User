package com.example.khizana_user.presentation.home.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.khizana_user.R
import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.model.Product
import com.example.khizana_user.presentation.auth.viewmodel.AuthViewModel
import com.example.khizana_user.presentation.home.viewModel.HomeViewModel
import com.example.khizana_user.presentation.nav.ScreenRoute
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.customFontFamily
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    navController: NavHostController,
    onNavigateToFavorites: () -> Unit,
    onNavigateToCart: () -> Unit
)
 {
    val brands by viewModel.brands.collectAsState()
    val couponState by viewModel.coupons.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState(initial = "")
    val suggestions by viewModel.suggestions.collectAsState()
    val currentCustomer by authViewModel.currentCustomer.collectAsState()
    val focusManager = LocalFocusManager.current

    var expanded by remember { mutableStateOf(false) }
    var selectedVendor by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(brands) {
        if (brands.isNotEmpty()) selectedVendor = brands.first().title
    }

    LaunchedEffect(selectedVendor) {
        selectedVendor?.let { viewModel.fetchProductsByVendor(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToProduct.collect { id ->
            navController.navigate(ScreenRoute.ProductDetails.createRoute(id))
        }
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
                        Image(
                            painter = painterResource(R.drawable.filter2),
                            contentDescription = "Filter",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = onNavigateToCart) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.Black
                        )
                    }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.dark_blue))
                    .padding(16.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.person),
                        contentDescription = "User Icon",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color.Gray, CircleShape)
                            .padding(4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Welcome ${currentCustomer?.name ?: ""}",
                        fontSize = 22.sp,
                        fontFamily = customFontFamily,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            viewModel.updateSearchQuery(it)
                            expanded = true
                        },
                        placeholder = { Text("Search for products or brands") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp)),
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = {
                                    viewModel.updateSearchQuery("")
                                    expanded = false
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )

                    if (expanded && suggestions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .shadow(6.dp, RoundedCornerShape(8.dp))
                                .background(Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                                    .padding(vertical = 8.dp)
                            ) {
                                suggestions.forEach { suggestion ->
                                    Text(
                                        text = suggestion,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (suggestion.startsWith("Brand: ")) {
                                                    val brandName = suggestion.removePrefix("Brand: ").trim()
                                                    viewModel.updateSearchQuery(brandName)
                                                    viewModel.fetchProductsByVendor(brandName)
                                                } else {
                                                    viewModel.updateSearchQuery(suggestion)
                                                }
                                                expanded = false
                                                focusManager.clearFocus()
                                            }
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Brands",
                fontSize = 25.sp,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(id = R.color.black),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(brands) { brand ->
                    Brands(
                        brands = brand,
                        onClick = { selectedVendor = brand.title },
                        isSelected = selectedVendor == brand.title
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (couponState) {
                is Result.Error -> Text("Error loading coupons", color = MaterialTheme.colorScheme.error)
                is Result.Loading -> CircularProgressIndicator()
                is Result.Success -> {
                    val coupons = (couponState as Result.Success<List<Coupon>>).data
                    CouponCarousel(copuons = coupons)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Products",
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = customFontFamily,
                color = colorResource(id = R.color.black),
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            if (filteredProducts.isEmpty()) {
                Text(
                    text = "No matching products found.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            } else {
                val productRows = groupProductsInRows(filteredProducts)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    productRows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { product ->
                                ProductItem(
                                    modifier = Modifier.weight(1f),
                                    product = product,
                                    onClick = {
                                        navController.navigate(ScreenRoute.ProductDetails.createRoute(product.id))
                                    }
                                )
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
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
        border = if (isSelected) BorderStroke(2.dp, colorResource(id = R.color.dark_blue)) else BorderStroke(2.dp, colorResource(id = R.color.black))
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
                contentDescription = "Brand Logo",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

//            Text(
//                text = brands.title,
//                fontSize = 18.sp,
//                fontFamily = customFontFamily,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )

        }
    }
}

fun <T> groupProductsInRows(products: List<T>): List<List<T>> {
    return products.chunked(2)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .height(200.dp)
            .clickable { onClick() },
        border = BorderStroke(1.dp, colorResource(id = R.color.black))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.white)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlideImage(
                model = product.productImage,
                contentDescription = product.productTitle,
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.productTitle.substringAfter("|").trim(),
                fontSize = 14.sp,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
            .height(200.dp)
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
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            BorderStroke(2.dp, Color.White),
                            RoundedCornerShape(16.dp)
                        )

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
            title = { Text("🎉 Congrats!") },
            text = { Text("Use coupon code: ${selectedCoupon?.title}") },
            confirmButton = {
                Row {
                    TextButton(onClick = {
                        val clip = ClipData.newPlainText(
                            "Coupon Code",
                            selectedCoupon?.title ?: ""
                        )
                        clipboardManager.setPrimaryClip(clip)
                        Toast.makeText(context, "Coupon copied to clipboard", Toast.LENGTH_SHORT)
                            .show()
                        showDialog = false
                    }) {
                        Text("Copy")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { showDialog = false }) {
                        Text("Close")
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