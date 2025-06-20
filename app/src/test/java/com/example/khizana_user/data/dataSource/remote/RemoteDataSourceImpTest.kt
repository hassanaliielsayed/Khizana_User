@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.khizana_user.data.dataSource.remote

import com.example.khizana_user.data.dataSource.remote.api.CurrencyAPIService
import com.example.khizana_user.data.dataSource.remote.api.KhizanaAPIService
import com.example.khizana_user.data.dto.*
import com.example.khizana_user.data.dto.draftorderDto.VariantResponse
import com.example.khizana_user.data.dto.draftorderDto.VariantData
import com.example.khizana_user.data.repository.RemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RemoteDataSourceImpTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var khizanaAPIService: KhizanaAPIService
    private lateinit var currencyAPIService: CurrencyAPIService
    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        khizanaAPIService = mockk()
        currencyAPIService = mockk()
        remoteDataSource = RemoteDataSourceImp(khizanaAPIService, currencyAPIService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchAllBrands returns brand list on success`() = runTest {
        val mockBrands = listOf(BrandResponseDto(1L, "Nike", null))
        val brandsResponse = BrandsResponseDto(allBrands = mockBrands)

        coEvery { khizanaAPIService.getAllBrands() } returns Response.success(brandsResponse)

        val result = remoteDataSource.fetchAllBrands()

        assertEquals(1, result.size)
        assertEquals("Nike", result[0].title)
    }

    @Test(expected = Exception::class)
    fun `fetchAllBrands throws on failure`() = runTest {
        coEvery { khizanaAPIService.getAllBrands() } returns Response.error(
            500,
            mockk<ResponseBody>(relaxed = true)
        )

        remoteDataSource.fetchAllBrands()
    }

    @Test
    fun `getProductByVariantId returns resolved product`() = runTest {
        val variantId = 123L
        val productId = 456L

        val variantData = VariantData(id = variantId, product_id = productId, price = "199.99")
        val variantResponse = VariantResponse(variant = variantData)

        coEvery { khizanaAPIService.getVariantById(variantId) } returns Response.success(variantResponse)

        val productDetailsDto = ProductDetailsDto(
            id = productId,
            title = "Test Product",
            body_html = "desc",
            vendor = "vendor",
            variants = emptyList(),
            images = emptyList(),
            image = null,
            options = emptyList()
        )
        val productDetailsResponseDto = ProductDetailsResponseDto(product = productDetailsDto)

        coEvery { khizanaAPIService.getProductById(productId) } returns Response.success(productDetailsResponseDto)

        val result = remoteDataSource.getProductByVariantId(variantId)

        assertEquals(productId, result.id)
        assertEquals("Test Product", result.title)
    }

    @Test(expected = Exception::class)
    fun `getProductByVariantId throws if variant not found`() = runTest {
        coEvery { khizanaAPIService.getVariantById(any()) } returns Response.error(
            404,
            mockk<ResponseBody>(relaxed = true)
        )

        remoteDataSource.getProductByVariantId(123L)
    }

    @Test(expected = Exception::class)
    fun `getProductByVariantId throws if product not found`() = runTest {
        val variantId = 123L
        val productId = 456L

        val variantData = VariantData(id = variantId, product_id = productId, price = "199.99")
        val variantResponse = VariantResponse(variant = variantData)

        coEvery { khizanaAPIService.getVariantById(variantId) } returns Response.success(variantResponse)
        coEvery { khizanaAPIService.getProductById(productId) } returns Response.error(
            404,
            mockk<ResponseBody>(relaxed = true)
        )

        remoteDataSource.getProductByVariantId(variantId)
    }

    @Test
    fun `getCurrencyRate returns valid CurrencyResponseDto`() = runTest {
        val expectedCode = "EGP"
        val expectedRate = 30.5
        val currencyMap = mapOf(
            expectedCode to CurrencyData(code = expectedCode, value = expectedRate)
        )
        val currencyResponse = CurrencyResponseDto(data = currencyMap)

        coEvery { currencyAPIService.getLatestRates("USD", expectedCode) } returns currencyResponse

        val result = remoteDataSource.getCurrencyRate("USD", expectedCode)

        assertTrue(result.data.containsKey(expectedCode))
        val currencyData = result.data[expectedCode]!!
        assertEquals(expectedCode, currencyData.code)
        assertEquals(expectedRate, currencyData.value, 0.01)
    }

    @Test
    fun `fetchAllProducts with vendor returns product list on success`() = runTest {
        val mockProducts = listOf(
            ProductDto(1L, "Product1", "desc", "vendor", "tags", "type", emptyList(), null, null)
        )
        val productResponseDto = ProductResponseDto(products = mockProducts)

        coEvery { khizanaAPIService.getAllProducts("vendor") } returns Response.success(productResponseDto)

        val result = remoteDataSource.fetchAllProducts("vendor")

        assertEquals(1, result.size)
        assertEquals("Product1", result[0].title)
    }

    @Test(expected = Exception::class)
    fun `fetchAllProducts with vendor throws on failure`() = runTest {
        coEvery { khizanaAPIService.getAllProducts("vendor") } returns Response.error(
            500,
            mockk<ResponseBody>(relaxed = true)
        )

        remoteDataSource.fetchAllProducts("vendor")
    }

    @Test
    fun `fetchAllProducts returns product list on success`() = runTest {
        val mockProducts = listOf(
            ProductDto(2L, "Product2", "desc", "vendor", "tags", "type", emptyList(), null, null)
        )
        val productResponseDto = ProductResponseDto(products = mockProducts)

        coEvery { khizanaAPIService.getAllProducts() } returns Response.success(productResponseDto)

        val result = remoteDataSource.fetchAllProducts()

        assertEquals(1, result.size)
        assertEquals("Product2", result[0].title)
    }

    @Test(expected = Exception::class)
    fun `fetchAllProducts throws on failure`() = runTest {
        coEvery { khizanaAPIService.getAllProducts() } returns Response.error(
            500,
            mockk<ResponseBody>(relaxed = true)
        )

        remoteDataSource.fetchAllProducts()
    }

}
