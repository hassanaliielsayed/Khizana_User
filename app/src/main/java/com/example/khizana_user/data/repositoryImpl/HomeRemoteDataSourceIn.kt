package com.example.khizana_user.data.repositoryImpl

import com.example.khizana_user.data.dto.BrandResponse


interface HomeRemoteDataSourceIn {

    suspend fun fetchAllBrands():  List<BrandResponse>

}