package com.example.khizana_user.data.dto

data class CouponsResponseDto(
    val price_rules: List<Price_rulesItemEntity>
)

data class Price_rulesItemEntity(
    val value_type: String,
    val once_per_customer: Boolean,
    val usage_limit: String,
    val starts_at: String,
    val created_at: String,
    val prerequisite_customer_ids: List<String>,
    val title: String,
    val entitled_collection_ids: List<String>,
    val updated_at: String,
    val prerequisite_product_ids: List<String>,
    val prerequisite_shipping_price_range: String,
    val entitled_country_ids: List<String>,
    val entitled_variant_ids: List<String>,
    val id: String,
    val ends_at: String,
    val value: String,
    val prerequisite_subtotal_range: String,
    val allocation_method: String,
    val prerequisite_to_entitlement_quantity_ratio: Prerequisite_to_entitlement_quantity_ratioEntity,
    val prerequisite_quantity_range: String,
    val allocation_limit: Int,
    val target_type: String,
    val entitled_product_ids: List<String>,
    val customer_segment_prerequisite_ids: List<String>,
    val customer_selection: String,
    val prerequisite_variant_ids: List<String>,
    val admin_graphql_api_id: String,
    val target_selection: String,
    val prerequisite_to_entitlement_purchase: Prerequisite_to_entitlement_purchaseEntity,
    val prerequisite_collection_ids: List<String>
)

data class Prerequisite_to_entitlement_quantity_ratioEntity(
    val prerequisite_quantity: Int,
    val entitled_quantity: Int
)

data class Prerequisite_to_entitlement_purchaseEntity(
    val prerequisite_amount: String
)
