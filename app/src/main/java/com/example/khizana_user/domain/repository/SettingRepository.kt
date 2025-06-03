package com.example.khizana_user.domain.repository

import com.example.khizana_user.domain.model.CurrencyRate

interface SettingRepository {

    suspend fun getCurrencyRate(base: String, target: String): CurrencyRate

}