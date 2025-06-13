package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dto.OrderDto
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderDto

interface OrderRemoteDataSource {
    suspend fun completeDraftOrder(id: Long)
    suspend fun getDraftOrder(id: Long): DraftOrderDto
    suspend fun sendInvoice(id: Long)
    suspend fun getOrdersByCustomerId(customerId: Long):List<OrderDto>
}