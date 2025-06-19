package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.Product
import com.example.khizana_user.domain.repository.HomeRepository
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
class GetAllProductsUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: HomeRepository
    private lateinit var useCase: GetAllProductsUseCase

    private val mockProducts = listOf(
        Product(
            id = 1L,
            productTitle = "Product 1",
            productImage = "https://example.com/img1.png",
            variantId = 10L
        ),
        Product(
            id = 2L,
            productTitle = "Product 2",
            productImage = "https://example.com/img2.png",
            variantId = 20L
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetAllProductsUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns product list from repository for given vendor`() = runTest {
        val vendor = "Nike"
        coEvery { repository.getAllProductsByBrand(vendor) } returns mockProducts

        val result = useCase(vendor)

        assertEquals(2, result.size)
        assertEquals("Product 1", result[0].productTitle)
        assertEquals("Product 2", result[1].productTitle)
    }

    @Test
    fun `invoke returns empty list when repository returns empty`() = runTest {
        val vendor = "Adidas"
        coEvery { repository.getAllProductsByBrand(vendor) } returns emptyList()

        val result = useCase(vendor)

        assertTrue(result.isEmpty())
    }
}
