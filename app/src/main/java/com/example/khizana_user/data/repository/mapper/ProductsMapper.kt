package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.ProductDto
import com.example.khizana_user.domain.model.Product


fun ProductDto.toProductModel(): Product {
    return Product(
        id = this.id,
        productTitle = this.title,
        productImage = this.image?.src
    )
}