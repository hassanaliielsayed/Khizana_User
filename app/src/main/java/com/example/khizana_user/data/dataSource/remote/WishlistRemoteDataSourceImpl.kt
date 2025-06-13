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
            val favoriteDraft = getCustomerFavoritesDraft(customerId)

            if (favoriteDraft != null) {
                Log.d(TAG, "Updating existing FAVORITES draft: ${favoriteDraft.id}")
                val updatedItems = favoriteDraft.lineItems.toMutableList()
                if (updatedItems.none { it.variantId == variantId }) {
                    updatedItems.add(DraftOrderItemDto(null, variantId, "Fav Item", 1))
                }

                val request = DraftOrderRequest(
                    draftOrder = DraftOrderData(
                        line_items = updatedItems.map { DraftOrderItem(it.variantId, it.quantity) },
                        customer = CustomerData(customerId),
                        note = favoritesNote(customerId)
                    )
                )

                service.updateDraftOrder(favoriteDraft.id, request)
            } else {
                Log.d(TAG, "Creating new FAVORITES draft for $customerId")
                val request = DraftOrderRequest(
                    draftOrder = DraftOrderData(
                        line_items = listOf(DraftOrderItem(variantId, 1)),
                        customer = CustomerData(customerId),
                        note = favoritesNote(customerId)
                    )
                )

                val response = service.createDraftOrder(request)
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Create draft failed: ${response.code()} $errorBody")
                    return Result.failure(Exception("Shopify draft creation failed: $errorBody"))
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error in addToFavorites: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun removeFromFavorites(customerId: Long, variantId: Long): Result<Unit> {
        return try {
            val favoriteDraft = getCustomerFavoritesDraft(customerId) ?: return Result.success(Unit)

            val updatedItems = favoriteDraft.lineItems.filter { it.variantId != variantId }

            val request = DraftOrderRequest(
                draftOrder = DraftOrderData(
                    line_items = updatedItems.map { DraftOrderItem(it.variantId, it.quantity) },
                    customer = CustomerData(customerId),
                    note = favoritesNote(customerId)
                )
            )

            service.updateDraftOrder(favoriteDraft.id, request)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error in removeFromFavorites: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getFavorites(customerId: Long): Result<FavoriteList> {
        return try {
            val favoriteDraft = getCustomerFavoritesDraft(customerId)
                ?: return Result.failure(Exception("No favorites found"))

            val enrichedItems = coroutineScope {
                favoriteDraft.lineItems.map { item ->
                    async {
                        val (imageUrl, price) = resolveProductDetails(item.variantId)
                        FavoriteItem(
                            variantId = item.variantId,
                            title = item.title,
                            quantity = item.quantity,
                            price = price,  // Now including the price
                            imageUrl = imageUrl
                        )
                    }
                }.awaitAll()
            }

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

    override suspend fun deleteFavoritesDraft(customerId: Long): Result<Unit> {
        return try {
            val favoriteDraft = getCustomerFavoritesDraft(customerId) ?: return Result.success(Unit)

            service.deleteDraftOrder(favoriteDraft.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error in deleteFavoritesDraft: ${e.message}", e)
            Result.failure(e)
        }
    }

    //  DRY: Helper to find a specific customer's favorites draft order
    private suspend fun getCustomerFavoritesDraft(customerId: Long): DraftOrderDto? {
        return try {
            val allDrafts = service.getDraftOrders().body()?.draftOrders.orEmpty()
            allDrafts.find {
                it.note == favoritesNote(customerId) && it.customer.id == customerId
            }.also {
                Log.d(TAG, "Fetched draft for $customerId: ${it?.id}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get customer draft: ${e.message}", e)
            null
        }
    }

    private fun favoritesNote(customerId: Long): String = "FAVORITES-$customerId"

    private suspend fun resolveProductDetails(variantId: Long): Pair<String, Double> = try {
        val variantResponse = service.getVariantById(variantId).body()
        val productId = variantResponse?.variant?.product_id
        val price = variantResponse?.variant?.price?.toDoubleOrNull() ?: 0.0

        val imageUrl = if (productId != null) {
            service.getProductImages(productId).body()?.images?.firstOrNull()?.src ?: ""
        } else ""

        Pair(imageUrl, price)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to resolve product details for variantId $variantId: ${e.message}", e)
        Pair("", 0.0)
    }
}
