package com.example.khizana_user.data.dataSource.remote

import com.example.khizana_user.data.dataSource.remote.api.ShopifyDraftOrderService
import com.example.khizana_user.data.dto.OrderDto
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderDto
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderRequest
import com.example.khizana_user.data.dto.draftorderDto.ProductImageDto
import com.example.khizana_user.data.repository.order.OrderRemoteDataSource
import retrofit2.Response
import javax.inject.Inject

class OrderRemoteDataSourceImpl @Inject constructor(
    private val api: ShopifyDraftOrderService
) : OrderRemoteDataSource {

    override suspend fun completeDraftOrder(id: Long) {
        val body = mapOf(
            "draft_order" to mapOf("send_receipt" to true)
        )

        val res = api.completeDraftOrder(id, body)

        if (!res.isSuccessful) {
            val errorBody = res.errorBody()?.string()
            throw Exception("Complete failed: $errorBody")
        }

        val completedDraft = getDraftOrder(id)

        if (completedDraft.completedAt == null) {
            throw Exception("Draft not marked as completed by Shopify")
        }
    }


    override suspend fun getDraftOrder(id: Long): DraftOrderDto {
        val res = api.getDraftOrder(id)

        if (!res.isSuccessful) {
            val errorBody = res.errorBody()?.string()
            throw Exception("Fetch failed: $errorBody")
        }

        val draft = res.body()?.draftOrder
        if (draft == null) {
            throw Exception("No draft found")
        }

        return draft
    }

    override suspend fun sendInvoice(id: Long) {
        val res = api.sendInvoice(id)

        if (!res.isSuccessful) {
            val errorBody = res.errorBody()?.string()
            throw Exception("Send invoice failed: $errorBody")
        }

    }

    override suspend fun updateDraftOrder(
        draftOrderId: Long,
        draftOrderRequest: DraftOrderRequest
    ): Response<DraftOrderDto> {
        val response = api.updateDraftOrder(draftOrderId, draftOrderRequest)

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            throw Exception("Update draft failed: $errorBody")
        }

        val draft = response.body()?.draftOrder
        if (draft == null) {
            throw Exception("Missing draft_order in response body")
        }
        return Response.success(response.code(), draft)
    }

    override suspend fun getOrdersByCustomerId(customerId: Long):List<OrderDto> {
        val response = api.getOrdersByCustomerId(customerId)
        val body = response.body()

        if (response.isSuccessful && body != null) {

            return body.orders

        } else {

            throw Exception("Error fetching brands: ${response.message()}")

        }
    }

    override suspend fun getOrderById(orderId: Long): OrderDto {
        val response = api.getOrderById(orderId)
        val body = response.body()

        if (response.isSuccessful && body != null) {

            return body.order

        } else {

            throw Exception("Error fetching order details: ${response.message()}")

        }
    }

    override suspend fun getProductImage(productId: Long): List<ProductImageDto> {
        val response = api.getProductImages(productId)
        val body = response.body()

        if (response.isSuccessful && body != null) {

            return body.images

        } else {

            throw Exception("Error fetching order images: ${response.message()}")

        }
    }

}
