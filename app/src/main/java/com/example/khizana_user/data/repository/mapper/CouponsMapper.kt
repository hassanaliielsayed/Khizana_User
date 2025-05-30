package com.example.khizana_user.data.repository.mapper


import com.example.khizana_user.R
import com.example.khizana_user.data.dto.CouponsResponseDto
import com.example.khizana_user.data.dto.Price_rulesItemEntity
import com.example.khizana_user.domain.model.Coupon

fun CouponsResponseDto.toDomain(): List<Coupon> {
    return this.price_rules.map { it.toDomain() }
}

fun Price_rulesItemEntity.toDomain(): Coupon {
    return Coupon(
        title = title,
        id = id,
        img = R.drawable.ad11
    )
}

