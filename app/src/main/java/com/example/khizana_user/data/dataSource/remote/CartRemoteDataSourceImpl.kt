package com.example.khizana_user.data.dataSource.remote

import android.util.Log
import com.example.khizana_user.data.dataSource.remote.api.ShopifyDraftOrderService
import com.example.khizana_user.data.dto.draftorderDto.*
import com.example.khizana_user.data.repository.CartRemoteDataSource
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.domain.model.FavoriteList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class CartRemoteDataSourceImpl @Inject constructor(
    private val service: ShopifyDraftOrderService
) : CartRemoteDataSource {
    private val TAG = "CartRemote"

    private fun cartNote(customerId: Long) = "CART-$customerId"

    private suspend fun getCustomerCartDraft(customerId: Long): DraftOrderDto? {
        val draftOrders = service.getDraftOrders().body()?.draftOrders.orEmpty()
        return draftOrders.find {
            it.note == cartNote(customerId) && it.customer.id == customerId
        }
    }

    override suspend fun addToCart(customerId: Long, variantId: Long): Result<Unit> = try {
        Log.d(TAG, "Adding to cart - customerId: $customerId, variantId: $variantId")

        val cartDraft = getCustomerCartDraft(customerId)
        val updatedItems = cartDraft?.lineItems?.toMutableList() ?: mutableListOf()

        val existingItem = updatedItems.find { it.variantId == variantId }
        if (existingItem != null) {
            existingItem.quantity += 1
        } else {
            updatedItems.add(DraftOrderItemDto(null, variantId, "Cart Item", 1))
        }

        val request = DraftOrderRequest(
            draft_order = DraftOrderData(
                line_items = updatedItems.map { DraftOrderItem(it.variantId, it.quantity) },
                customer = CustomerData(customerId),
                note = cartNote(customerId)
            )
        )

        val response = if (cartDraft != null) {
            service.updateDraftOrder(cartDraft.id, request)
        } else {
            service.createDraftOrder(request)
        }

        Log.d(TAG, "Cart updated. Response: ${response.code()}")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error in addToCart: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun decrementFromCart(customerId: Long, variantId: Long): Result<Unit> {
        return try {
            Log.d(TAG, "Decrementing cart - customerId: $customerId, variantId: $variantId")

            val cartDraft = getCustomerCartDraft(customerId)
                ?: return Result.failure(Exception("Cart draft not found"))

            val updatedItems = cartDraft.lineItems.mapNotNull {
                when {
                    it.variantId == variantId && it.quantity > 1 -> it.copy(quantity = it.quantity - 1)
                    it.variantId == variantId && it.quantity == 1 -> null // Remove if reaching 0
                    else -> it
                }
            }

            val request = DraftOrderRequest(
                draft_order = DraftOrderData(
                    line_items = updatedItems.map { DraftOrderItem(it.variantId, it.quantity) },
                    customer = CustomerData(customerId),
                    note = cartNote(customerId)
                )
            )

            val response = service.updateDraftOrder(cartDraft.id, request)
            Log.d(TAG, "Decrement response: ${response.code()}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error in decrementFromCart: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getCart(customerId: Long): FavoriteList {
        Log.d(TAG, "Getting cart for customerId: $customerId")
        val cartDraft = getCustomerCartDraft(customerId)
            ?: return FavoriteList( // Return empty cart if not found
                draftOrderId = -1,
                customerId = customerId,
                items = emptyList()
            )

        val enrichedItems = coroutineScope {
            cartDraft.lineItems.map { item ->
                async {
                    item.price?.let {
                        FavoriteItem(
                            variantId = item.variantId,
                            title = item.title,
                            quantity = item.quantity,
                            price = it.toDouble(),
                            imageUrl = resolveImageUrl(item.variantId)
                        )
                    }
                }
            }.awaitAll()
        }

        return FavoriteList(
            draftOrderId = cartDraft.id,
            customerId = cartDraft.customer.id,
            items = enrichedItems
        )
    }

//    override suspend fun getCart(customerId: Long): Result<FavoriteList> {
//        return try {
//            Log.d(TAG, "Getting cart for customerId: $customerId")
//            val cartDraft = getCustomerCartDraft(customerId)
//                ?: return Result.failure(Exception("Cart not found"))
//
//            val enrichedItems = coroutineScope {
//                cartDraft.lineItems.map { item ->
//                    async {
//                        val imageUrl = resolveImageUrl(item.variantId)
//                        FavoriteItem(
//                            variantId = item.variantId,
//                            title = item.title,
//                            quantity = item.quantity,
//                            imageUrl = imageUrl
//                        )
//                    }
//                }.awaitAll()
//            }
//
//            Result.success(
//                FavoriteList(
//                    draftOrderId = cartDraft.id,
//                    customerId = cartDraft.customer.id,
//                    items = enrichedItems
//                )
//            )
//        } catch (e: Exception) {
//            Log.e(TAG, "Error in getCart: ${e.message}", e)
//            Result.failure(e)
//        }
//    }

    override suspend fun clearCart(customerId: Long): Result<Unit> {
        return try {
            Log.d(TAG, "Clearing cart for customerId: $customerId")

            val cartDraft = getCustomerCartDraft(customerId)
                ?: return Result.success(Unit)

            val response = service.deleteDraftOrder(cartDraft.id)
            Log.d(TAG, "Delete cart response: ${response.code()}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error in clearCart: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun resolveImageUrl(variantId: Long): String {
        return try {
            val variant = service.getVariantById(variantId).body()?.variant
            val productId = variant?.product_id ?: return ""
            service.getProductImages(productId).body()?.images?.firstOrNull()?.src ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error resolving image for $variantId: ${e.message}", e)
            ""
        }
    }
}