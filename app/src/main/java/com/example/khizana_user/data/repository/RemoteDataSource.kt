package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dto.BrandResponseDto
import com.example.khizana_user.data.dto.CouponsResponseDto
import com.example.khizana_user.data.dto.ProductDetailsDto
import com.example.khizana_user.data.dto.ProductDto


interface RemoteDataSource {

    suspend fun fetchAllBrands():  List<BrandResponseDto>

    suspend fun getCoupons(): CouponsResponseDto

    suspend fun fetchAllProducts(vendor: String): List<ProductDto>

    suspend fun getProductById(id: Long): ProductDetailsDto

}