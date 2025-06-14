package com.example.khizana_user.data.repository

import androidx.room.FtsOptions
import com.example.khizana_user.data.dataSource.remote.CartRemoteDataSourceImpl
import com.example.khizana_user.data.repository.mapper.toDomain
import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.repository.CartRepository
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val remote: CartRemoteDataSourceImpl,
    private val remoteSource: RemoteDataSource,
) : CartRepository {
    override suspend fun addToCart(customerId: Long, variantId: Long): Result<Unit> {
        return remote.addToCart(customerId, variantId)
    }

    override suspend fun decrementFromCart(customerId: Long, variantId: Long): Result<Unit> {
        return remote.decrementFromCart(customerId, variantId)

    }

    override suspend fun removeFromCart(customerId: Long, variantId: Long): Result<Unit> {
        return remote.removeFromCart(customerId, variantId)
    }

    override suspend fun getCart(customerId: Long): FavoriteList {
        return remote.getCart(customerId)
    }

    override suspend fun clearCart(customerId: Long): Result<Unit> {
        return remote.clearCart(customerId)
    }

    override suspend fun fetchCoupon(code: String): List<Coupon> = remoteSource.fetchCoupon(code).toDomain()

}