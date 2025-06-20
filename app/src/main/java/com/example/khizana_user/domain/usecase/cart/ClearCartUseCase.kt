package com.example.khizana_user.domain.usecase.cart

import com.example.khizana_user.domain.repository.CartRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(customerId: Long): Result<Unit> {
        return repository.clearCart(customerId)
    }
}