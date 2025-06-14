package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.OrderDto
import com.example.khizana_user.domain.model.OrderItem
import com.example.khizana_user.domain.model.Orders

fun OrderDto.toDomain() = Orders(
    id = id,
    email = email,
    createdAt = created_at,
    totalPrice = total_price,
    currency = currency,
    financialStatus = financial_status,
    items = line_items?.map {
        OrderItem(
            title = it.title,
            quantity = it.quantity,
            price = it.price,
            sku = it.sku,
            imageUrl = it.image_url
        )
    } ?: emptyList()
)
