package com.example.khizana_user.data.repository.sharedpref

import com.example.khizana_user.domain.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerPreferencesDataSource {

    suspend fun saveCustomer(customer: Customer)

    fun getCustomer(): Flow<Customer?>

    suspend fun clearCustomer()

    suspend fun saveCurrency(currency: String)

    fun getCurrency(): Flow<String?>

    suspend fun saveAddress(governorate: String, city: String)

    suspend fun getAddress(): Pair<String?, String?>
}
