@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.khizana_user.presentation.productdetails.viewmodel

import com.example.khizana_user.domain.model.ProductDetails
import com.example.khizana_user.domain.usecase.details.GetProductDetailsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import io.mockk.coEvery
import io.mockk.mockk

@ExperimentalCoroutinesApi
class ProductDetailsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var useCase: GetProductDetailsUseCase
    private lateinit var viewModel: ProductDetailsViewModel

    private val mockProduct = ProductDetails(
        id = 1L,
        variantId = 123L,
        title = "Mock Product",
        description = "Test Desc",
        images = listOf("https://example.com/image.png"),
        colors = listOf("black"),
        sizes = listOf("S"),
        price = "100.00",
        rating = 4.5f
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        useCase = mockk()
        viewModel = ProductDetailsViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProduct emits Success when useCase succeeds`() = runTest {
        coEvery { useCase.byProductId(1L) } returns mockProduct

        viewModel.loadProduct(1L)
        advanceUntilIdle()

        val result = viewModel.state.value
        assertTrue(result is ProductDetailsViewModel.Result.Success)
        assertEquals("Mock Product", (result as ProductDetailsViewModel.Result.Success).data.title)
    }

    @Test
    fun `loadProduct emits Error when useCase throws`() = runTest {
        coEvery { useCase.byProductId(1L) } throws Exception("Network failure")

        viewModel.loadProduct(1L)
        advanceUntilIdle()

        val result = viewModel.state.value
        assertTrue(result is ProductDetailsViewModel.Result.Error)
        assertEquals("Network failure", (result as ProductDetailsViewModel.Result.Error).message)
    }

    @Test
    fun `loadProductByVariant emits Success when useCase succeeds`() = runTest {
        coEvery { useCase.byVariantId(123L) } returns mockProduct

        viewModel.loadProductByVariant(123L)
        advanceUntilIdle()

        val result = viewModel.state.value
        assertTrue(result is ProductDetailsViewModel.Result.Success)
        assertEquals(123L, (result as ProductDetailsViewModel.Result.Success).data.variantId)
    }
}
