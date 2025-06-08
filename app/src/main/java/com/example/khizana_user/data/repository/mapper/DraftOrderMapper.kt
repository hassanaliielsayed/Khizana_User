package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.draftorderDto.DraftOrderDto
import com.example.khizana_user.domain.model.FavoriteItem
import com.example.khizana_user.domain.model.FavoriteList

fun DraftOrderDto.toDomain(): FavoriteList {
    return FavoriteList(
        draftOrderId = this.id,
        customerId = this.customer.id,
        items = this.lineItems.map {
            FavoriteItem(
                variantId = it.variantId,
                title = it.title,
                quantity = it.quantity,
                imageUrl = it.imageUrl ?: ""
            )
        }
    )
}
