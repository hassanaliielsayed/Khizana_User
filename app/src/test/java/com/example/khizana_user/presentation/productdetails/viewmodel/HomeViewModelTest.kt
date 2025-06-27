package com.example.khizana_user.presentation.productdetails.viewmodel

import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.model.Product
import com.example.khizana_user.domain.usecase.home.GetAllBrandsUseCase
import com.example.khizana_user.domain.usecase.home.GetAllCouponsUseCase
import com.example.khizana_user.domain.usecase.home.GetAllProductsUseCase
import com.example.khizana_user.domain.usecase.home.GetExchangeRateUseCase
import com.example.khizana_user.domain.usecase.sharedperference.GetCurrencyUseCase
import com.example.khizana_user.presentation.home.viewModel.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getAllBrandsUseCase: GetAllBrandsUseCase
    private lateinit var getAllCouponsUseCase: GetAllCouponsUseCase
    private lateinit var getAllProductsUseCase: GetAllProductsUseCase
    private lateinit var getExchangeRateUseCase: GetExchangeRateUseCase
    private lateinit var getCurrencyUseCase: GetCurrencyUseCase

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getAllBrandsUseCase = mockk()
        getAllCouponsUseCase = mockk()
        getAllProductsUseCase = mockk()
        getExchangeRateUseCase = mockk()
        getCurrencyUseCase = mockk()

        coEvery { getCurrencyUseCase() } returns flowOf("EGP")

        viewModel = HomeViewModel(
            getAllBrandsUseCase,
            getAllCouponsUseCase,
            getAllProductsUseCase,
            getExchangeRateUseCase,
            getCurrencyUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchBrands success updates brands list`() = runTest {
        val mockBrands = listOf(
            Brand(1L, "Nike", null),
            Brand(2L, "Adidas", null)
        )
        coEvery { getAllBrandsUseCase() } returns mockBrands

        viewModel.fetchBrands()
        advanceUntilIdle()

        assertEquals(mockBrands, viewModel.brands.value)
    }

    @Test
    fun `fetchProductsByVendor success updates products list`() = runTest {
        val mockProducts = listOf(Product(1L, "Shoe", null, null))
        coEvery { getAllProductsUseCase("Nike") } returns mockProducts

        viewModel.fetchProductsByVendor("Nike")
        advanceUntilIdle()

        assertEquals(mockProducts, viewModel.products.value)
        assertEquals(mockProducts, viewModel.filteredProducts.value)
    }
}
