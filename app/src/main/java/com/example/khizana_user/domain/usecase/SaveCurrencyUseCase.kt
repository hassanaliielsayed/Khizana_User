package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.repository.CustomerPreferencesRepository
import javax.inject.Inject

class SaveCurrencyUseCase @Inject constructor(
    private val repository: CustomerPreferencesRepository
) {
  suspend operator fun invoke(currency: String) = repository.saveCurrency(currency)
}