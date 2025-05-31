package com.example.khizana_user.domain.repository

import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.model.Coupon


interface HomeRepository {
    suspend fun getAllBrands(): List<Brand>

    suspend fun getCoupons(): List<Coupon>

}