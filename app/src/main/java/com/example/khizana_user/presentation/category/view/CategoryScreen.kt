package com.example.khizana_user.presentation.category.view

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.khizana_user.R
import com.example.khizana_user.domain.model.ProductByCategory
import com.example.khizana_user.presentation.category.viewModel.CategoryViewModel
import com.example.khizana_user.presentation.home.view.NoInternetConnectionView
import com.example.khizana_user.utils.customFontFamily
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.example.khizana_user.presentation.AppLogo
import com.example.khizana_user.presentation.TopBarIconButton
import com.example.khizana_user.presentation.favorites.viewmodel.WishlistViewModel
import com.example.khizana_user.presentation.home.view.SharedModifiers
import com.example.khizana_user.presentation.nav.ScreenRoute
import com.example.khizana_user.presentation.profile.view.EmptyState
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.isGuestUser
import com.example.khizana_user.utils.toCurrentCurrency
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateToFavorites: () -> Unit,
    onNavigateToSearch: () -> Unit,
    navController: NavHostController,
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
    customerId: Long
) {

    val mainCategory = listOf(
        "All" to stringResource(R.string.all),
        "Women" to stringResource(R.string.women),
        "Men" to stringResource(R.string.men),
        "Kid" to stringResource(R.string.kids)
    )

    var selectedMainCategory by remember { mutableStateOf("All") }

    val subCategories = listOf("All", "ACCESSORIES", "SHOES", "T-SHIRTS")
    var selectedSubCategory by remember { mutableStateOf("All") }

    val products by viewModel.products.collectAsState()

    var isSubCategoryMenuOpen by remember { mutableStateOf(false) }

    var selectedPrice by remember { mutableFloatStateOf(10000f) }

    var isFilterVisible by remember { mutableStateOf(false) }

    val favoriteStates = remember { mutableStateMapOf<Long, Boolean>() }
    val togglingStates = remember { mutableStateMapOf<Long, Boolean>() }
    var showGuestDialog by remember { mutableStateOf(false) }
    var guestAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val favoritesState by wishlistViewModel.favoritesState.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(customerId) {
        wishlistViewModel.loadFavorites(customerId)

    }

    val minPrice = 0f
    val maxPrice = 2000f


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
                        icon = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search),
                        onClick = onNavigateToSearch
                    )
                    TopBarIconButton(
                        icon = Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.filter),
                        tint = Color.Black,
                        onClick = { isFilterVisible = !isFilterVisible }
                    )
                    TopBarIconButton(
                        icon = Icons.Default.Favorite,
                        contentDescription = stringResource(R.string.favorites),
                        onClick = onNavigateToFavorites
                    )
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(visible = isSubCategoryMenuOpen) {
                    Column(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        val subCategoryImages = mapOf(
                            stringResource(R.string.all) to R.drawable.all,
                            stringResource(R.string.accessories) to R.drawable.accessories,
                            stringResource(R.string.shoes) to R.drawable.shoes,
                            stringResource(R.string.t_shirts) to R.drawable.tshirt
                        )

                        Column(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            subCategories.forEach { subCategory ->
                                val imageRes = subCategoryImages[subCategory] ?: R.drawable.all
                                CategoryChipImage(
                                    imageResId = imageRes,
                                    isSelected = selectedSubCategory == subCategory,
                                    onClick = {
                                        selectedSubCategory = subCategory
                                        viewModel.filterProductsBySubCategory(subCategory)
                                        isSubCategoryMenuOpen = false
                                    },
                                    contentDescription = subCategory
                                )
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { isSubCategoryMenuOpen = !isSubCategoryMenuOpen },
                    containerColor = colorResource(id = R.color.black),
                    modifier = Modifier
                        .padding(bottom = 70.dp)
                        .clip(RoundedCornerShape(42.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.filter),
                        tint = Color.White
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {

            ScrollableTabRow(
                selectedTabIndex = mainCategory.indexOfFirst { it.first == selectedMainCategory },
                edgePadding = 16.dp,
                indicator = {},
                modifier = Modifier.background(colorResource(id = R.color.dark_blue))
            ) {
                mainCategory.forEach { (tagValue, displayValue) ->
                    Tab(
                        selected = selectedMainCategory == tagValue,
                        onClick = {
                            selectedMainCategory = tagValue
                            viewModel.filterProductsByTag(tagValue)
                        },
                        text = {
                            Text(
                                text = displayValue,
                                color = if (selectedMainCategory == tagValue) Color.Black else Color.Gray,
                                fontWeight = if (selectedMainCategory == tagValue) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = customFontFamily
                            )
                        },
                        modifier = Modifier.background(colorResource(id = R.color.white))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isFilterVisible) {
                FilterByPrice(minPrice, maxPrice) { price ->
                    selectedPrice = price
                    viewModel.filterProductsByPrice(price)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (products.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EmptyState(
                        animationRes = R.raw.no_data,
                        message = stringResource(R.string.no_product_found_in_this_category)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    items(products) { product ->
                        val isFavorite = favoritesState?.items?.any { it?.variantId == product.variantId } == true
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
                                        is Result.Success<*> -> favoriteStates[product.id] = !wasFavorite
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
        }
    }
}

@Composable
fun ProductItem(product: ProductByCategory, onClick: () -> Unit,
                isFavorite: Boolean,
                isToggling: Boolean,
                onToggleFavorite: () -> Unit,
                ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(1.dp, colorResource(R.color.content_color)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
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

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AsyncImage(
                    model = product.productImage,
                    contentDescription = product.productTitle,
                    modifier = SharedModifiers.circleImageModifier(150.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = product.productTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.vendor, product.productVendor ?: "N/A"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Price: ${product.productPrice.toCurrentCurrency()}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CategoryChipImage(
    imageResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    contentDescription: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) colorResource(R.color.content_color) else Color.White,
        border = BorderStroke(1.dp, Color.Gray),
        modifier = Modifier
            .padding(4.dp)
            .size(40.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(6.dp)
                .size(20.dp)
        )
    }
}

@Composable
fun FilterByPrice(
    minPrice: Float,
    maxPrice: Float,
    onValueChange: (Float) -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(minPrice) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(
            text = "Price: ${sliderPosition.toInt()} EGP",
            fontFamily = customFontFamily,
            fontSize = 20.sp
        )

        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onValueChange(sliderPosition)
            },
            valueRange = minPrice..maxPrice,
            modifier = Modifier.height(22.dp),
            colors = SliderDefaults.colors(
                thumbColor = colorResource(R.color.dark_blue),
                activeTrackColor = colorResource(R.color.content_color),
                inactiveTrackColor = colorResource(R.color.dark_blue)
            )
        )
    }
}


