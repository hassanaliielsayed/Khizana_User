package com.example.khizana_user.data.dataSource.remote.api

import com.example.khizana_user.data.dto.BrandsResponse
import retrofit2.Response
import retrofit2.http.GET

interface KhizanaAPIService {

    @GET("smart_collections.json")
    suspend fun getAllBrands(
    ): Response<BrandsResponse>


}