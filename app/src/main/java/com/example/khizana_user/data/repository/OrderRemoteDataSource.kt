package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dto.OrderDto
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderDto
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderRequest
import retrofit2.Response

interface OrderRemoteDataSource {
    suspend fun completeDraftOrder(id: Long)
    suspend fun getDraftOrder(id: Long): DraftOrderDto
    suspend fun sendInvoice(id: Long)
    suspend fun updateDraftOrder(
        draftOrderId: Long,
        draftOrderRequest: DraftOrderRequest
    ): Response<DraftOrderDto>

    suspend fun getOrdersByCustomerId(customerId: Long):List<OrderDto>

    suspend fun getOrderById(orderId: Long): OrderDto
}