package com.example.khizana_user.data.dataSource.local


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.core.app.ApplicationProvider
import com.example.khizana_user.domain.model.Customer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CustomerPreferencesDataSourceImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var dataSource: CustomerPreferencesDataSourceImpl

    private val testCustomer = Customer(
        id = 1L,
        name = "John Doe",
        email = "john@example.com",
        isVerified = true,
        currency = "USD"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        dataSource = CustomerPreferencesDataSourceImpl(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun save_and_get_customer_should_match() = runTest {
        dataSource.saveCustomer(testCustomer)
        val result = dataSource.getCustomer().first()

        assertNotNull(result)
        assertEquals(testCustomer, result)
    }

    @Test
    fun clear_customer_should_return_null() = runTest {
        dataSource.saveCustomer(testCustomer)
        dataSource.clearCustomer()
        val result = dataSource.getCustomer().first()

        assertNull(result)
    }

    @Test
    fun save_and_get_currency_should_match() = runTest {
        dataSource.saveCurrency("EGP")
        val currency = dataSource.getCurrency().first()
        assertEquals("EGP", currency)
    }

    @Test
    fun save_and_get_address_should_match() = runTest {
        dataSource.saveAddress("Cairo", "Nasr City")
        val (gov, city) = dataSource.getAddress()
        assertEquals("Cairo", gov)
        assertEquals("Nasr City", city)
    }
}