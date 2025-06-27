@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.khizana_user.data.repository.cart

import com.example.khizana_user.data.dataSource.remote.CartRemoteDataSourceImpl
import com.example.khizana_user.data.dto.CouponsResponseDto
import com.example.khizana_user.data.dto.Price_rulesItemEntity
import com.example.khizana_user.data.dto.Prerequisite_to_entitlement_purchaseEntity
import com.example.khizana_user.data.dto.Prerequisite_to_entitlement_quantity_ratioEntity
import com.example.khizana_user.data.repository.RemoteDataSource
import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.domain.model.FavoriteList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import io.mockk.*
import org.junit.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CartRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var remote: CartRemoteDataSourceImpl
    private lateinit var remoteSource: RemoteDataSource
    private lateinit var repository: CartRepositoryImpl

    private val customerId = 123L
    private val variantId = 456L

    private val fakeCart = FavoriteList(
        draftOrderId = 1001L,
        customerId = customerId,
        items = listOf(
            FavoriteItem(
                variantId = 456L,
                title = "Product 1",
                quantity = 1,
                price = 99.99,
                imageUrl = "https://example.com/image.jpg"
            )
        )
    )

    private val fakeCouponDto = CouponsResponseDto(
        price_rules = listOf(
            Price_rulesItemEntity(
                title = "DISCOUNT10",
                id = "rule-10",
                value = "-10.0",
                value_type = "percentage",
                once_per_customer = false,
                usage_limit = "10",
                starts_at = "",
                created_at = "",
                prerequisite_customer_ids = emptyList(),
                entitled_collection_ids = emptyList(),
                updated_at = "",
                prerequisite_product_ids = emptyList(),
                prerequisite_shipping_price_range = "",
                entitled_country_ids = emptyList(),
                entitled_variant_ids = emptyList(),
                ends_at = "",
                prerequisite_subtotal_range = "",
                allocation_method = "",
                prerequisite_to_entitlement_quantity_ratio = Prerequisite_to_entitlement_quantity_ratioEntity(
                    0,
                    0
                ),
                prerequisite_quantity_range = "",
                allocation_limit = 1,
                target_type = "",
                entitled_product_ids = emptyList(),
                customer_segment_prerequisite_ids = emptyList(),
                customer_selection = "",
                prerequisite_variant_ids = emptyList(),
                admin_graphql_api_id = "",
                target_selection = "",
                prerequisite_to_entitlement_purchase = Prerequisite_to_entitlement_purchaseEntity("0"),
                prerequisite_collection_ids = emptyList()
            )
        )
    )

    private val expectedCoupon = Coupon(
        title = "DISCOUNT10",
        id = "rule-10",
        img = 0,
        discount = 10.0
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        remote = mockk()
        remoteSource = mockk()
        repository = CartRepositoryImpl(remote, remoteSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addToCart returns success`() = runTest {
        coEvery { remote.addToCart(customerId, variantId) } returns Result.success(Unit)

        val result = repository.addToCart(customerId, variantId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `decrementFromCart returns success`() = runTest {
        coEvery { remote.decrementFromCart(customerId, variantId) } returns Result.success(Unit)

        val result = repository.decrementFromCart(customerId, variantId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `removeFromCart returns success`() = runTest {
        coEvery { remote.removeFromCart(customerId, variantId) } returns Result.success(Unit)

        val result = repository.removeFromCart(customerId, variantId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `getCart returns expected FavoriteList`() = runTest {
        coEvery { remote.getCart(customerId) } returns fakeCart

        val result = repository.getCart(customerId)

        assertEquals(fakeCart, result)
    }

    @Test
    fun `clearCart returns success`() = runTest {
        coEvery { remote.clearCart(customerId) } returns Result.success(Unit)

        val result = repository.clearCart(customerId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `fetchCoupon maps dto to domain`() = runTest {
        coEvery { remoteSource.fetchCoupon("DISCOUNT10") } returns fakeCouponDto

        val result = repository.fetchCoupon("DISCOUNT10")

        assertEquals(1, result.size)
        assertEquals("DISCOUNT10", result[0].title)
        assertEquals("rule-10", result[0].id)
        assertEquals(10.0, result[0].discount, 0.001)
    }
}