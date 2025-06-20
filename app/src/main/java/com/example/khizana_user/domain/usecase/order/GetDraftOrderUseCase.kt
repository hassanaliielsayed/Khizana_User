package com.example.khizana_user.domain.usecase.order

import com.example.khizana_user.domain.model.Order
import com.example.khizana_user.domain.repository.OrderRepository

class GetDraftOrderUseCase(
    private val repo: OrderRepository
) {
    suspend operator fun invoke(id: Long): Order = repo.getDraftOrder(id)
}