package com.example.khizana_user.domain.usecase.orderusecase

import com.example.khizana_user.domain.model.Order
import com.example.khizana_user.domain.repository.OrderRepository
import javax.inject.Inject

class GetDraftOrderUseCase @Inject constructor(
    private val repo: OrderRepository
) {
    suspend operator fun invoke(id: Long): Order = repo.getDraftOrder(id)
}