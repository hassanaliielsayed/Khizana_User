package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dto.ProductDto
import com.example.khizana_user.data.dto.ProductImageDto
import com.example.khizana_user.data.dto.ProductVariantDto
import com.example.khizana_user.domain.repository.CategoryRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.*
import kotlin.test.assertEquals

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CategoryRepositoryImpTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var repository: CategoryRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        remoteDataSource = mockk()
        repository = CategoryRepositoryImp(remoteDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllProducts returns mapped and distinct product list`() = runTest {

        val mockProductDtos = listOf(
            ProductDto(
                id = 1L,
                title = "Product1",
                body_html = "desc",
                vendor = "vendor1",
                tags = "tag1, tag2",
                product_type = "type1",
                variants = listOf(ProductVariantDto(11L, "100.0")),
                created_at = null,
                image = ProductImageDto(101L, "url1")
            ),
            ProductDto(
                id = 2L,
                title = "Product2",
                body_html = "desc",
                vendor = "vendor2",
                tags = "tag3, tag4",
                product_type = "type2",
                variants = listOf(ProductVariantDto(12L, "150.0")),
                created_at = null,
                image = ProductImageDto(102L, "url2")
            ),
            ProductDto(
                id = 1L,
                title = "Product1",
                body_html = "desc",
                vendor = "vendor1",
                tags = "tag1, tag2",
                product_type = "type1",
                variants = listOf(ProductVariantDto(11L, "100.0")),
                created_at = null,
                image = ProductImageDto(101L, "url1")
            )
        )

        coEvery { remoteDataSource.fetchAllProducts() } returns mockProductDtos

        val result = repository.getAllProducts()

        assertEquals(2, result.size)

        val product1 = result.first { it.id == 1L }
        assertEquals("Product1", product1.productTitle)
        assertEquals("url1", product1.productImage)
        assertEquals(listOf("tag1", "tag2"), product1.productTags)
        assertEquals(100.0, product1.productPrice, 0.01)

        val product2 = result.first { it.id == 2L }
        assertEquals("Product2", product2.productTitle)
        assertEquals("url2", product2.productImage)
        assertEquals(listOf("tag3", "tag4"), product2.productTags)
        assertEquals(150.0, product2.productPrice, 0.01)
    }

    @Test
    fun `getAllProducts returns empty list when no products`() = runTest {
        coEvery { remoteDataSource.fetchAllProducts() } returns emptyList()

        val result = repository.getAllProducts()

        assertTrue(result.isEmpty())
    }
}
