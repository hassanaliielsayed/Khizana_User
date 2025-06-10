package com.example.khizana_user.domain.usecase.cartusecase

import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.repository.CartRepository
import javax.inject.Inject

class ValidateCouponUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(code: String): Coupon {
        val coupon = repository.fetchCoupon(code)[0]
        if (coupon.title != code) {
            throw IllegalArgumentException("Invalid coupon code")
        }
        return coupon
    }

}