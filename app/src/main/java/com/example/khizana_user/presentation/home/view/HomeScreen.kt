package com.example.khizana_user.presentation.home.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.khizana_user.presentation.home.viewModel.HomeViewModel
import com.example.khizana_user.presentation.nav.ScreenRoute
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.customFontFamily
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController
)
 {

    val brands by viewModel.brands.collectAsState()
    val error by viewModel.error.collectAsState()
    val couponState by viewModel.coupons.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsState()

    var selectedVendor by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(brands) {
        if (brands.isNotEmpty()) {
            selectedVendor = brands.first().title
        }
    }

    LaunchedEffect(selectedVendor){
        viewModel.fetchProductsByVendor(selectedVendor.toString())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.project_name),
                        fontFamily = customFontFamily,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.dark_blue)
                ),
                actions = {
                    IconButton(onClick = {}) {
                        Image(
                            painter = painterResource(id = R.drawable.filter),
                            contentDescription = stringResource(R.string.filter),
                            modifier = Modifier.size(24.dp)
                        )

                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            tint = Color.White,
                            contentDescription = stringResource(R.string.favorites),
                            modifier = Modifier.size(24.dp)
                        )

                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            tint = Color.White,
                            contentDescription = stringResource(R.string.shopping_cart),
                            modifier = Modifier.size(24.dp)
                        )

                    }
                }
            )
        }

    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(363.dp)
                ) {

                    Image(

                        painter = painterResource(id = R.drawable.home_background),
                        contentDescription = stringResource(R.string.background_image),
                        modifier = Modifier.fillMaxSize()

                    )

                    Column(

                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center

                    ) {
                        Image(

                            painter = painterResource(id = R.drawable.person),
                            contentDescription = stringResource(R.string.user_image),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(55.dp)
                                .clip(CircleShape)

                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.welcome_message),
                            fontSize = 22.sp,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Normal,
                            //fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val searchText = remember { mutableStateOf("") }

                        OutlinedTextField(

                            value = searchText.value,
                            onValueChange = { searchText.value = it },
                            placeholder = { Text(stringResource(R.string.search_for_products)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.Gray

                            ),

                        )
                    }
                }
            }

            item {

                Text(

                    text = stringResource(R.string.brands),
                    fontSize = 20.sp,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(id = R.color.dark_blue),
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)

                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(

                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {

                    items(brands) { brand ->

                        Brands(
                            brands = brand,
                            onClick = {
                                selectedVendor = brand.title
                            },
                            isSelected = selectedVendor == brand.title
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                when (couponState) {
                    is Result.Error -> {
                        Text("Error loading coupons", color = MaterialTheme.colorScheme.error)
                    }

                    is Result.Loading -> {
                        CircularProgressIndicator()
                    }

                    is Result.Success -> {
                        val coupons = (couponState as Result.Success<List<Coupon>>).data
                        CouponCarousel(copuons = coupons)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Text(

                        text = stringResource(R.string.products),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = customFontFamily,
                        color = colorResource(id = R.color.dark_blue)

                    )
                }
            }

            item {

                Spacer(modifier = Modifier.height(8.dp))

                if (products.isEmpty()) {

                    Text(

                        text = "there is no Product For this vendor",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray

                    )

                } else {

                    LazyRow(

                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)

                    ) {

                        items(products) { product ->

                            ProductItem(product = product) {
                                navController.navigate(ScreenRoute.ProductDetails.createRoute(product.id))
                            }

                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
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
            .size(80.dp)
            .clickable { onClick() },
        border = if (isSelected) BorderStroke(2.dp, colorResource(id = R.color.dark_blue)) else BorderStroke(2.dp, colorResource(id = R.color.black))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.light_blue))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            GlideImage(
                model = brands.imageUrl ?: "",
                contentDescription = "Brand Logo",
                modifier = Modifier.size(35.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = brands.title,
                fontSize = 14.sp,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.size(150.dp)
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
                        .size(70.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.productTitle.substringAfter("|").trim(),
                fontSize = 14.sp,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
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
                    modifier = Modifier.fillMaxSize()
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


