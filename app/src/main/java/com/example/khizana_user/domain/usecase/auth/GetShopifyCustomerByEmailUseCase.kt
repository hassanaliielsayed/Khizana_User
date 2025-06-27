package com.example.khizana_user.domain.usecase.auth

import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.repository.ShopifyRepository

class GetShopifyCustomerByEmailUseCase(
    private val repo: ShopifyRepository
) {
    suspend operator fun invoke(email: String): Result<Customer?> {
        return repo.searchCustomerByEmail(email)
    }
}
