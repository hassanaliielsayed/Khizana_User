package com.example.khizana_user.data.dataSource.remote

import android.util.Log
import com.example.khizana_user.data.dataSource.remote.api.ShopifyDraftOrderService
import com.example.khizana_user.data.dto.draftorderDto.*
import com.example.khizana_user.data.repository.WishlistRemoteDataSource
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.domain.model.FavoriteList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class WishlistRemoteDataSourceImpl @Inject constructor(
    private val service: ShopifyDraftOrderService
) : WishlistRemoteDataSource {

    private val TAG = "WishlistRemote"

    override suspend fun addToFavorites(customerId: Long, variantId: Long): Result<Unit> {
        return try {
            Log.d(TAG, "Fetching draft orders for customer $customerId")
            val draftOrders = service.getDraftOrders(customerId).body()?.draftOrders.orEmpty()
            val favoriteDraft = draftOrders.find { it.note == "FAVORITES" }

            if (favoriteDraft != null) {
                Log.d(TAG, "FAVORITES draft found: ${favoriteDraft.id}")
                val updatedItems = favoriteDraft.lineItems.toMutableList()
                if (updatedItems.none { it.variantId == variantId }) {
                    Log.d(TAG, "Adding variantId $variantId to FAVORITES draft")
                    updatedItems.add(DraftOrderItemDto(null, variantId, "Fav Item", 1))
                } else {
                    Log.d(TAG, "VariantId $variantId already exists in FAVORITES")
                }

                val request = DraftOrderRequest(
                    draft_order = DraftOrderData(
                        line_items = updatedItems.map { DraftOrderItem(it.variantId, it.quantity) },
                        customer = CustomerData(customerId),
                        note = "FAVORITES"
                    )
                )

                val response = service.updateDraftOrder(favoriteDraft.id, request)
                Log.d(TAG, "Update draft response: ${response.code()} ${response.message()}")
            } else {
                Log.d(TAG, "No FAVORITES draft found. Creating new with variantId $variantId")

                val request = DraftOrderRequest(
                    draft_order = DraftOrderData(
                        line_items = listOf(DraftOrderItem(variantId, 1)),
                        customer = CustomerData(customerId),
                        note = "FAVORITES"
                    )
                )

                val response = service.createDraftOrder(request)
                Log.d(TAG, "Create draft response: ${response.code()} ${response.message()}")
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Create draft failed. Code: ${response.code()}, Body: $errorBody")
                    return Result.failure(Exception("Shopify draft creation failed: $errorBody"))
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error in addToFavorites: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun removeFromFavorites(customerId: Long, variantId: Long): Result<Unit> = try {
        Log.d(TAG, "Removing variantId $variantId for customer $customerId")

        val draftOrders = service.getDraftOrders(customerId).body()?.draftOrders.orEmpty()
        val favoriteDraft = draftOrders.find { it.note == "FAVORITES" } ?: run {
            Log.d(TAG, "No FAVORITES draft found to remove from")
            return Result.success(Unit)
        }

        val updatedItems = favoriteDraft.lineItems.filter { it.variantId != variantId }

        val request = DraftOrderRequest(
            draft_order = DraftOrderData(
                line_items = updatedItems.map { DraftOrderItem(it.variantId, it.quantity) },
                customer = CustomerData(customerId),
                note = "FAVORITES"
            )
        )

        val response = service.updateDraftOrder(favoriteDraft.id, request)
        Log.d(TAG, "Remove from favorites response: ${response.code()} ${response.message()}")

        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error in removeFromFavorites: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun getFavorites(customerId: Long): Result<FavoriteList> {
        return try {
            Log.d(TAG, "Fetching favorites for customerId: $customerId")

            val draftOrders = service.getDraftOrders(customerId).body()?.draftOrders.orEmpty()
            val favoriteDraft = draftOrders.find { it.note == "FAVORITES" }
                ?: return Result.failure(Exception("No favorites found"))

            val enrichedItems = coroutineScope {
                favoriteDraft.lineItems.map { item ->
                    async {
                        val imageUrl = resolveImageUrl(item.variantId)
                        FavoriteItem(
                            variantId = item.variantId,
                            title = item.title,
                            quantity = item.quantity,
                            imageUrl = imageUrl
                        )
                    }
                }.awaitAll()
            }

            Log.d(TAG, "Loaded ${enrichedItems.size} favorite items")
            Result.success(
                FavoriteList(
                    draftOrderId = favoriteDraft.id,
                    customerId = favoriteDraft.customer.id,
                    items = enrichedItems
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error in getFavorites: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteFavoritesDraft(customerId: Long): Result<Unit> = try {
        Log.d(TAG, "Deleting FAVORITES draft for customerId: $customerId")

        val draftOrders = service.getDraftOrders(customerId).body()?.draftOrders.orEmpty()
        val favoriteDraft = draftOrders.find { it.note == "FAVORITES" } ?: run {
            Log.d(TAG, "No FAVORITES draft found to delete")
            return Result.success(Unit)
        }

        val response = service.deleteDraftOrder(favoriteDraft.id)
        Log.d(TAG, "Delete draft response: ${response.code()} ${response.message()}")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error in deleteFavoritesDraft: ${e.message}", e)
        Result.failure(e)
    }

    private suspend fun resolveImageUrl(variantId: Long): String = try {
        Log.d(TAG, "Resolving image for variantId: $variantId")

        val variantResponse = service.getVariantById(variantId).body()
        val productId = variantResponse?.variant?.product_id

        if (productId != null) {
            val imageUrl = service.getProductImages(productId).body()?.images?.firstOrNull()?.src
            Log.d(TAG, "Resolved image: $imageUrl")
            imageUrl ?: ""
        } else {
            Log.d(TAG, "Product ID not found for variantId: $variantId")
            ""
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to resolve image for variantId $variantId: ${e.message}", e)
        ""
    }
}
