package com.example.khizana_user.domain.usecase.sharedperference

import com.example.khizana_user.domain.repository.CustomerPreferencesRepository
import javax.inject.Inject

class SaveAddressUseCase @Inject constructor(
    private val repository: CustomerPreferencesRepository
) {
    suspend operator fun invoke(governorate: String, city: String) = repository.saveAddress(governorate, city)

}