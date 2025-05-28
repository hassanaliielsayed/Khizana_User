
package com.example.khizana_user.data.repositoryImpl

import com.example.khizana_user.data.repositoryImpl.mapper.toBrandModel
import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.repositoryInterfaces.HomeRepositoryIn
import javax.inject.Inject
import kotlin.collections.distinctBy
import kotlin.collections.map


class HomeRepositoryImp @Inject constructor(private val remoteDateSource: HomeRemoteDataSourceIn) : HomeRepositoryIn {

    override suspend fun getAllBrands(): List<Brand> {
        var allBrands = remoteDateSource.fetchAllBrands()
            .map { it.toBrandModel() }
            .distinctBy { it.title }

        return allBrands
    }
}