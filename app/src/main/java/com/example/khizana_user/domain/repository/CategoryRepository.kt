package com.example.khizana_user.domain.repository

import com.example.khizana_user.domain.model.ProductByCategory

interface CategoryRepository {

    suspend fun getAllProducts(): List<ProductByCategory>

}