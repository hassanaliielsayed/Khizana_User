package com.example.khizana_user.data.dto

data class ShopifyCustomerCreatedResponse(
    val customer: ShopifyCustomerDto
)

data class ShopifyCustomerSearchResponseDto(
    val customers: List<ShopifyCustomerDto>
)

data class ShopifyCustomerDto(
    val id: Long,
    val email: String,
    val first_name: String?,
    val last_name: String?,
    val verified_email: Boolean,
    val state: String?,
    val orders_count: Int,
    val total_spent: String,
    val currency: String?,
    val phone: String?,
    val admin_graphql_api_id: String?,
)