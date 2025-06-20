package com.example.khizana_user.data.repository.sharedpref

import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.repository.CustomerPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CustomerPreferencesRepositoryImpl @Inject constructor(
    private val localDataSource: CustomerPreferencesDataSource
) : CustomerPreferencesRepository {

    override suspend fun saveCustomer(customer: Customer) =
        localDataSource.saveCustomer(customer)

    override fun getCustomer(): Flow<Customer?> =
        localDataSource.getCustomer()

    override suspend fun clearCustomer() =
        localDataSource.clearCustomer()

    override suspend fun saveCurrency(currency: String) = localDataSource.saveCurrency(currency)


    override fun getCurrency(): Flow<String?> = localDataSource.getCurrency()

    override suspend fun saveAddress(governorate: String, city: String) = localDataSource.saveAddress(governorate, city)

    override suspend fun getAddress(): Pair<String?, String?> = localDataSource.getAddress()


}
