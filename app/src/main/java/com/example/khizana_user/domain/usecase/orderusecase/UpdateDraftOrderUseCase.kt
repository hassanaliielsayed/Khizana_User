package com.example.khizana_user.domain.usecase.orderusecase

import com.example.khizana_user.data.dto.draftorderDto.AppliedDiscountDto
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderItem
import com.example.khizana_user.data.dto.draftorderDto.ShippingAddressDto
import com.example.khizana_user.domain.repository.OrderRepository
import javax.inject.Inject

class UpdateDraftOrderUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(
        draftOrderId: Long,
        customerId: Long,
        shippingAddress: ShippingAddressDto?,
        appliedDiscount: AppliedDiscountDto?,
        lineItems: List<DraftOrderItem>
    ) {
        repository.updateDraftOrder(
            draftOrderId = draftOrderId,
            customerId = customerId,
            shippingAddress = shippingAddress,
            appliedDiscount = appliedDiscount,
            lineItems = lineItems
        )
    }
}
