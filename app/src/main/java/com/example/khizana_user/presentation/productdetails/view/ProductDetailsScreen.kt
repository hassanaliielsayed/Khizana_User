package com.example.khizana_user.presentation.productdetails.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.khizana_user.domain.model.ProductDetails
import com.example.khizana_user.presentation.productdetails.viewmodel.ProductDetailsViewModel
import com.example.khizana_user.utils.toCurrentCurrency
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
fun ProductDetailsScreen(productId: Long, viewModel: ProductDetailsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    when (val result = state) {
        is ProductDetailsViewModel.Result.Loading -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        is ProductDetailsViewModel.Result.Error -> Text("Error: ${result.message}", color = Color.Red)
        is ProductDetailsViewModel.Result.Success -> ProductDetailsContent(result.data)
    }
}

@Composable
fun ProductDetailsContent(product: ProductDetails) {
    var selectedSize by remember { mutableStateOf<String?>(null) }
    var selectedColorIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())) {
                HorizontalPager(
                    count = product.images.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) { page ->
                    AsyncImage(
                        model = product.images[page],
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(product.images.size) { index ->
                        val selected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(if (selected) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (selected) Color.Black else Color.LightGray)
                        )
                    }
                }

                IconButton(
                    onClick = { /* Toggle favorite */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
                ) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(product.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    val filled = i < product.rating.toInt()
                    Icon(Icons.Default.Star, contentDescription = null, tint = if (filled) Color(0xFFFFC107) else Color.LightGray)
                }
                Text("  (${product.rating})", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("$${product.price.toCurrentCurrency()}", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E88E5))

            Spacer(modifier = Modifier.height(12.dp))

            Text("Color", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(Color.Blue, Color.Magenta, Color(0xFFFF9800)).forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(2.dp, if (selectedColorIndex == index) Color.Black else Color.Transparent, CircleShape)
                            .clickable { selectedColorIndex = index }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Available Sizes", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(product.sizes) { size ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                2.dp,
                                if (size == selectedSize) Color(0xFF1E88E5) else Color.LightGray,
                                RoundedCornerShape(12.dp)
                            )
                            .background(if (size == selectedSize) Color(0xFF1E88E5) else Color.White)
                            .clickable { selectedSize = size }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(size, color = if (size == selectedSize) Color.White else Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(product.description, fontSize = 14.sp, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(100.dp))
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { /* Add to cart */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Add to Cart")
                }
                Button(
                    onClick = { /* Buy now */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Buy Now")
                }
            }
        }
    }
}
