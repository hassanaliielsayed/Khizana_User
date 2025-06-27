package com.example.khizana_user.domain.usecase.order

import com.example.khizana_user.domain.repository.OrderRepository

class CompleteDraftOrderUseCase(
    private val repo: OrderRepository
) {
    suspend operator fun invoke(id: Long) = repo.completeDraftOrder(id)
}