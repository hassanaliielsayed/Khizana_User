package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.Orders
import com.example.khizana_user.domain.repository.OrderRepository
import javax.inject.Inject

class GetOrderByIdUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(orderId: Long): Orders {
        return repository.getOrder(orderId)
    }
}
