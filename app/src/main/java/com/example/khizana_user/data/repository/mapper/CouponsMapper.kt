package com.example.khizana_user.data.repository.mapper


import com.example.khizana_user.R
import com.example.khizana_user.data.dto.CouponsResponseDto
import com.example.khizana_user.data.dto.Price_rulesItemEntity
import com.example.khizana_user.domain.model.Coupon

fun CouponsResponseDto.toDomain(): List<Coupon> {
    return this.price_rules.map { it.toDomain() }
}

fun Price_rulesItemEntity.toDomain(): Coupon {

    val discount = value
        .replace("%", "")
        .replace("-", "")      // Shopify might send discount as "-15.0"
        .trim()
        .toDoubleOrNull() ?: 0.0

    val images = listOf(
        R.drawable.add2,
        R.drawable.add1,
    )
    val randomImage = images.random()

    return Coupon(
        title = title,
        id = id,
        discount = discount,
        img = randomImage
    )
}

