package com.example.khizana_user.data.dto.draftorderDto

import com.google.gson.annotations.SerializedName

data class DraftOrderResponse(
    @SerializedName("draft_orders")
    val draftOrders: List<DraftOrderDto>? = null,

    @SerializedName("draft_order")
    val draftOrder: DraftOrderDto? = null
)

data class DraftOrderDto(
    val id: Long,
    val note: String?,
    @SerializedName("line_items")
    val lineItems: List<DraftOrderItemDto>,
    val customer: CustomerData
)

data class DraftOrderItemDto(
    val id: Long?,
    @SerializedName("variant_id")
    val variantId: Long,
    val title: String,
    val quantity: Int,
    val imageUrl: String? = null
)

data class CustomerData(
    val id: Long
)

