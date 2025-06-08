package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.ProductDto
import com.example.khizana_user.domain.model.ProductByCategory

fun ProductDto.toProductByCategoryModel(): ProductByCategory {
    return ProductByCategory(
        id = this.id,
        productTitle = this.title,
        productImage = this.image?.src,
        productVendor = this.vendor,
        productTags = this.tags.split(",").map { it.trim() },
        product_type = this.product_type,
        productPrice = this.variants.firstOrNull()?.price?.toDoubleOrNull() ?: 0.0
    )
}
