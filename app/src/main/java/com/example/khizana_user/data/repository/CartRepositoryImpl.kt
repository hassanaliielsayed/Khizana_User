package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dataSource.remote.CartRemoteDataSourceImpl
import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.repository.CartRepository
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val remote: CartRemoteDataSourceImpl
) : CartRepository {
    override suspend fun addToCart(customerId: Long, variantId: Long): Result<Unit> {
        return remote.addToCart(customerId, variantId)
    }

    override suspend fun decrementFromCart(customerId: Long, variantId: Long): Result<Unit> {
        return remote.decrementFromCart(customerId, variantId)
    }

    override suspend fun getCart(customerId: Long): FavoriteList {
        return remote.getCart(customerId)
    }

    override suspend fun clearCart(customerId: Long): Result<Unit> {
        return remote.clearCart(customerId)
    }
}