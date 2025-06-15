package com.example.khizana_user.data.dto

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    val orders: List<OrderDto>
)

data class OrderDto(
    val id: Long,
    val name: String,
    val email: String?,
    val created_at: String,
    val total_price: String,
    val currency: String,
    val financial_status: String,
    val line_items: List<LineItemDto>?
)

data class SingleOrderResponse(
    val order: OrderDto
)

data class LineItemDto(
    val id: Long,
    @SerializedName("product_id")
    val productId: Long,
    @SerializedName("variant_id")
    val variantId: Long,
    val title: String,
    val quantity: Int,
    val price: String,
    val sku: String?,
    val vendor: String?,
    @SerializedName("variant_title")
    val variantTitle: String?
)