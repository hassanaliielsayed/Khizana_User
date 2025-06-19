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
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class GetOrderByIdUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: OrderRepository
    private lateinit var useCase: GetOrderByIdUseCase

    private val mockOrder = Orders(
        id = 1L,
        email = "ayaahmed75383@gmail.com",
        createdAt = "2024-06-18T12:00:00Z",
        totalPrice = "250.00",
        currency = "USD",
        financialStatus = "paid",
        items = listOf(
            OrderItem(
                id = 100L,
                productId = 200L,
                variantId = 300L,
                title = "Test Product",
                quantity = 2,
                price = "125.00",
                sku = "SKU123",
                vendor = "Test Vendor"
            )
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetOrderByIdUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns order from repository`() = runTest {
        val orderId = 1L
        coEvery { repository.getOrder(orderId) } returns mockOrder

        val result = useCase(orderId)

        assertEquals(mockOrder, result)
    }

    @Test
    fun `invoke throws exception if repository throws`() = runTest {
        val orderId = 99L
        coEvery { repository.getOrder(orderId) } throws Exception("Order not found")

        val exception = assertFailsWith<Exception> {
            useCase(orderId)
        }

        assertEquals("Order not found", exception.message)
    }
}
