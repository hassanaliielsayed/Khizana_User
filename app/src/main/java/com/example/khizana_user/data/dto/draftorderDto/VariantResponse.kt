package com.example.khizana_user.data.dto.draftorderDto

data class VariantResponse(
    val variant: VariantData
)

data class VariantData(
    val id: Long,
    val product_id: Long
)
