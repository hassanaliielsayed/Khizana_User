package com.example.khizana_user.domain.model

data class Product(
    val id: Long,
    val productTitle: String,
    val productImage: String?,
    val variantId: Long?,
)
