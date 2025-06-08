package com.example.khizana_user.domain.usecase.cartusecase

import com.example.khizana_user.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(customerId: Long, variantId: Long): Result<Unit> {
        return repository.addToCart(customerId, variantId)
    }
}
