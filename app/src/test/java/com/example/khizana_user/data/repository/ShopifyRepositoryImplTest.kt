package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dto.*
import com.example.khizana_user.data.repository.mapper.toDomain
import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.repository.ShopifyRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import org.junit.*
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNull

class ShopifyRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var repository: ShopifyRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        remoteDataSource = mockk()
        repository = ShopifyRepositoryImpl(remoteDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val expectedCustomer = Customer(
        id = 123L,
        name = "John",
        email = "john@example.com",
        isVerified = true,
        currency = "USD"
    )

    private val customerDto = ShopifyCustomerDto(
        id = 123L,
        first_name = "John",
        last_name = "Doe",
        email = "john@example.com",
        verified_email = true,
        state = "enabled",
        orders_count = 0,
        total_spent = "0.00",
        currency = "USD",
        phone = null,
        admin_graphql_api_id = null
    )

    @Test
    fun `registerCustomerInShopify returns success when API is successful`() = runTest {
        val responseDto = ShopifyCustomerCreatedResponse(customer = customerDto)
        val response = Response.success(responseDto)

        coEvery { remoteDataSource.registerShopifyCustomer(any()) } returns response

        val result = repository.registerCustomerInShopify("John", "john@example.com")

        assertTrue(result.isSuccess)
        assertEquals(expectedCustomer, result.getOrNull())
    }

    @Test
    fun `registerCustomerInShopify returns failure when API returns error`() = runTest {
        val errorBody = ResponseBody.create("application/json".toMediaType(), "error")
        val response = Response.error<ShopifyCustomerCreatedResponse>(400, errorBody)

        coEvery { remoteDataSource.registerShopifyCustomer(any()) } returns response

        val result = repository.registerCustomerInShopify("John", "john@example.com")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Shopify error") == true)
    }

    @Test
    fun `searchCustomerByEmail returns customer when found`() = runTest {
        val responseDto = ShopifyCustomerSearchResponseDto(customers = listOf(customerDto))
        val response = Response.success(responseDto)

        coEvery { remoteDataSource.searchShopifyCustomerByEmail("email:john@example.com") } returns response

        val result = repository.searchCustomerByEmail("john@example.com")

        assertTrue(result.isSuccess)
        assertEquals(expectedCustomer, result.getOrNull())
    }

    @Test
    fun `searchCustomerByEmail returns null when no customer found`() = runTest {
        val responseDto = ShopifyCustomerSearchResponseDto(customers = emptyList())
        val response = Response.success(responseDto)

        coEvery { remoteDataSource.searchShopifyCustomerByEmail("email:john@example.com") } returns response

        val result = repository.searchCustomerByEmail("john@example.com")

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `searchCustomerByEmail returns failure when API returns error`() = runTest {
        val errorBody = ResponseBody.create("application/json".toMediaType(), "error")
        val response = Response.error<ShopifyCustomerSearchResponseDto>(500, errorBody)

        coEvery { remoteDataSource.searchShopifyCustomerByEmail("email:john@example.com") } returns response

        val result = repository.searchCustomerByEmail("john@example.com")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Search failed") == true)
    }
}