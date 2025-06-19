
package com.example.khizana_user.data.repository

import com.example.khizana_user.data.repository.mapper.toBrandModel
import com.example.khizana_user.data.repository.mapper.toDomain
import com.example.khizana_user.data.repository.mapper.toProductModel
import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.model.Product
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

    override suspend fun getCoupons(): List<Coupon> = remoteDateSource.getCoupons().toDomain()

    override suspend fun getAllProductsByBrand(vendor: String): List<Product> {

        return remoteDateSource.fetchAllProducts(vendor)
            .map { it.toProductModel() }
            .distinctBy { it.productTitle }

    }

}