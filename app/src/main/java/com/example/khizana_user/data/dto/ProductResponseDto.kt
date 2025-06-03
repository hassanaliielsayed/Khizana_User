package com.example.khizana_user.data.dto


data class ProductResponseDto(
    val products: List<ProductDto>
)

data class ProductDto(
    val id: Long,
    val title: String,
    val body_html: String?,
    val vendor: String?,
    val product_type: String?,
    val created_at: String?,
    val image: ProductImageDto?
)

data class ProductImageDto(
    val id: Long,
    val src: String
)
