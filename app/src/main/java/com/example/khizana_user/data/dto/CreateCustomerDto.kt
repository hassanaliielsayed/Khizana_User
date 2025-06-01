package com.example.khizana_user.data.dto

data class ShopifyCreateCustomerRequest(
    val customer: ShopifyCustomerData
)

data class ShopifyCustomerData(
    val first_name: String,
    val email: String,
    val verified_email: Boolean = true
)