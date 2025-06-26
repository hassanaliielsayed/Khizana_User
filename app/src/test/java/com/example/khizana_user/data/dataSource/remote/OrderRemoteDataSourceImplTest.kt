@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.khizana_user.data.dataSource.remote

import com.example.khizana_user.data.dataSource.remote.api.ShopifyDraftOrderService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import com.example.khizana_user.data.dto.*
import com.example.khizana_user.data.repository.order.OrderRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class OrderRemoteDataSourceImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var api: ShopifyDraftOrderService
    private lateinit var dataSource: OrderRemoteDataSource

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        dataSource = OrderRemoteDataSourceImpl(api)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getOrdersByCustomerId returns orders on success`() = runTest {
        val orders = listOf(OrderDto(1L, "Order1", "ayaahmed75383@gmail.com", "2024-06-01", "100.0", "USD", "paid", emptyList()))
        val response = OrderResponse(orders = orders)

        coEvery { api.getOrdersByCustomerId(1L) } returns Response.success(response)

        val result = dataSource.getOrdersByCustomerId(1L)

        assertEquals(1, result.size)
        assertEquals("Order1", result[0].name)
    }

    @Test(expected = Exception::class)
    fun `getOrdersByCustomerId throws on failure`() = runTest {
        coEvery { api.getOrdersByCustomerId(1L) } returns Response.error(
            500,
            mockk(relaxed = true)
        )

        dataSource.getOrdersByCustomerId(1L)
    }

    @Test
    fun `getOrderById returns order on success`() = runTest {
        val order = OrderDto(1L, "Order1", "ayaahmed75383@gmail.com", "2024-06-01", "100.0", "USD", "paid", emptyList())
        val response = SingleOrderResponse(order = order)

        coEvery { api.getOrderById(1L) } returns Response.success(response)

        val result = dataSource.getOrderById(1L)

        assertEquals(1L, result.id)
    }

    @Test(expected = Exception::class)
    fun `getOrderById throws on failure`() = runTest {
        coEvery { api.getOrderById(1L) } returns Response.error(
            404,
            mockk(relaxed = true)
        )

        dataSource.getOrderById(1L)
    }

}
