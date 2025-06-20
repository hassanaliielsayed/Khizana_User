package com.example.khizana_user.data.dto


data class ProductResponseDto(
    val products: List<ProductDto>
)

data class ProductDto(
    val id: Long,
    val title: String,
    val body_html: String?,
    val vendor: String?,
    val tags: String,
    val product_type: String?,
    val variants: List<ProductVariantDto>,
    val created_at: String?,
    val image: ProductImageDto?,
)

data class ProductImageDto(
    val id: Long,
    val src: String
)

data class ProductVariantDto(
    val id: Long,
    val price: String
)
