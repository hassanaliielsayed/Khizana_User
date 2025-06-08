package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.ProductDetailsDto
import com.example.khizana_user.domain.model.ProductDetails

fun ProductDetailsDto.toDomain(): ProductDetails {
    val firstVariant = variants?.firstOrNull()

    val sizeOptions = options
        ?.firstOrNull { it.name.equals("Size", ignoreCase = true) }
        ?.values ?: emptyList()

    val colorOptions = options
        ?.firstOrNull { it.name.equals("Color", ignoreCase = true) }
        ?.values ?: emptyList()

    return ProductDetails(
        id = this.id,
        variantId = firstVariant?.id, // ✅ mapped
        title = this.title,
        description = this.body_html ?: "",
        images = this.images?.map { it.src } ?: listOfNotNull(this.image?.src),
        sizes = sizeOptions,
        colors = colorOptions,
        price = firstVariant?.price ?: "0.00",
        rating = (3..5).random().toFloat() // temp random rating
    )
}
