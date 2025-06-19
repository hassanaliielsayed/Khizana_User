package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dto.ProductDetailsDto
import com.example.khizana_user.domain.model.ProductDetails
import com.example.khizana_user.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.*
import kotlin.test.assertEquals

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ProductRepositoryImpTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var repository: ProductRepository

    private val fakeDto = ProductDetailsDto(
        id = 1L,
        title = "Mock Product",
        body_html = "Test Desc",
        vendor = "Mock Vendor",
        variants = emptyList(),
        images = emptyList(),
        image = null,
        options = emptyList(),
    )

    private val expectedDomain = ProductDetails(
        id = 1L,
        variantId = null,
        title = "Mock Product",
        description = "Test Desc",
        images = emptyList(),
        sizes = emptyList(),
        colors = emptyList(),
        price = "0.00",
        rating = 0.0f
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        remoteDataSource = mockk()
        repository = ProductRepositoryImp(remoteDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getProductById returns mapped domain model`() = runTest {
        coEvery { remoteDataSource.getProductById(1L) } returns fakeDto

        val result = repository.getProductById(1L)

        assertEquals(expectedDomain.copy(rating = result.rating), result)
    }

    @Test
    fun `getProductByVariantId returns mapped domain model`() = runTest {
        coEvery { remoteDataSource.getProductByVariantId(123L) } returns fakeDto

        val result = repository.getProductByVariantId(123L)

        assertEquals(expectedDomain.copy(rating = result.rating), result)
    }
}
