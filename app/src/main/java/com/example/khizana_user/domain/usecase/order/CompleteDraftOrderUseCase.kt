package com.example.khizana_user.domain.usecase.order

import com.example.khizana_user.domain.repository.OrderRepository
import javax.inject.Inject

class CompleteDraftOrderUseCase @Inject constructor(
    private val repo: OrderRepository
) {
    suspend operator fun invoke(id: Long) = repo.completeDraftOrder(id)
}