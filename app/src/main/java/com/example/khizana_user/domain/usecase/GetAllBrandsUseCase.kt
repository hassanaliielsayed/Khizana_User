package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.repository.HomeRepository

class GetAllBrandsUseCase(private val repository: HomeRepository) {
    suspend operator fun invoke(): List<Brand> = repository.getAllBrands()
}

