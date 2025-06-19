package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.Product
import com.example.khizana_user.domain.repository.HomeRepository

class GetAllProductsUseCase(private val repository: HomeRepository) {
    suspend operator fun invoke(vendor: String): List<Product> {
        return repository.getAllProductsByBrand(vendor)
    }
}