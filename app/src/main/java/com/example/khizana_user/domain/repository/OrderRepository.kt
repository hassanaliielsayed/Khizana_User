package com.example.khizana_user.domain.repository

import com.example.khizana_user.data.dto.draftorderDto.AppliedDiscountDto
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderItem
import com.example.khizana_user.data.dto.draftorderDto.ShippingAddressDto
import com.example.khizana_user.domain.model.Order
import com.example.khizana_user.domain.model.Orders
import com.example.khizana_user.domain.model.ProductImage

interface OrderRepository {
    suspend fun completeDraftOrder(id: Long)
    suspend fun getDraftOrder(id: Long): Order
    suspend fun sendInvoice(id: Long)

    suspend fun updateDraftOrder(
        draftOrderId: Long,
        customerId: Long,
        shippingAddress: ShippingAddressDto?,
        appliedDiscount: AppliedDiscountDto?,
        lineItems: List<DraftOrderItem>
    )

    suspend fun getOrders(customerId: Long): List<Orders>

    suspend fun getOrder(orderId: Long): Orders

    suspend fun getProductImage(productId: Long): List<ProductImage>

}
