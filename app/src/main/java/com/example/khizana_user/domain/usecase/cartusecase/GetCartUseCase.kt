package com.example.khizana_user.domain.usecase.cartusecase

import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.repository.CartRepository
import com.example.khizana_user.utils.Result
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(customerId: Long): FavoriteList {
        return repository.getCart(customerId)
    }
}