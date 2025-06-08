package com.example.khizana_user.data.dto.draftorderDto

data class DraftOrderRequest(
    val draft_order: DraftOrderData
)

data class DraftOrderData(
    val line_items: List<DraftOrderItem>,
    val customer: CustomerData,
    val note: String = "FAVORITES"
)

data class DraftOrderItem(
    val variant_id: Long,
    val quantity: Int = 1
)
