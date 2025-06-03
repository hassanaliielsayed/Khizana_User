package com.example.khizana_user.data.repository

import com.example.khizana_user.data.repository.mapper.toDomain
import com.example.khizana_user.domain.model.CurrencyRate
import com.example.khizana_user.domain.repository.SettingRepository
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val remoteDateSource: RemoteDataSource
) : SettingRepository {
    override suspend fun getCurrencyRate(base: String, target: String): CurrencyRate {

        val response = remoteDateSource.getCurrencyRate(base, target)
        return response.data[target]?.toDomain() ?: throw Exception("Currency not found")

    }
}