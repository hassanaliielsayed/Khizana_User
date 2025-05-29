package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dto.BrandResponse


interface RemoteDataSource {

    suspend fun fetchAllBrands():  List<BrandResponse>

}