package com.example.khizana_user.domain.usecase.cart

import com.example.khizana_user.domain.repository.CartRepository

class AddToCartUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke(customerId: Long, variantId: Long): Result<Unit> {
        return repository.addToCart(customerId, variantId)
    }
}
