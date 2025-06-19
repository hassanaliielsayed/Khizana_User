package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.ProductByCategory
import com.example.khizana_user.domain.repository.CategoryRepository
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
class GetAllProductsByCategoryUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: CategoryRepository
    private lateinit var useCase: GetAllProductsByCategoryUseCase

    private val mockProducts = listOf(
        ProductByCategory(
            id = 1L,
            productTitle = "Product 1",
            productImage = "https://example.com/img1.png",
            productVendor = "Vendor 1",
            productTags = listOf("tag1", "tag2"),
            product_type = "Type1",
            productPrice = 100.0,
            variantId = 10L
        ),
        ProductByCategory(
            id = 2L,
            productTitle = "Product 2",
            productImage = "https://example.com/img2.png",
            productVendor = "Vendor 2",
            productTags = listOf("tag3"),
            product_type = "Type2",
            productPrice = 200.0,
            variantId = 20L
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetAllProductsByCategoryUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns product list from repository`() = runTest {
        coEvery { repository.getAllProducts() } returns mockProducts

        val result = useCase()

        assertEquals(2, result.size)
        assertEquals("Product 1", result[0].productTitle)
        assertEquals(100.0, result[0].productPrice, 0.01)
        assertEquals("Product 2", result[1].productTitle)
    }

    @Test
    fun `invoke returns empty list when repository returns empty`() = runTest {
        coEvery { repository.getAllProducts() } returns emptyList()

        val result = useCase()

        assertTrue(result.isEmpty())
    }
}
