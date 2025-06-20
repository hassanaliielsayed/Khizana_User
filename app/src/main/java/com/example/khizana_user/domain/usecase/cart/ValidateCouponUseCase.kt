package com.example.khizana_user.domain.usecase.cart

import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.repository.CartRepository
import javax.inject.Inject

class ValidateCouponUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(code: String): Coupon {
        val coupons = repository.fetchCoupon(code) // Assuming this returns List<Coupon>
        
        val validCoupon = coupons.firstOrNull { it.title.equals(code, ignoreCase = true) }

        if (validCoupon == null) {
            throw IllegalArgumentException("Invalid coupon code")
        }

        return validCoupon
    }
}