package com.example.khizana_user.domain.usecase.favourite

import com.example.khizana_user.domain.repository.WishlistRepository

class AddToFavoritesUseCase (
    private val repo: WishlistRepository
) {
    suspend operator fun invoke(customerId: Long, variantId: Long) =
        repo.addToFavorites(customerId, variantId)
}