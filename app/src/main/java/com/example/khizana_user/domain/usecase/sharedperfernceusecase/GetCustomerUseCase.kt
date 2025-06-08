package com.example.khizana_user.domain.usecase.sharedperfernceusecase

import android.util.Log
import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.repository.CustomerPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetCustomerUseCase @Inject constructor(
    private val repository: CustomerPreferencesRepository
) {
    operator fun invoke(): Flow<Customer?> {
        return repository.getCustomer().onEach { customer ->
            Log.d("GetCustomerUseCase", "Loaded customer: $customer")
        }
    }
}
