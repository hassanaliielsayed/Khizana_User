package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dto.BrandResponseDto
import com.example.khizana_user.data.dto.ImageResponseDto
import com.example.khizana_user.data.dto.ProductDto
import com.example.khizana_user.data.dto.ProductImageDto
import com.example.khizana_user.data.dto.ProductVariantDto
import com.example.khizana_user.domain.repository.HomeRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.*
import kotlin.test.assertEquals


@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HomeRepositoryImpTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var repository: HomeRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        remoteDataSource = mockk()
        repository = HomeRepositoryImp(remoteDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllBrands returns mapped and distinct brands`() = runTest {
        val mockBrands = listOf(
            BrandResponseDto(1L, "Nike", ImageResponseDto("url1")),
            BrandResponseDto(2L, "Adidas", ImageResponseDto("url2")),
            BrandResponseDto(1L, "Nike", ImageResponseDto("url1"))
        )

        coEvery { remoteDataSource.fetchAllBrands() } returns mockBrands

        val result = repository.getAllBrands()

        assertEquals(2, result.size)

        val nike = result.first { it.id == 1L }
        assertEquals("Nike", nike.title)
        assertEquals("url1", nike.imageUrl)

        val adidas = result.first { it.id == 2L }
        assertEquals("Adidas", adidas.title)
        assertEquals("url2", adidas.imageUrl)
    }

    @Test
    fun `getAllBrands returns empty list when no brands`() = runTest {
        coEvery { remoteDataSource.fetchAllBrands() } returns emptyList()

        val result = repository.getAllBrands()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllProductsByBrand returns mapped and distinct products`() = runTest {
        val mockProducts = listOf(
            ProductDto(
                id = 1L,
                title = "Product1",
                body_html = "desc",
                vendor = "vendor",
                tags = "",
                product_type = null,
                variants = listOf(ProductVariantDto(11L, "100.0")),
                created_at = null,
                image = ProductImageDto(101L, "url1")
            ),
            ProductDto(
                id = 2L,
                title = "Product2",
                body_html = "desc",
                vendor = "vendor",
                tags = "",
                product_type = null,
                variants = listOf(ProductVariantDto(12L, "200.0")),
                created_at = null,
                image = ProductImageDto(102L, "url2")
            ),
            ProductDto(
                id = 1L,
                title = "Product1",
                body_html = "desc",
                vendor = "vendor",
                tags = "",
                product_type = null,
                variants = listOf(ProductVariantDto(11L, "100.0")),
                created_at = null,
                image = ProductImageDto(101L, "url1")
            )
        )

        coEvery { remoteDataSource.fetchAllProducts("vendor") } returns mockProducts

        val result = repository.getAllProductsByBrand("vendor")

        assertEquals(2, result.size)

        val product1 = result.first { it.id == 1L }
        assertEquals("Product1", product1.productTitle)
        assertEquals("url1", product1.productImage)
        assertEquals(11L, product1.variantId)

        val product2 = result.first { it.id == 2L }
        assertEquals("Product2", product2.productTitle)
        assertEquals("url2", product2.productImage)
        assertEquals(12L, product2.variantId)
    }

    @Test
    fun `getAllProductsByBrand returns empty list when no products`() = runTest {
        coEvery { remoteDataSource.fetchAllProducts("vendor") } returns emptyList()

        val result = repository.getAllProductsByBrand("vendor")

        assertTrue(result.isEmpty())
    }
}
