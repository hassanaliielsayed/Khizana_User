package com.example.khizana_user.data.api

import com.example.khizana_user.data.dto.AllBrandsResponse
import retrofit2.Response
import retrofit2.http.GET

interface KhizanaAPIService {

    @GET("smart_collections.json")
    suspend fun getAllBrands(
    ): Response<AllBrandsResponse>


}