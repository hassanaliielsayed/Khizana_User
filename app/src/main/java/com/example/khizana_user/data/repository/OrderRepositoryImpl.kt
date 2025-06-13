package com.example.khizana_user.data.repository

import com.example.khizana_user.data.repository.mapper.toDomain
import com.example.khizana_user.data.repository.mapper.toOrder
import com.example.khizana_user.domain.model.Order
import com.example.khizana_user.domain.model.Orders
import com.example.khizana_user.domain.repository.OrderRepository
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val remote: OrderRemoteDataSource
) : OrderRepository {
    override suspend fun completeDraftOrder(id: Long) = remote.completeDraftOrder(id)

    override suspend fun getDraftOrder(id: Long): Order =
        remote.getDraftOrder(id).toOrder()

    override suspend fun sendInvoice(id: Long) = remote.sendInvoice(id)

    override suspend fun getOrders(customerId: Long): List<Orders> {

        return remote.getOrdersByCustomerId(customerId).map { it.toDomain() }
    }
}