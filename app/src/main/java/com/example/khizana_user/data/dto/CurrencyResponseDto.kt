package com.example.khizana_user.data.dto

data class CurrencyResponseDto(
    val data: Map<String, CurrencyData>
)

data class CurrencyData(
    val code: String,
    val value: Double
)
