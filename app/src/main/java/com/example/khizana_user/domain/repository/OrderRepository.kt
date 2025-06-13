package com.example.khizana_user.domain.repository

import com.example.khizana_user.domain.model.Order
import com.example.khizana_user.domain.model.Orders

interface OrderRepository {
    suspend fun completeDraftOrder(id: Long)
    suspend fun getDraftOrder(id: Long): Order
    suspend fun sendInvoice(id: Long)
    suspend fun getOrders(customerId: Long): List<Orders>
}