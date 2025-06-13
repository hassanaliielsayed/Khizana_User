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
    val financial_status: String
)

