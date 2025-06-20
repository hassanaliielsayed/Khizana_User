package com.example.khizana_user.domain.usecase.details

import com.example.khizana_user.domain.model.ProductDetails
import com.example.khizana_user.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductDetailsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend fun byProductId(id: Long): ProductDetails {
        return repository.getProductById(id)
    }

    suspend fun byVariantId(variantId: Long): ProductDetails {
        return repository.getProductByVariantId(variantId)
    }
}
