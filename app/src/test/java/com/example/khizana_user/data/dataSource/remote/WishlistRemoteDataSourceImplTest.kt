package com.example.khizana_user.data.dataSource.remote

import com.example.khizana_user.data.dataSource.remote.api.ShopifyDraftOrderService
import com.example.khizana_user.data.dto.draftorderDto.*
import com.example.khizana_user.domain.model.FavoriteList
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody
import org.junit.*
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class WishlistRemoteDataSourceImplTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var service: ShopifyDraftOrderService
    private lateinit var dataSource: WishlistRemoteDataSourceImpl

    private val customerId = 123L
    private val variantId = 456L

    private val draftOrder = DraftOrderDto(
        id = 1L,
        note = "FAVORITES-$customerId",
        lineItems = listOf(
            DraftOrderItemDto(null, variantId, "Fav Item", 1)
        ),
        customer = CustomerData(customerId),
        email = null,
        shippingAddress = null,
        appliedDiscount = null,
        completedAt = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        service = mockk()
        dataSource = WishlistRemoteDataSourceImpl(service)

        coEvery { service.getDraftOrders() } returns Response.success(DraftOrderResponse(draftOrders = listOf(draftOrder)))
        coEvery { service.getVariantById(variantId) } returns Response.success(
            VariantResponse(
                variant = VariantData(id = variantId, product_id = 888L, price = "99.99")
            )
        )
        coEvery { service.getProductImages(888L) } returns Response.success(
            ProductImagesResponse(
                images = listOf(ProductImageDto(id = 1L, src = "https://example.com/image.jpg"))
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addToFavorites returns success`() = runTest {
        coEvery { service.updateDraftOrder(any(), any()) } returns Response.success(DraftOrderResponse())
        val result = dataSource.addToFavorites(customerId, variantId)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `removeFromFavorites returns success`() = runTest {
        coEvery { service.updateDraftOrder(any(), any()) } returns Response.success(DraftOrderResponse())
        val result = dataSource.removeFromFavorites(customerId, variantId)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `getFavorites returns enriched FavoriteList`() = runTest {
        val result = dataSource.getFavorites(customerId)
        assertTrue(result.isSuccess)
        val favoriteList = result.getOrNull()!!
        assertEquals(customerId, favoriteList.customerId)
        assertEquals(1, favoriteList.items.size)
        assertEquals(variantId, favoriteList.items[0]?.variantId)
        assertEquals(99.99, favoriteList.items[0]?.price)
        assertEquals("https://example.com/image.jpg", favoriteList.items[0]?.imageUrl)
    }

    @Test
    fun `deleteFavoritesDraft returns success`() = runTest {
        coEvery { service.deleteDraftOrder(any()) } returns Response.success(Unit)
        val result = dataSource.deleteFavoritesDraft(customerId)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteFavoritesDraft returns success when no draft`() = runTest {
        coEvery { service.getDraftOrders() } returns Response.success(DraftOrderResponse(draftOrders = emptyList()))
        val result = dataSource.deleteFavoritesDraft(customerId)
        assertTrue(result.isSuccess)
    }
}