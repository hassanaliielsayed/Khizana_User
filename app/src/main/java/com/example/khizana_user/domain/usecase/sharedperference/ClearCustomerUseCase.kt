package com.example.khizana_user.domain.usecase.sharedperference

import com.example.khizana_user.domain.repository.CustomerPreferencesRepository
import javax.inject.Inject

class ClearCustomerUseCase @Inject constructor(
    private val repository: CustomerPreferencesRepository
) {
    suspend operator fun invoke() {
        repository.clearCustomer()
    }
}