package com.example.khizana_user.domain.usecase

import kotlinx.coroutines.Dispatchers
import com.example.khizana_user.domain.model.ProductDetails
import com.example.khizana_user.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetProductDetailsUseCaseTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: ProductRepository
    private lateinit var useCase: GetProductDetailsUseCase

    private val mockProduct = ProductDetails(
        id = 1L,
        variantId = 123L,
        title = "Mock Product",
        description = "Test Description",
        images = listOf("https://example.com/image.png"),
        sizes = listOf("S", "M"),
        colors = listOf("Black", "White"),
        price = "150.00",
        rating = 4.5f
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetProductDetailsUseCase(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `byProductId returns product from repository`() = runTest {
        coEvery { repository.getProductById(1L) } returns mockProduct

        val result = useCase.byProductId(1L)

        assertEquals(mockProduct, result)
    }

    @Test
    fun `byVariantId returns product from repository`() = runTest {
        coEvery { repository.getProductByVariantId(123L) } returns mockProduct

        val result = useCase.byVariantId(123L)

        assertEquals(mockProduct, result)
    }
}
