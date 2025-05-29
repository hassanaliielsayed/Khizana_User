
package com.example.khizana_user.data.repository

import com.example.khizana_user.data.repository.mapper.toBrandModel
import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.repository.HomeRepository
import javax.inject.Inject
import kotlin.collections.distinctBy
import kotlin.collections.map


class HomeRepositoryImp @Inject constructor(private val remoteDateSource: RemoteDataSource) : HomeRepository {

    override suspend fun getAllBrands(): List<Brand> {
        var allBrands = remoteDateSource.fetchAllBrands()
            .map { it.toBrandModel() }
            .distinctBy { it.title }

        return allBrands
    }
}