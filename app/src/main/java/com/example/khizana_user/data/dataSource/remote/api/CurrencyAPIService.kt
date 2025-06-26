package com.example.khizana_user.data.dataSource.remote.api

import com.example.khizana_user.data.dto.CurrencyResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyAPIService {

    @GET("latest")
    suspend fun getLatestRates(
        @Query("base_currency") baseCurrency: String,
        @Query("currencies") targetCurrency: String
    ): CurrencyResponseDto
}