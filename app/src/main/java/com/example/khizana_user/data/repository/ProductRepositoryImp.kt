package com.example.khizana_user.data.repository

import com.example.khizana_user.data.repository.mapper.toDomain
import com.example.khizana_user.domain.model.ProductDetails
import com.example.khizana_user.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : ProductRepository {

    override suspend fun getProductById(id: Long): ProductDetails {
        return remoteDataSource.getProductById(id).toDomain()
    }

    override suspend fun getProductByVariantId(variantId: Long): ProductDetails {
        return remoteDataSource.getProductByVariantId(variantId).toDomain()
    }
}
