package com.example.khizana_user.domain.usecase.cartusecase

import com.example.khizana_user.domain.repository.CartRepository

class ClearCartUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke(customerId: Long): Result<Unit> {
        return repository.clearCart(customerId)
    }
}