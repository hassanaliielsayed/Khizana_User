package com.example.khizana_user.domain.model

data class ProductByCategory(
    val id: Long,
    val productTitle: String,
    val productImage: String?,
    val productVendor: String?,
    val productTags: List<String>,
    val product_type: String?,
    val productPrice: Double,
    val variantId: Long?,
)
