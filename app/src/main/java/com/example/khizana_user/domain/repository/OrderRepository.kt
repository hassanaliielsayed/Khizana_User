package com.example.khizana_user.domain.repository

import com.example.khizana_user.domain.model.Order

interface OrderRepository {
    suspend fun completeDraftOrder(id: Long)
    suspend fun getDraftOrder(id: Long): Order
    suspend fun sendInvoice(id: Long)
}