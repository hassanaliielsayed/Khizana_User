package com.example.khizana_user.data.dto.draftorderDto

import com.google.gson.annotations.SerializedName

data class DraftOrderRequest(
    @SerializedName("draft_order")
    val draftOrder: DraftOrderData
)

data class DraftOrderData(
    val line_items: List<DraftOrderItem>,
    val customer: CustomerData,
    val note: String = "FAVORITES",
    val email: String? = null,
    val shipping_address: ShippingAddressDto? = null,
    val applied_discount: AppliedDiscountDto? = null,
    val use_customer_default_address: Boolean = false
)

data class DraftOrderItem(
    @SerializedName("variant_id") val variantId: Long,
    val quantity: Int = 1
)

data class ShippingAddressDto(
    val address1: String,
    val city: String,
    val country: String,
    val zip: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val phone: String? = null
)

data class AppliedDiscountDto(
    val title: String,
    val value: String,
    @SerializedName("value_type") val valueType: String = "percentage",
    val amount: String
)


