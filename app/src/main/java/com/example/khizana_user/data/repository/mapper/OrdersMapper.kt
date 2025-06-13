package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.OrderDto
import com.example.khizana_user.domain.model.Orders

fun OrderDto.toDomain() = Orders(
    id = id,
    email = email,
    createdAt = created_at,
    totalPrice = total_price,
    currency = currency,
    financialStatus = financial_status
)