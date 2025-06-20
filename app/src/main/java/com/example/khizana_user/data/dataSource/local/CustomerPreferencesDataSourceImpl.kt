package com.example.khizana_user.data.dataSource.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.khizana_user.data.repository.sharedpref.CustomerPreferencesDataSource
import com.example.khizana_user.domain.model.Customer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "customer_prefs")

class CustomerPreferencesDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CustomerPreferencesDataSource {

    companion object {
        private val CUSTOMER_ID = longPreferencesKey("customer_id")
        private val CUSTOMER_NAME = stringPreferencesKey("customer_name")
        private val CUSTOMER_EMAIL = stringPreferencesKey("customer_email")
        private val CUSTOMER_VERIFIED = booleanPreferencesKey("customer_verified")
        private val CUSTOMER_CURRENCY = stringPreferencesKey("customer_currency")
        private val GOVERNORATE = stringPreferencesKey("customer_governorate")
        private val CITY = stringPreferencesKey("customer_city")

        private const val TAG = "CustomerPrefs"
    }

    override suspend fun saveCustomer(customer: Customer) {
        Log.d(TAG, "Saving customer: $customer")
        context.dataStore.edit { prefs ->
            prefs[CUSTOMER_ID] = customer.id
            prefs[CUSTOMER_NAME] = customer.name
            prefs[CUSTOMER_EMAIL] = customer.email
            prefs[CUSTOMER_VERIFIED] = customer.isVerified
            prefs[CUSTOMER_CURRENCY] = customer.currency
        }
    }

    override fun getCustomer(): Flow<Customer?> {
        return context.dataStore.data.map { prefs ->
            val id = prefs[CUSTOMER_ID] ?: return@map null
            val name = prefs[CUSTOMER_NAME] ?: return@map null
            val email = prefs[CUSTOMER_EMAIL] ?: return@map null
            val verified = prefs[CUSTOMER_VERIFIED] ?: false
            val currency = prefs[CUSTOMER_CURRENCY] ?: "USD"
            val customer = Customer(id, name, email, verified, currency)
            Log.d(TAG, "Loaded customer from DataStore: $customer")
            customer
        }
    }

    override suspend fun clearCustomer() {
        Log.d(TAG, "Clearing all customer data from DataStore")
        context.dataStore.edit { it.clear() }
    }

    override suspend fun saveCurrency(currency: String) {
        context.dataStore.edit { prefs ->
            prefs[CUSTOMER_CURRENCY] = currency
        }
    }

    override fun getCurrency(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[CUSTOMER_CURRENCY]
        }
    }

    override suspend fun saveAddress(governorate: String, city: String) {
        context.dataStore.edit { prefs ->
            prefs[GOVERNORATE] = governorate
            prefs[CITY] = city
        }
        Log.d(TAG, "Saved address: Governorate=$governorate, City=$city")
    }

    override suspend fun getAddress(): Pair<String?, String?> {
        return context.dataStore.data.map { prefs ->
            Pair(prefs[GOVERNORATE], prefs[CITY])
        }.first()
    }


}
