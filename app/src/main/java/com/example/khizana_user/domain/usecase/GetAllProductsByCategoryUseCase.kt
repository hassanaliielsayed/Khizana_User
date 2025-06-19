package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.ProductByCategory
import com.example.khizana_user.domain.repository.CategoryRepository


class GetAllProductsByCategoryUseCase (private val repository: CategoryRepository) {
    suspend operator fun invoke(): List<ProductByCategory> {
        val response = repository.getAllProducts()
        return response
    }
}