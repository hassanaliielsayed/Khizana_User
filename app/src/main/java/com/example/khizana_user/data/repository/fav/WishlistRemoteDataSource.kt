package com.example.khizana_user.data.repository.fav

import com.example.khizana_user.domain.model.FavoriteList

interface WishlistRemoteDataSource {

    suspend fun addToFavorites(customerId: Long, variantId: Long): Result<Unit>

    suspend fun removeFromFavorites(customerId: Long, variantId: Long): Result<Unit>

    suspend fun getFavorites(customerId: Long): Result<FavoriteList>

    suspend fun deleteFavoritesDraft(customerId: Long): Result<Unit>
}