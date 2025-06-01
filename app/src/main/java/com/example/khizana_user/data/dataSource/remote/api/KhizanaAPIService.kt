package com.example.khizana_user.data.dataSource.remote.api

import com.example.khizana_user.data.dto.BrandsResponseDto
import com.example.khizana_user.data.dto.CouponsResponseDto
import com.example.khizana_user.data.dto.ProductDetailsResponseDto
import com.example.khizana_user.data.dto.ProductResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KhizanaAPIService {

    @GET("smart_collections.json")
    suspend fun getAllBrands(
    ): Response<BrandsResponseDto>

    @GET("price_rules.json")
    suspend fun getCoupons(): CouponsResponseDto

    @GET("products.json")
    suspend fun getAllProducts(
        @Query("vendor") vendor: String
    ): Response<ProductResponseDto>

    @GET("products/{id}.json")
    suspend fun getProductById(@Path("id") id: Long): Response<ProductDetailsResponseDto>

}


