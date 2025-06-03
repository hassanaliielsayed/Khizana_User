package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.ShopifyCustomerDto
import com.example.khizana_user.domain.model.Customer

fun ShopifyCustomerDto.toDomain(): Customer {
    return Customer(
        id = this.id,
        name = this.first_name ?: "No Name",
        email = this.email,
        isVerified = this.verified_email,
        currency = this.currency ?: "N/A"
    )
}
