package com.example.khizana_user.domain.usecase.home

import com.example.khizana_user.domain.repository.SettingRepository
import javax.inject.Inject

class GetExchangeRateUseCase @Inject constructor(
    private val repository: SettingRepository
) {

    suspend operator fun invoke(base: String, target: String) = repository.getCurrencyRate(base, target)
}