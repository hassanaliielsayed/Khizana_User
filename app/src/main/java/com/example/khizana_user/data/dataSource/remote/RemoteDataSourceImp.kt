package com.example.khizana_user.data.dataSource.remote

import android.util.Log
import com.example.khizana_user.data.dataSource.remote.api.KhizanaAPIService
import com.example.khizana_user.data.dto.BrandResponse
import com.example.khizana_user.data.repository.RemoteDataSource
import javax.inject.Inject


class RemoteDataSourceImp @Inject constructor(private val apiService: KhizanaAPIService) : RemoteDataSource {

    override suspend fun fetchAllBrands():  List<BrandResponse> {

        val response = apiService.getAllBrands()
        val body = response.body()

        Log.d("AllBrandsDebug", "code: ${response.code()}")
        Log.d("AllBrandsDebug", "All Brands body: ${response.body()?.allBrands}")

        if (response.isSuccessful && body != null) {

            return body.allBrands

        } else {

            throw Exception("Error fetching brands: ${response.message()}")

        }
    }
}
