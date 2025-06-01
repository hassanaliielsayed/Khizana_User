package com.example.khizana_user.data.dto

data class ProductDetailsResponseDto(
    val product: ProductDetailsDto
)

data class ProductDetailsDto(
    val id: Long,
    val title: String,
    val body_html: String?,
    val vendor: String?,
    val variants: List<VariantDto>?,
    val images: List<ProductImageDto>?,
    val image: ProductImageDto?
)

data class VariantDto(
    val title: String,
    val price: String
)

