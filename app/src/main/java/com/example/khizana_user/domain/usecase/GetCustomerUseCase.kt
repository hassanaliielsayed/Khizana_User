package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.repository.CustomerPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCustomerUseCase @Inject constructor(
    private val repository: CustomerPreferencesRepository
) {
    operator fun invoke(): Flow<Customer?> {
        return repository.getCustomer()
    }
}