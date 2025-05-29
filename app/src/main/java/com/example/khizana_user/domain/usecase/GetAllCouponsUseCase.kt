package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.repository.HomeRepository
import javax.inject.Inject


class GetAllCouponsUseCase @Inject constructor(private val repository: HomeRepository) {

    suspend operator fun invoke() = repository.getCoupons()

}