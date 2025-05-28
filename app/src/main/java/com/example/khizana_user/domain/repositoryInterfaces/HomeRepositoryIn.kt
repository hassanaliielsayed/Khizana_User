package com.example.khizana_user.domain.repositoryInterfaces

import com.example.khizana_user.domain.model.Brand


interface HomeRepositoryIn {
    suspend fun getAllBrands(): List<Brand>
}