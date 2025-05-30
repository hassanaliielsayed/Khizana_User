package com.example.khizana_user.data.dataSource.remote.api

import com.example.khizana_user.data.dto.BrandsResponseDto
import com.example.khizana_user.data.dto.CouponsResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface KhizanaAPIService {

    @GET("smart_collections.json")
    suspend fun getAllBrands(
    ): Response<BrandsResponseDto>

    @GET("price_rules.json")
    suspend fun getCoupons(): CouponsResponseDto


}


