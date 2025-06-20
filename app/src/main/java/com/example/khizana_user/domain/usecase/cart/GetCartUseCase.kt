package com.example.khizana_user.domain.usecase.cartusecase

import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.repository.CartRepository


class GetCartUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke(customerId: Long): FavoriteList {
        return repository.getCart(customerId)
    }
}