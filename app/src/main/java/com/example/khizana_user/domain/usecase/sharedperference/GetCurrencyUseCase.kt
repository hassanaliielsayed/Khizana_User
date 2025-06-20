package com.example.khizana_user.domain.usecase.sharedperference

import com.example.khizana_user.domain.repository.CustomerPreferencesRepository
import javax.inject.Inject

class GetCurrencyUseCase @Inject constructor(
    private val repository: CustomerPreferencesRepository
) {
    operator fun invoke() = repository.getCurrency()
}