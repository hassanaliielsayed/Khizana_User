package com.example.khizana_user.domain.repository

import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.model.Product

interface HomeRepository {

    suspend fun getAllBrands(): List<Brand>

    suspend fun getCoupons(): List<Coupon>

    suspend fun getAllProductsByBrand(vendor: String): List<Product>

}