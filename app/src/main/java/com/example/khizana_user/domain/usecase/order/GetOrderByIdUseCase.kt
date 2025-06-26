package com.example.khizana_user.domain.usecase.order

import com.example.khizana_user.domain.model.Orders
import com.example.khizana_user.domain.repository.OrderRepository

class GetOrderByIdUseCase(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(orderId: Long): Orders {
        return repository.getOrder(orderId)
    }
}
