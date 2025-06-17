package com.example.khizana_user.data.dto.draftorderDto

data class ProductImagesResponse(
    val images: List<ProductImageDto>
)

data class ProductImageDto(
    val id: Long,
    val src: String
)
