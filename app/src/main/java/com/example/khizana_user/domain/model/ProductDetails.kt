package com.example.khizana_user.domain.model


data class ProductDetails(
    val id: Long,
    val variantId: Long?,
    val title: String,
    val description: String,
    val images: List<String>,
    val sizes: List<String>,
    val colors: List<String>,
    val price: String,
    val rating: Float
)
