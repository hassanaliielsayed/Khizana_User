package com.example.khizana_user.data.repository.mapper

import com.example.khizana_user.data.dto.CurrencyData
import com.example.khizana_user.domain.model.CurrencyRate

fun Map<String, CurrencyData>.toDomain(): List<CurrencyRate> {
    return values.map { it.toDomain() }
}

fun CurrencyData.toDomain(): CurrencyRate {
    return CurrencyRate(
        code = this.code,
        rate = this.value
    )
}