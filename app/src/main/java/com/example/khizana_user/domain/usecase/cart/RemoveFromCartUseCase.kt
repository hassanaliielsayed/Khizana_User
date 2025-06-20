package com.example.khizana_user.domain.usecase.cart

import com.example.khizana_user.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(customerId: Long, variantId: Long): Result<Unit> {
        return repository.removeFromCart(customerId, variantId)
    }
}