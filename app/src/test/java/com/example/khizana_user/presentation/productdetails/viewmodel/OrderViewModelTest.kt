package com.example.khizana_user.presentation.productdetails.viewmodel

import com.example.khizana_user.utils.Result
import com.example.khizana_user.domain.model.Orders
import com.example.khizana_user.domain.usecase.order.CompleteDraftOrderUseCase
import com.example.khizana_user.domain.usecase.order.GetDraftOrderUseCase
import com.example.khizana_user.domain.usecase.order.GetOrderByIdUseCase
import com.example.khizana_user.domain.usecase.order.GetOrdersByCustomerIdUseCase
import com.example.khizana_user.domain.usecase.order.SendInvoiceUseCase
import com.example.khizana_user.domain.usecase.order.UpdateDraftOrderUseCase
import com.example.khizana_user.domain.usecase.order.getProductImageUseCase
import com.example.khizana_user.presentation.order.viewmodel.OrderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import io.mockk.coEvery
import io.mockk.mockk


@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class OrderViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var completeOrderUseCase: CompleteDraftOrderUseCase
    private lateinit var getDraftOrderUseCase: GetDraftOrderUseCase
    private lateinit var sendInvoiceUseCase: SendInvoiceUseCase
    private lateinit var updateDraftOrderUseCase: UpdateDraftOrderUseCase
    private lateinit var getOrdersByCustomerIdUseCase: GetOrdersByCustomerIdUseCase
    private lateinit var getOrderByIdUseCase: GetOrderByIdUseCase
    private lateinit var getProductImageUseCase: getProductImageUseCase

    private lateinit var viewModel: OrderViewModel

    private val mockOrder = Orders(
        id = 1L,
        email = "ayaahmed75383@gmail.com.com",
        createdAt = "2024-06-01",
        totalPrice = "100.00",
        currency = "USD",
        financialStatus = "paid",
        items = emptyList()
    )
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        completeOrderUseCase = mockk()
        getDraftOrderUseCase = mockk()
        sendInvoiceUseCase = mockk()
        updateDraftOrderUseCase = mockk()
        getOrdersByCustomerIdUseCase = mockk()
        getOrderByIdUseCase = mockk()
        getProductImageUseCase = mockk()

        viewModel = OrderViewModel(
            completeOrderUseCase,
            getDraftOrderUseCase,
            sendInvoiceUseCase,
            updateDraftOrderUseCase,
            getOrdersByCustomerIdUseCase,
            getOrderByIdUseCase,
            getProductImageUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchOrders success updates orders`() = runTest {
        coEvery { getOrdersByCustomerIdUseCase(1L) } returns listOf(mockOrder)

        viewModel.fetchOrders(1L)
        advanceUntilIdle()

        val result = viewModel.orders.value
        assertTrue(result is Result.Success)
        assertEquals(1, (result as Result.Success).data.size)
    }

    @Test
    fun `fetchOrders failure updates orders to Error`() = runTest {
        coEvery { getOrdersByCustomerIdUseCase(1L) } throws Exception("Orders error")

        viewModel.fetchOrders(1L)
        advanceUntilIdle()

        val result = viewModel.orders.value
        assertTrue(result is Result.Error)
        assertEquals("Orders error", (result as Result.Error).message)
    }

    @Test
    fun `fetchOrderDetails success updates orderDetails`() = runTest {
        coEvery { getOrderByIdUseCase(1L) } returns mockOrder

        viewModel.fetchOrderDetails(1L)
        advanceUntilIdle()

        val result = viewModel.orderDetails.value
        assertTrue(result is Result.Success)
        assertEquals(1L, (result as Result.Success).data.id)
    }

    @Test
    fun `fetchOrderDetails failure updates orderDetails to Error`() = runTest {
        coEvery { getOrderByIdUseCase(1L) } throws Exception("Details error")

        viewModel.fetchOrderDetails(1L)
        advanceUntilIdle()

        val result = viewModel.orderDetails.value
        assertTrue(result is Result.Error)
        assertEquals("Details error", (result as Result.Error).message)
    }

}
