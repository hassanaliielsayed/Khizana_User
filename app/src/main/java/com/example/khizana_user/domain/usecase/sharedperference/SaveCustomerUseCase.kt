package com.example.khizana_user.domain.usecase.sharedperference

import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.repository.CustomerPreferencesRepository
import javax.inject.Inject

class SaveCustomerUseCase @Inject constructor(
    private val repository: CustomerPreferencesRepository
) {
    suspend operator fun invoke(customer: Customer) {
        repository.saveCustomer(customer)
    }
}
