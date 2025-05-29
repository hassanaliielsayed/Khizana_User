package com.example.khizana_user.presentation.home.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.khizana_user.R
import com.example.khizana_user.presentation.home.viewModel.HomeViewModel


@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true , showBackground = true)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {

    val brands by viewModel.brands.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Khizana Store") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4157FF)
                ),
                actions = {
                    IconButton(onClick = {}) {
                        Image(
                            painter = painterResource(id = R.drawable.filter),
                            contentDescription = "Filter",
                            modifier = Modifier.size(24.dp)
                        )

                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite"
                        )

                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "ShoppingCart"
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

        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)

            ) {

                Image(

                    painter = painterResource(id = R.drawable.home_background),
                    contentDescription = "Background",
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
                        contentDescription = "User Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(55.dp)
                            .clip(CircleShape)

                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Hi, Aya \nWelcome To Khizana Store",
                        fontSize = 20.sp,
                        //fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    val searchText = remember { mutableStateOf("") }

                    OutlinedTextField(

                        value = searchText.value,
                        onValueChange = { searchText.value = it },
                        placeholder = { Text("Search For products") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.Blue

                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "Brands",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(listOf("ADIDAS", "ADIDAS", "ADIDAS", "ADIDAS" , "ADIDAS")) { category ->

                        CategoryItem(category = category)

                    }

                }

                Spacer(modifier = Modifier.height(24.dp))


                // Adds --------------------------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)

                ) {

                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color(0xFFE3F2FD))

                    ) {

                        Column(

                            modifier = Modifier.padding(16.dp)

                        ) {

                            Text(
                                text = "Coupons",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                //-------------------------------------------

                Spacer(modifier = Modifier.height(24.dp))


                Row(

                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(
                        text = "Products",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "See More Products",
                        color = Color.Blue,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))


                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listOf("CLASSIC BACKPACK", "CHERRY SMOOTH", "CHERRY SMOOTH","CHERRY SMOOTH","CHERRY SMOOTH")) { product ->
                        ProductItem(product = product)
                    }

                }

            }
        }
    }
}

@Composable
fun CategoryItem(category: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.size(80.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.person),
                contentDescription = "test category",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = category)
        }
    }
}


@Composable
fun ProductItem(product: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.size(110.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.person),
                contentDescription = "test category",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = product, fontSize = 10.sp)
        }
    }
}
