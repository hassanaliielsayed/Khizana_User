package com.example.khizana_user.domain.usecase.cartusecase

import com.example.khizana_user.domain.repository.CartRepository

class DecrementFromCartUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke(customerId: Long, variantId: Long): Result<Unit> {
        return repository.decrementFromCart(customerId, variantId)
    }
}