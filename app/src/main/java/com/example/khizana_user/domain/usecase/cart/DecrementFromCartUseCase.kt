package com.example.khizana_user.domain.usecase.cart

import com.example.khizana_user.domain.repository.CartRepository
import javax.inject.Inject

class DecrementFromCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(customerId: Long, variantId: Long): Result<Unit> {
        return repository.decrementFromCart(customerId, variantId)
    }
}