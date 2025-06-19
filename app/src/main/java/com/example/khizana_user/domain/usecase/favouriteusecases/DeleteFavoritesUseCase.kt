package com.example.khizana_user.domain.usecase.favouriteusecases

import com.example.khizana_user.domain.repository.WishlistRepository

class DeleteFavoritesUseCase(
    private val repo: WishlistRepository
) {
    suspend operator fun invoke(customerId: Long) =
        repo.deleteFavoritesDraft(customerId)
}