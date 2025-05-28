package com.example.khizana_user.presentation.home.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.khizana_user.data.api.KhizanaClient
import com.example.khizana_user.data.remote.HomeRemoteDataSourceImp
import com.example.khizana_user.data.repositoryImpl.HomeRepositoryImp
import com.example.khizana_user.domain.usecase.GetAllBrandsUseCase
import com.example.khizana_user.presentation.home.viewModel.AllBrandsViewModelFactory
import com.example.khizana_user.presentation.home.viewModel.BrandViewModel


@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: BrandViewModel = viewModel(
    factory = AllBrandsViewModelFactory(GetAllBrandsUseCase(
        HomeRepositoryImp(
            HomeRemoteDataSourceImp(KhizanaClient.getInstance())
        ))
    )
)) {

    val brands by viewModel.brands.collectAsState()
    val error by viewModel.error.collectAsState()

        LazyColumn {

        items(brands) { brand ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                GlideImage(
                    model = brand.imageUrl ?: "",
                    contentDescription = "Brand Logo",
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(text = brand.title)
            }
        }
    }


}