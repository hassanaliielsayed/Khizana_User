package com.example.khizana_user.data.dto

data class OrderResponse(
    val orders: List<OrderDto>
)

data class OrderDto(
    val id: Long,
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
    val title: String,
    val quantity: Int,
    val price: String,
    val sku: String?,
    val image_url: String?
)