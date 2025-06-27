package com.example.khizana_user.data.repository.sharedpref

import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.repository.CustomerPreferencesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CustomerPreferencesRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var localDataSource: CustomerPreferencesDataSource
    private lateinit var repository: CustomerPreferencesRepository

    private val mockCustomer = Customer(
        id = 1L,
        name = "Test User",
        email = "test@example.com",
        isVerified = true,
        currency = "USD"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        localDataSource = mockk(relaxed = true)
        repository = CustomerPreferencesRepositoryImpl(localDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveCustomer calls localDataSource`() = runTest {
        repository.saveCustomer(mockCustomer)
        coVerify { localDataSource.saveCustomer(mockCustomer) }
    }

    @Test
    fun `getCustomer returns flow from localDataSource`() = runTest {
        coEvery { localDataSource.getCustomer() } returns flowOf(mockCustomer)

        val result = repository.getCustomer()
        result.collect {
            assertEquals(mockCustomer, it)
        }
    }

    @Test
    fun `clearCustomer calls localDataSource`() = runTest {
        repository.clearCustomer()
        coVerify { localDataSource.clearCustomer() }
    }

    @Test
    fun `saveCurrency calls localDataSource`() = runTest {
        repository.saveCurrency("EGP")
        coVerify { localDataSource.saveCurrency("EGP") }
    }

    @Test
    fun `getCurrency returns flow from localDataSource`() = runTest {
        coEvery { localDataSource.getCurrency() } returns flowOf("USD")

        val result = repository.getCurrency()
        result.collect {
            assertEquals("USD", it)
        }
    }

    @Test
    fun `saveAddress calls localDataSource`() = runTest {
        repository.saveAddress("Giza", "Dokki")
        coVerify { localDataSource.saveAddress("Giza", "Dokki") }
    }

    @Test
    fun `getAddress returns value from localDataSource`() = runTest {
        coEvery { localDataSource.getAddress() } returns Pair("Cairo", "Nasr City")

        val result = repository.getAddress()
        assertEquals(Pair("Cairo", "Nasr City"), result)
    }
}