@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.khizana_user.presentation.productdetails.viewmodel

import com.example.khizana_user.domain.model.ProductByCategory
import com.example.khizana_user.domain.usecase.GetAllProductsByCategoryUseCase
import com.example.khizana_user.presentation.category.viewModel.CategoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import io.mockk.coEvery
import io.mockk.mockk

class CategoryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var useCase: GetAllProductsByCategoryUseCase
    private lateinit var viewModel: CategoryViewModel

    private val mockProducts = listOf(
        ProductByCategory(
            id = 1L,
            productTitle = "Product One",
            productImage = "https://example.com/1.png",
            productVendor = "Vendor1",
            productTags = listOf("Men", "Sport"),
            product_type = "Shoes",
            productPrice = 100.0,
            variantId = 10L
        ),
        ProductByCategory(
            id = 2L,
            productTitle = "Product Two",
            productImage = "https://example.com/2.png",
            productVendor = "Vendor2",
            productTags = listOf("Women"),
            product_type = "Dress",
            productPrice = 200.0,
            variantId = 20L
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        useCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllProducts success updates products state`() = runTest {
        coEvery { useCase() } returns mockProducts

        viewModel = CategoryViewModel(useCase)
        advanceUntilIdle()

        val result = viewModel.products.value
        assertEquals(2, result.size)
        assertEquals("Product One", result[0].productTitle)
    }

    @Test
    fun `getAllProducts failure updates error state`() = runTest {
        coEvery { useCase() } throws Exception("Failed to fetch products")

        viewModel = CategoryViewModel(useCase)
        advanceUntilIdle()

        val error = viewModel.error.value
        assertEquals("Failed to fetch products", error)
    }

    @Test
    fun `filterProductsByTag filters correctly`() = runTest {
        coEvery { useCase() } returns mockProducts

        viewModel = CategoryViewModel(useCase)
        advanceUntilIdle()

        viewModel.filterProductsByTag("Men")
        val result = viewModel.products.value
        assertEquals(1, result.size)
        assertEquals("Product One", result[0].productTitle)
    }

    @Test
    fun `filterProductsBySubCategory filters correctly`() = runTest {
        coEvery { useCase() } returns mockProducts

        viewModel = CategoryViewModel(useCase)
        advanceUntilIdle()

        viewModel.filterProductsBySubCategory("Shoes")
        val result = viewModel.products.value
        assertEquals(1, result.size)
        assertEquals("Shoes", result[0].product_type)
    }

    @Test
    fun `filterProductsByPrice filters correctly`() = runTest {
        coEvery { useCase() } returns mockProducts

        viewModel = CategoryViewModel(useCase)
        advanceUntilIdle()

        viewModel.filterProductsByPrice(150f)
        val result = viewModel.products.value
        assertEquals(1, result.size)
        assertTrue(result.all { it.productPrice <= 150.0 })
    }

    @Test
    fun `filterProductsBySearch filters correctly`() = runTest {
        coEvery { useCase() } returns mockProducts

        viewModel = CategoryViewModel(useCase)
        advanceUntilIdle()

        viewModel.filterProductsBySearch("Two")
        val result = viewModel.products.value
        assertEquals(1, result.size)
        assertEquals("Product Two", result[0].productTitle)
    }
}
