package com.example.khizana_user.domain.usecase.favourite

import com.example.khizana_user.domain.repository.WishlistRepository

class GetFavoritesUseCase(
    private val repo: WishlistRepository
) {
    suspend operator fun invoke(customerId: Long) = repo.getFavorites(customerId)
}