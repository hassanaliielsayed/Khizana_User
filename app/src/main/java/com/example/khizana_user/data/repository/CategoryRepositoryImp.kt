package com.example.khizana_user.data.repository

import android.util.Log
import com.example.khizana_user.data.repository.mapper.toProductByCategoryModel
import com.example.khizana_user.domain.model.ProductByCategory
import com.example.khizana_user.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImp @Inject constructor(private val remoteDateSource: RemoteDataSource) : CategoryRepository {

    override suspend fun getAllProducts(): List<ProductByCategory> {

        val response =  remoteDateSource.fetchAllProducts()
            .map { it.toProductByCategoryModel() }
           // .distinctBy { it.productTitle }

        Log.d("repository", "getAllProducts: ${response.size}")

        return response
    }
}