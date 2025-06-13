package com.example.khizana_user.domain.model

data class Orders(
    val id: Long,
    val email: String?,
    val createdAt: String,
    val totalPrice: String,
    val currency: String,
    val financialStatus: String
)
