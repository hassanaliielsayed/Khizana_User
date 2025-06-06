package com.example.khizana_user.data.dto.draftorderDto

data class ProductImagesResponse(
    val images: List<ProductImage>
)

data class ProductImage(
    val id: Long,
    val src: String
)
