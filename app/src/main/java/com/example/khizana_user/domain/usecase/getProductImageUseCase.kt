package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.Orders
import com.example.khizana_user.domain.model.ProductImage
import com.example.khizana_user.domain.repository.OrderRepository
import javax.inject.Inject

class getProductImageUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(productId: Long): List<ProductImage> {
        return repository.getProductImage(productId)
    }
}