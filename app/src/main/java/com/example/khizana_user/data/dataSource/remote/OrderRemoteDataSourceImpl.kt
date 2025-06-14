package com.example.khizana_user.data.dataSource.remote

import android.util.Log
import com.example.khizana_user.data.dataSource.remote.api.ShopifyDraftOrderService
import com.example.khizana_user.data.dto.OrderDto
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderDto
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderRequest
import com.example.khizana_user.data.repository.OrderRemoteDataSource
import retrofit2.Response
import javax.inject.Inject

class OrderRemoteDataSourceImpl @Inject constructor(
    private val api: ShopifyDraftOrderService
) : OrderRemoteDataSource {

    override suspend fun completeDraftOrder(id: Long) {
        Log.i("OrderRemote", "Calling completeDraftOrder for draft ID: $id")
        val res = api.completeDraftOrder(id)

        if (!res.isSuccessful) {
            val errorBody = res.errorBody()?.string()
            Log.e("OrderRemote", "completeDraftOrder failed: ${res.code()} - $errorBody")
            throw Exception("Complete failed: $errorBody")
        }

        val completedDraft = getDraftOrder(id)
        Log.i("OrderRemote", "Draft completed. completedAt: ${completedDraft.completedAt}")

        if (completedDraft.completedAt == null) {
            Log.e("OrderRemote", "Shopify did not mark draft as completed")
            throw Exception("Draft not marked as completed by Shopify")
        }
    }

    override suspend fun getDraftOrder(id: Long): DraftOrderDto {
        Log.i("OrderRemote", "Fetching draft order with ID: $id")
        val res = api.getDraftOrder(id)

        if (!res.isSuccessful) {
            val errorBody = res.errorBody()?.string()
            Log.e("OrderRemote", "getDraftOrder failed: ${res.code()} - $errorBody")
            throw Exception("Fetch failed: $errorBody")
        }

        val draft = res.body()?.draftOrder
        if (draft == null) {
            Log.e("OrderRemote", "No draft_order found in response body")
            throw Exception("No draft found")
        }

        Log.i("OrderRemote", "Draft fetched successfully")
        return draft
    }

    override suspend fun sendInvoice(id: Long) {
        Log.i("OrderRemote", "Sending invoice for draft ID: $id")
        val res = api.sendInvoice(id)

        if (!res.isSuccessful) {
            val errorBody = res.errorBody()?.string()
            Log.e("OrderRemote", "sendInvoice failed: ${res.code()} - $errorBody")
            throw Exception("Send invoice failed: $errorBody")
        }

        Log.i("OrderRemote", "Invoice sent successfully for draft ID: $id")
    }

    override suspend fun updateDraftOrder(
        draftOrderId: Long,
        draftOrderRequest: DraftOrderRequest
    ): Response<DraftOrderDto> {
        Log.i("OrderRemote", "Updating draft order ID: $draftOrderId")
        Log.i("OrderRemote", "Request Body: $draftOrderRequest")

        val response = api.updateDraftOrder(draftOrderId, draftOrderRequest)

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            Log.e("OrderRemote", """
                Shopify update failed
                Code: ${response.code()}
                Message: ${response.message()}
                Body: $errorBody
            """.trimIndent())
            throw Exception("Update draft failed: $errorBody")
        }

        val draft = response.body()?.draftOrder
        if (draft == null) {
            Log.e("OrderRemote", "Missing draft_order in response body")
            throw Exception("Missing draft_order in response body")
        }

        Log.i("OrderRemote", "Draft updated successfully")
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
}
