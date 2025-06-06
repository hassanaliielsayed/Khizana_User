package com.example.khizana_user.data.repository

import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.repository.WishlistRepository
import javax.inject.Inject

class WishlistRepositoryImpl @Inject constructor(
    private val remoteDataSource: WishlistRemoteDataSource
) : WishlistRepository {

    override suspend fun addToFavorites(customerId: Long, variantId: Long): Result<Unit> {
        return remoteDataSource.addToFavorites(customerId, variantId)
    }

    override suspend fun removeFromFavorites(customerId: Long, variantId: Long): Result<Unit> {
        return remoteDataSource.removeFromFavorites(customerId, variantId)
    }

    override suspend fun getFavorites(customerId: Long): Result<FavoriteList> {
        return remoteDataSource.getFavorites(customerId)
    }

    override suspend fun deleteFavoritesDraft(customerId: Long): Result<Unit> {
        return remoteDataSource.deleteFavoritesDraft(customerId)
    }
}
