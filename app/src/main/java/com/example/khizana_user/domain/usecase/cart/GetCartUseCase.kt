package com.example.khizana_user.domain.usecase.cart

import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.repository.CartRepository
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(customerId: Long): FavoriteList {
        return repository.getCart(customerId)
    }
}