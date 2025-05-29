package com.example.khizana_user.domain.repository

import com.example.khizana_user.domain.model.Brand


interface HomeRepository {
    suspend fun getAllBrands(): List<Brand>
}