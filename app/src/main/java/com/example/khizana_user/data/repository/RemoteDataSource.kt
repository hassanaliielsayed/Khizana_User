package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dto.BrandResponseDto
import com.example.khizana_user.data.dto.CouponsResponseDto
import com.example.khizana_user.data.dto.CurrencyResponseDto
import com.example.khizana_user.data.dto.ProductDetailsDto
import com.example.khizana_user.data.dto.ProductDto
import com.example.khizana_user.data.dto.ShopifyCreateCustomerRequest
import com.example.khizana_user.data.dto.ShopifyCustomerCreatedResponse
import com.example.khizana_user.data.dto.ShopifyCustomerSearchResponseDto
import retrofit2.Response


interface RemoteDataSource {

    suspend fun fetchAllBrands():  List<BrandResponseDto>

    suspend fun getCoupons(): CouponsResponseDto

    suspend fun getCurrencyRate(base: String, target: String): CurrencyResponseDto

    suspend fun fetchAllProducts(vendor: String): List<ProductDto>

    suspend fun getProductById(id: Long): ProductDetailsDto

    suspend fun registerShopifyCustomer(request: ShopifyCreateCustomerRequest): Response<ShopifyCustomerCreatedResponse>

    suspend fun searchShopifyCustomerByEmail(query: String): Response<ShopifyCustomerSearchResponseDto>

    suspend fun fetchAllProducts(): List<ProductDto>

}