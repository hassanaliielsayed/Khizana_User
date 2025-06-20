package com.example.khizana_user.domain.usecase.favourite

import com.example.khizana_user.domain.repository.WishlistRepository
import javax.inject.Inject

class AddToFavoritesUseCase @Inject constructor(
    private val repo: WishlistRepository
) {
    suspend operator fun invoke(customerId: Long, variantId: Long) =
        repo.addToFavorites(customerId, variantId)
}