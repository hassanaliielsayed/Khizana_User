package com.example.khizana_user.domain.repository

import com.example.khizana_user.domain.model.Customer

interface ShopifyRepository {
    suspend fun registerCustomerInShopify(name: String, email: String): Result<Customer>

    suspend fun searchCustomerByEmail(email: String): Result<Customer?>

}