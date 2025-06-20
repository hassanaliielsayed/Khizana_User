package com.example.khizana_user.domain.usecase.order

import com.example.khizana_user.domain.model.Orders
import com.example.khizana_user.domain.repository.OrderRepository

class GetOrdersByCustomerIdUseCase(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(customerId: Long): List<Orders> {
        return repository.getOrders(customerId)
    }
}
