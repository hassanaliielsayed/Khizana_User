package com.example.khizana_user.domain.usecase

import android.util.Log
import com.example.khizana_user.domain.model.ProductByCategory
import com.example.khizana_user.domain.repository.CategoryRepository
import javax.inject.Inject

class GetAllProductsByCategoryUseCase @Inject constructor(private val repository: CategoryRepository) {
    suspend operator fun invoke(): List<ProductByCategory> {
        val response = repository.getAllProducts()
        Log.d("useCase", "getAllProducts: ${response.size}")
        return response
    }
}