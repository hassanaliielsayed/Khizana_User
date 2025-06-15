package com.example.khizana_user.domain.model

data class Orders(
    val id: Long,
    val email: String?,
    val createdAt: String,
    val totalPrice: String,
    val currency: String,
    val financialStatus: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val id: Long,
    val productId: Long,
    val variantId: Long,
    val title: String,
    val quantity: Int,
    val price: String,
    val sku: String?,
    val vendor: String?,
)

