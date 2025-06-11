package com.example.khizana_user.presentation.category.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import com.example.khizana_user.R
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.khizana_user.domain.model.ProductByCategory
import com.example.khizana_user.presentation.category.viewModel.CategoryViewModel
import com.example.khizana_user.utils.customFontFamily


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateToFavorites: () -> Unit,
    onNavigateToCart: () -> Unit
) {

    val mainCategory = listOf("All", "Women", "Men", "Kid")
    var selectedMainCategory by remember { mutableStateOf("All") }

    val subCategories = listOf("All", "ACCESSORIES", "SHOES", "T-SHIRTS")
    var selectedSubCategory by remember { mutableStateOf("All") }

    val products by viewModel.products.collectAsState()

    var isSubCategoryMenuOpen by remember { mutableStateOf(false) }

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
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black)
                    }
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Black)
                    }
                    IconButton(onClick = onNavigateToCart) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            tint = Color.Black,
                            contentDescription = null,
                        )
                    }
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
                            "All" to R.drawable.all,
                            "ACCESSORIES" to R.drawable.accessories,
                            "SHOES" to R.drawable.shoes,
                            "T-SHIRTS" to R.drawable.tshirt
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
                        contentDescription = "Filter",
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
                selectedTabIndex = mainCategory.indexOf(selectedMainCategory),
                edgePadding = 16.dp,
                indicator = {},
                modifier = Modifier.background(colorResource(id = R.color.dark_blue))
            ) {
                mainCategory.forEach { main ->
                    Tab(
                        selected = selectedMainCategory == main,
                        onClick = {
                            selectedMainCategory = main
                            viewModel.filterProductsByTag(main)
                        },
                        text = {
                            Text(
                                text = main,
                                color = if (selectedMainCategory == main) Color.Black else Color.Gray,
                                fontWeight = if (selectedMainCategory == main) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.background(colorResource(id = R.color.white))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (products.isEmpty()) {
                Text("No products found.", modifier = Modifier.padding(16.dp), fontFamily = customFontFamily,)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(products) { product ->
                        ProductItem(product = product)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: ProductByCategory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.dark_blue)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.productImage,
                contentDescription = product.productTitle,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(product.productTitle, fontWeight = FontWeight.Bold, fontFamily = customFontFamily,)
                Text("Vendor: ${product.productVendor ?: "N/A"}", fontFamily = customFontFamily,)
                Text("Price: ${product.productPrice} EG", fontFamily = customFontFamily,)
                Text("Product Type: ${product.product_type}", fontFamily = customFontFamily,)
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
        color = if (isSelected) Color(0xFFFDE6EE) else Color.White,
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
            modifier = Modifier.padding(6.dp).size(20.dp)
        )
    }
}
