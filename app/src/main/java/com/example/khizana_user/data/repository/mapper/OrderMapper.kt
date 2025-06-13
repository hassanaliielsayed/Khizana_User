package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.draftorderDto.DraftOrderDto
import com.example.khizana_user.domain.model.Order

fun DraftOrderDto.toOrder(): Order {
    return Order(
        id = this.id,
        invoiceUrl = this.invoiceUrl
    )
}