package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.repositoryInterfaces.HomeRepositoryIn
import javax.inject.Inject

class GetAllBrandsUseCase @Inject constructor(private val repository: HomeRepositoryIn) {
    suspend operator fun invoke(): List<Brand> = repository.getAllBrands()
}

