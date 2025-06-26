package com.example.khizana_user.data.dataSource.remote

import com.example.khizana_user.data.dataSource.remote.api.ShopifyDraftOrderService
import com.example.khizana_user.data.dto.draftorderDto.*
import com.example.khizana_user.data.repository.cart.CartRemoteDataSource
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
            val match = it.note == cartNote(customerId) &&
                    it.customer.id == customerId &&
                    it.completedAt == null
            match
        }
    }

    override suspend fun addToCart(customerId: Long, variantId: Long): Result<Unit> = try {
        val cartDraft = getCustomerCartDraft(customerId)
        val updatedItems = cartDraft?.lineItems?.toMutableList() ?: mutableListOf()

        val existingItem = updatedItems.find { it.variantId == variantId }
        if (existingItem != null) {
            existingItem.quantity += 1
        } else {
            updatedItems.add(DraftOrderItemDto(null, variantId, "Cart Item", 1))
        }

        val request = DraftOrderRequest(
            draftOrder = DraftOrderData(
                line_items = updatedItems.map { DraftOrderItem(it.variantId, it.quantity) },
                customer = CustomerData(customerId),
                note = cartNote(customerId),
                email = cartDraft?.email,
                shipping_address = cartDraft?.shippingAddress,
                applied_discount = cartDraft?.appliedDiscount,
                use_customer_default_address = false
            )
        )

        val response = if (cartDraft != null) {
            service.updateDraftOrder(cartDraft.id, request)
        } else {
            service.createDraftOrder(request)
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun decrementFromCart(customerId: Long, variantId: Long): Result<Unit> {
        return try {
            val cartDraft = getCustomerCartDraft(customerId)
                ?: return Result.failure(Exception("Cart draft not found"))

            // Find the item to decrement
            val itemToUpdate = cartDraft.lineItems.find { it.variantId == variantId }
                ?: return Result.failure(Exception("Item not found in cart"))

            // If quantity is already 1, return success without making any changes
            if (itemToUpdate.quantity == 1) {
                return Result.success(Unit)
            }

            // Otherwise, decrement the quantity
            val updatedItems = cartDraft.lineItems.map {
                if (it.variantId == variantId) {
                    it.copy(quantity = it.quantity - 1)
                } else {
                    it
                }
            }

            val request = DraftOrderRequest(
                draftOrder = DraftOrderData(
                    line_items = updatedItems.map { DraftOrderItem(it.variantId, it.quantity) },
                    customer = CustomerData(customerId),
                    note = cartNote(customerId),
                    email = cartDraft.email,
                    shipping_address = cartDraft.shippingAddress,
                    applied_discount = cartDraft.appliedDiscount,
                    use_customer_default_address = false
                )
            )

            val response = service.updateDraftOrder(cartDraft.id, request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromCart(customerId: Long, variantId: Long): Result<Unit> {
        return try {
            val cartDraft = getCustomerCartDraft(customerId)
                ?: return Result.failure(Exception("Cart draft not found"))

            val updatedItems = cartDraft.lineItems.filterNot { it.variantId == variantId }

            if (updatedItems.isEmpty()) {
                val deleteResponse = service.deleteDraftOrder(cartDraft.id)
                return if (deleteResponse.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to delete empty cart"))
                }
            }

            val request = DraftOrderRequest(
                draftOrder = DraftOrderData(
                    line_items = updatedItems.map { DraftOrderItem(it.variantId, it.quantity) },
                    customer = CustomerData(customerId),
                    note = cartNote(customerId),
                    email = cartDraft.email,
                    shipping_address = cartDraft.shippingAddress,
                    applied_discount = cartDraft.appliedDiscount,
                    use_customer_default_address = false
                )
            )

            val response = service.updateDraftOrder(cartDraft.id, request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to remove item from cart"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCart(customerId: Long): FavoriteList {
        val cartDraft = getCustomerCartDraft(customerId)
            ?: return FavoriteList(
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

    override suspend fun clearCart(customerId: Long): Result<Unit> {
        return try {
            val cartDraft = getCustomerCartDraft(customerId)
                ?: return Result.success(Unit)

            val response = service.deleteDraftOrder(cartDraft.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun resolveImageUrl(variantId: Long): String {
        return try {
            val variant = service.getVariantById(variantId).body()?.variant
            val productId = variant?.product_id ?: return ""
            service.getProductImages(productId).body()?.images?.firstOrNull()?.src ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
