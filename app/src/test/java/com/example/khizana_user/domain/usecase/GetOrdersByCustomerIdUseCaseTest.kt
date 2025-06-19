package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.OrderItem
import com.example.khizana_user.domain.model.Orders
import com.example.khizana_user.domain.repository.OrderRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.Dispatchers
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class GetOrdersByCustomerIdUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: OrderRepository
    private lateinit var useCase: GetOrdersByCustomerIdUseCase

    private val mockOrders = listOf(
        Orders(
            id = 1L,
            email = "ayaahmed75383@gmail.com",
            createdAt = "2024-06-01T12:00:00",
            totalPrice = "100.00",
            currency = "USD",
            financialStatus = "paid",
            items = listOf(
                OrderItem(
                    id = 101L,
                    productId = 1001L,
                    variantId = 2001L,
                    title = "Product 1",
                    quantity = 2,
                    price = "50.00",
                    sku = "SKU-1",
                    vendor = "Vendor 1"
                )
            )
        ),
        Orders(
            id = 2L,
            email = "ayaahmed75383@gmail.com",
            createdAt = "2024-06-05T15:30:00",
            totalPrice = "200.00",
            currency = "USD",
            financialStatus = "pending",
            items = emptyList()
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetOrdersByCustomerIdUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns orders list from repository`() = runTest {
        val customerId = 123L
        coEvery { repository.getOrders(customerId) } returns mockOrders

        val result = useCase(customerId)

        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("paid", result[0].financialStatus)
        assertEquals(2L, result[1].id)
        assertEquals("pending", result[1].financialStatus)
    }

    @Test
    fun `invoke returns empty list when repository returns empty`() = runTest {
        val customerId = 456L
        coEvery { repository.getOrders(customerId) } returns emptyList()

        val result = useCase(customerId)

        assertTrue(result.isEmpty())
    }
}
