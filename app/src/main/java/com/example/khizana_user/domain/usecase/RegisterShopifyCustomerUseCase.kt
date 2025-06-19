package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.repository.ShopifyRepository

class RegisterShopifyCustomerUseCase(
    private val repository: ShopifyRepository
) {
    suspend operator fun invoke(name: String, email: String): Result<Customer> {
        return repository.registerCustomerInShopify(name, email)
    }
}
