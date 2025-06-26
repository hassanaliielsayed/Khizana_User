package com.example.khizana_user.domain.repository

import com.example.khizana_user.domain.model.ProductDetails

interface ProductRepository {
    
    suspend fun getProductById(id: Long): ProductDetails

    suspend fun getProductByVariantId(variantId: Long): ProductDetails

}
