package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.repositoryInterfaces.HomeRepositoryIn

class GetAllBrandsUseCase(private val repository: HomeRepositoryIn) {
    suspend operator fun invoke(): List<Brand> = repository.getAllBrands()
}

