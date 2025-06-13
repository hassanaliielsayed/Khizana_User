package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.draftorderDto.DraftOrderDto
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.domain.model.FavoriteList

fun DraftOrderDto.toDomain(): FavoriteList {
    val nonNullItems = this.lineItems.mapNotNull { item ->
        val price = item.price?.toDoubleOrNull()
        if (price != null) {
            FavoriteItem(
                variantId = item.variantId,
                title = item.title,
                quantity = item.quantity,
                price = price,
                imageUrl = item.imageUrl ?: ""
            )
        } else null
    }

    return FavoriteList(
        draftOrderId = this.id,
        customerId = this.customer.id,
        items = nonNullItems
    )
}
