package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.ProductDetails
import com.example.khizana_user.domain.repository.ProductRepository

class GetProductDetailsUseCase(
    private val repository: ProductRepository
) {
    suspend fun byProductId(id: Long): ProductDetails {
        return repository.getProductById(id)
    }

    suspend fun byVariantId(variantId: Long): ProductDetails {
        return repository.getProductByVariantId(variantId)
    }
}
