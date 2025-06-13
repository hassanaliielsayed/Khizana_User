package com.example.khizana_user.domain.usecase.orderusecase

import com.example.khizana_user.domain.repository.OrderRepository
import javax.inject.Inject

class SendInvoiceUseCase @Inject constructor(
    private val repo: OrderRepository
) {
    suspend operator fun invoke(id: Long) = repo.sendInvoice(id)
}