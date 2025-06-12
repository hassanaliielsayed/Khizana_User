package com.example.khizana_user.data.dataSource.remote.api

import com.example.khizana_user.data.dto.draftorderDto.DraftOrderRequest
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderResponse
import com.example.khizana_user.data.dto.draftorderDto.ProductImagesResponse
import com.example.khizana_user.data.dto.draftorderDto.VariantResponse
import retrofit2.Response
import retrofit2.http.*

interface ShopifyDraftOrderService {
    @GET("draft_orders.json")
    suspend fun getDraftOrders(): Response<DraftOrderResponse>

    @POST("draft_orders.json")
    suspend fun createDraftOrder(@Body body: DraftOrderRequest): Response<DraftOrderResponse>

    @PUT("draft_orders/{id}.json")
    suspend fun updateDraftOrder(
        @Path("id") draftOrderId: Long,
        @Body body: DraftOrderRequest
    ): Response<DraftOrderResponse>

    @DELETE("draft_orders/{id}.json")
    suspend fun deleteDraftOrder(
        @Path("id") draftOrderId: Long
    ): Response<Unit>

    @GET("variants/{variant_id}.json")
    suspend fun getVariantById(@Path("variant_id") variantId: Long): Response<VariantResponse>

    @GET("products/{product_id}/images.json")
    suspend fun getProductImages(
        @Path("product_id") productId: Long
    ): Response<ProductImagesResponse>

//    @PUT("draft_orders/{id}/complete.json")
//    suspend fun completeDraftOrder(
//        @Path("id") draftOrderId: Long
//    ): Any
//
//    @GET("draft_orders/{id}.json")
//    suspend fun getDraftOrder(
//        @Path("id") draftOrderId: Long
//    ): DraftOrderResponse
//
//    @POST("draft_orders/{id}/send_invoice.json")
//    suspend fun sendInvoice(
//        @Path("id") draftOrderId: Long
//    ): Any

    @PUT("draft_orders/{id}/complete.json")
    suspend fun completeDraftOrder(@Path("id") id: Long): Response<DraftOrderResponse>

    @GET("draft_orders/{id}.json")
    suspend fun getDraftOrder(@Path("id") id: Long): Response<DraftOrderResponse>

    @POST("draft_orders/{id}/send_invoice.json")
    suspend fun sendInvoice(@Path("id") id: Long): Response<Unit>
}
