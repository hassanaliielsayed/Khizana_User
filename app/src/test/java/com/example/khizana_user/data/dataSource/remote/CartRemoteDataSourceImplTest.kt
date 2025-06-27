package com.example.khizana_user.data.dataSource.remote



import com.example.khizana_user.data.dataSource.remote.api.ShopifyDraftOrderService
import com.example.khizana_user.data.dto.draftorderDto.*
import com.example.khizana_user.domain.model.FavoriteItem
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
class CartRemoteDataSourceImplTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var service: ShopifyDraftOrderService
    private lateinit var dataSource: CartRemoteDataSourceImpl

    private val customerId = 123L
    private val variantId = 456L

    private val sampleDraftOrder = DraftOrderDto(
        id = 1L,
        customer = CustomerData(id = customerId),
        lineItems = listOf(
            DraftOrderItemDto(
                id = 1L,
                variantId = variantId,
                title = "Sample Item",
                quantity = 1,
                price = "99.99"
            )
        ),
        email = "user@example.com",
        note = "CART-$customerId",
        appliedDiscount = null,
        shippingAddress = null,
        completedAt = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        service = mockk()
        dataSource = spyk(CartRemoteDataSourceImpl(service))

        coEvery { service.getDraftOrders() } returns Response.success(
            DraftOrderResponse(draftOrders = listOf(sampleDraftOrder))
        )
        coEvery { service.getVariantById(variantId) } returns Response.success(
            VariantResponse(variant = VariantData(variantId, product_id = 999L, price = "99.99"))
        )
        coEvery { service.getProductImages(999L) } returns Response.success(
            ProductImagesResponse(images = listOf(ProductImageDto(id = 1L, src = "https://image.jpg")))
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addToCart returns success`() = runTest {
        coEvery { service.updateDraftOrder(any(), any()) } returns Response.success(
            DraftOrderResponse(draftOrder = sampleDraftOrder)
        )

        val result = dataSource.addToCart(customerId, variantId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `decrementFromCart returns success when quantity more than 1`() = runTest {
        val item = sampleDraftOrder.copy(
            lineItems = listOf(sampleDraftOrder.lineItems[0].copy(quantity = 2))
        )
        coEvery { service.getDraftOrders() } returns Response.success(
            DraftOrderResponse(draftOrders = listOf(item))
        )
        coEvery { service.updateDraftOrder(any(), any()) } returns Response.success(
            DraftOrderResponse(draftOrder = item)
        )

        val result = dataSource.decrementFromCart(customerId, variantId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `removeFromCart deletes draft order when last item`() = runTest {
        val item = sampleDraftOrder.copy(
            lineItems = listOf(DraftOrderItemDto(null, variantId, "title", 1))
        )
        coEvery { service.getDraftOrders() } returns Response.success(
            DraftOrderResponse(draftOrders = listOf(item))
        )
        coEvery { service.deleteDraftOrder(any()) } returns Response.success(Unit)

        val result = dataSource.removeFromCart(customerId, variantId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `getCart returns correct FavoriteList`() = runTest {
        val result = dataSource.getCart(customerId)

        assertEquals(customerId, result.customerId)
        assertEquals(1, result.items.size)
        assertEquals(variantId, result.items[0]?.variantId)
    }

    @Test
    fun `clearCart returns success when draft exists`() = runTest {
        coEvery { service.deleteDraftOrder(any()) } returns Response.success(Unit)

        val result = dataSource.clearCart(customerId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `clearCart returns success when no draft exists`() = runTest {
        coEvery { service.getDraftOrders() } returns Response.success(
            DraftOrderResponse(draftOrders = emptyList())
        )

        val result = dataSource.clearCart(customerId)

        assertTrue(result.isSuccess)
    }
}