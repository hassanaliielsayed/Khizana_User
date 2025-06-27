package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.ProductDto
import com.example.khizana_user.domain.model.Product


fun ProductDto.toProductModel(): Product {
    val firstVariant = variants?.firstOrNull()
    return Product(
        id = this.id,
        productTitle = this.title,
        productImage = this.image?.src,
        variantId = firstVariant?.id
    )
}