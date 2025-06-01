package com.example.khizana_user.domain.usecase


import com.example.khizana_user.domain.model.ProductDetails
import com.example.khizana_user.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductDetailsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: Long): ProductDetails {
        return repository.getProductById(id)
    }
}
