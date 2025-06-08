package com.example.khizana_user.domain.model

data class FavoriteItem(
    val variantId: Long,
    val title: String,
    val quantity: Int,
    val imageUrl: String
)


data class FavoriteList(
    val draftOrderId: Long,
    val customerId: Long,
    val items: List<FavoriteItem>
)