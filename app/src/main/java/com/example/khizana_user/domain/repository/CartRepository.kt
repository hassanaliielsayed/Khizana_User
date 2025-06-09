package com.example.khizana_user.domain.repository

import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.model.FavoriteList

interface CartRepository {
    suspend fun addToCart(customerId: Long, variantId: Long): Result<Unit>

    suspend fun decrementFromCart(customerId: Long, variantId: Long): Result<Unit>

    suspend fun removeFromCart(customerId: Long, variantId: Long): Result<Unit>

    suspend fun getCart(customerId: Long): FavoriteList

    suspend fun clearCart(customerId: Long): Result<Unit>

    suspend fun fetchCoupon(code: String): List<Coupon>

}