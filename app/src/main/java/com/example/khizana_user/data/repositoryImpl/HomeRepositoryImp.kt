
package com.example.khizana_user.data.repositoryImpl


import com.example.khizana_user.data.repositoryImpl.mapper.toDomain
import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.repositoryInterfaces.HomeRepositoryIn
import kotlin.collections.distinctBy
import kotlin.collections.map


class HomeRepositoryImp(private val remoteDateSource: HomeRemoteDataSourceIn) : HomeRepositoryIn {

    override suspend fun getAllBrands(): List<Brand> {
        var allBrands = remoteDateSource.fetchAllBrands()
            .map { it.toDomain() }
            .distinctBy { it.title }

        return allBrands
    }
}