package com.example.khizana_user.domain.repository

import com.example.khizana_user.domain.model.FavoriteList

interface WishlistRepository {
    suspend fun addToFavorites(customerId: Long, variantId: Long): Result<Unit>

    suspend fun removeFromFavorites(customerId: Long, variantId: Long): Result<Unit>

    suspend fun getFavorites(customerId: Long): Result<FavoriteList>

    suspend fun deleteFavoritesDraft(customerId: Long): Result<Unit>
}
