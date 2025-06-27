package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dto.OrderDto
import com.example.khizana_user.data.repository.order.OrderRemoteDataSource
import com.example.khizana_user.data.repository.order.OrderRepositoryImpl
import com.example.khizana_user.domain.repository.OrderRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.*
import kotlin.test.assertEquals

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class OrderRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var remote: OrderRemoteDataSource
    private lateinit var repository: OrderRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        remote = mockk()
        repository = OrderRepositoryImpl(remote)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getOrders returns mapped orders`() = runTest {
        val orderDto = OrderDto(
            id = 1L,
            name = "Order1",
            email = "ayaahmed75383@gmail.com",
            created_at = "2024-01-01T00:00:00Z",
            total_price = "200.0",
            currency = "USD",
            financial_status = "paid",
            line_items = emptyList()
        )

        coEvery { remote.getOrdersByCustomerId(1L) } returns listOf(orderDto)

        val result = repository.getOrders(1L)

        assertEquals(1, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("200.0", result[0].totalPrice)
    }

    @Test
    fun `getOrder returns mapped order`() = runTest {
        val orderDto = OrderDto(
            id = 1L,
            name = "Order1",
            email = "ayaahmed75383@gmail.com",
            created_at = "2024-01-01T00:00:00Z",
            total_price = "200.0",
            currency = "USD",
            financial_status = "paid",
            line_items = emptyList()
        )

        coEvery { remote.getOrderById(1L) } returns orderDto

        val result = repository.getOrder(1L)

        assertEquals(1L, result.id)
        assertEquals("ayaahmed75383@gmail.com", result.email)
    }

}
