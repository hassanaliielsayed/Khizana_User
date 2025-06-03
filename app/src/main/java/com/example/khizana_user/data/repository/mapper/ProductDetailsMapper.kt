package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.ProductDetailsDto
import com.example.khizana_user.domain.model.ProductDetails

fun ProductDetailsDto.toDomain(): ProductDetails {
    return ProductDetails(
        id = this.id,
        title = this.title,
        description = this.body_html ?: "",
        images = this.images?.map { it.src } ?: listOfNotNull(this.image?.src),
        sizes = this.variants?.map { it.title } ?: emptyList(),
        price = this.variants?.firstOrNull()?.price ?: "0.00",
        rating = (3..5).random().toFloat() // temp
    )
}