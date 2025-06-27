@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.khizana_user.presentation.cart.viewmodel

import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.usecase.cart.*
import com.example.khizana_user.domain.usecase.cartusecase.GetCartUseCase
import com.example.khizana_user.utils.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class CartViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var addToCartUseCase: AddToCartUseCase
    private lateinit var decrementFromCartUseCase: DecrementFromCartUseCase
    private lateinit var getCartUseCase: GetCartUseCase
    private lateinit var clearCartUseCase: ClearCartUseCase
    private lateinit var validateCouponUseCase: ValidateCouponUseCase
    private lateinit var removeFromCartUseCase: RemoveFromCartUseCase

    private lateinit var viewModel: CartViewModel

    private val customerId = 1L
    private val variantId = 101L

    private val cartList = FavoriteList(
        draftOrderId = 999L,
        customerId = customerId,
        items = listOf(
            FavoriteItem(variantId, "Test Product", 1, 100.0, "url")
        )
    )

    private val coupon = Coupon(
        title = "SALE20",
        id = "20%",
        img = 20,
        discount = 20.0
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        addToCartUseCase = mockk()
        decrementFromCartUseCase = mockk()
        getCartUseCase = mockk()
        clearCartUseCase = mockk()
        validateCouponUseCase = mockk()
        removeFromCartUseCase = mockk()

        viewModel = CartViewModel(
            addToCartUseCase,
            decrementFromCartUseCase,
            getCartUseCase,
            clearCartUseCase,
            validateCouponUseCase,
            removeFromCartUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCart sets Success on valid data`() = runTest {
        coEvery { getCartUseCase(customerId) } returns cartList

        viewModel.loadCart(customerId)
        advanceUntilIdle()

        val result = viewModel.cartState.value
        assertTrue(result is Result.Success)
        assertEquals(cartList, (result as Result.Success).data)
    }

    @Test
    fun `loadCart sets Error on exception`() = runTest {
        coEvery { getCartUseCase(customerId) } throws Exception("load failed")

        viewModel.loadCart(customerId)
        advanceUntilIdle()

        val result = viewModel.cartState.value
        assertTrue(result is Result.Error)
        assertEquals("load failed", (result as Result.Error).message)
    }


    @Test
    fun `validateCoupon sets Error when invalid`() = runTest {
        coEvery { validateCouponUseCase("INVALID") } throws Exception("not found")

        viewModel.validateCoupon("INVALID")
        advanceUntilIdle()

        val result = viewModel.couponState.value
        assertTrue(result is Result.Error)
        assertEquals("not found", (result as Result.Error).message)
    }

    @Test
    fun `addToCart triggers loadCart on success`() = runTest {
        coEvery { addToCartUseCase(customerId, variantId) } returns kotlin.Result.success(Unit)
        coEvery { getCartUseCase(customerId) } returns cartList

        viewModel.addToCart(customerId, variantId)
        advanceUntilIdle()

        val state = viewModel.cartState.value
        assertTrue(state is com.example.khizana_user.utils.Result.Success)
        assertEquals(cartList, (state as com.example.khizana_user.utils.Result.Success).data)
    }


    @Test
    fun `decrementFromCart triggers loadCart on success`() = runTest {
        coEvery { decrementFromCartUseCase(customerId, variantId) } returns kotlin.Result.success(Unit)
        coEvery { getCartUseCase(customerId) } returns cartList

        viewModel.decrementFromCart(customerId, variantId)
        advanceUntilIdle()

        val result = viewModel.cartState.value
        assertTrue(result is Result.Success)
    }

    @Test
    fun `removeFromCart triggers loadCart on success`() = runTest {
        coEvery { removeFromCartUseCase(customerId, variantId) }returns kotlin.Result.success(Unit)
        coEvery { getCartUseCase(customerId) } returns cartList

        viewModel.removeFromCart(customerId, variantId)
        advanceUntilIdle()

        val result = viewModel.cartState.value
        assertTrue(result is Result.Success)
    }

    @Test
    fun `clearCart triggers loadCart on success`() = runTest {
        coEvery { clearCartUseCase(customerId) } returns kotlin.Result.success(Unit)
        coEvery { getCartUseCase(customerId) } returns cartList

        viewModel.clearCart(customerId)
        advanceUntilIdle()

        val result = viewModel.cartState.value
        assertTrue(result is Result.Success)
    }
}