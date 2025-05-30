package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dto.BrandResponseDto
import com.example.khizana_user.data.dto.CouponsResponseDto


interface RemoteDataSource {

    suspend fun fetchAllBrands():  List<BrandResponseDto>

    suspend fun getCoupons(): CouponsResponseDto

}