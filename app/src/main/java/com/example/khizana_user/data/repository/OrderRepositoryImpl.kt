package com.example.khizana_user.data.repository

import com.example.khizana_user.data.dto.draftorderDto.*
import com.example.khizana_user.data.repository.mapper.toDomain
import com.example.khizana_user.data.repository.mapper.toOrder
import com.example.khizana_user.domain.model.Order
import com.example.khizana_user.domain.model.Orders
import com.example.khizana_user.domain.repository.OrderRepository
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val remote: OrderRemoteDataSource
) : OrderRepository {

    override suspend fun completeDraftOrder(id: Long) = remote.completeDraftOrder(id)

    override suspend fun getDraftOrder(id: Long): Order =
        remote.getDraftOrder(id).toOrder()

    override suspend fun sendInvoice(id: Long) = remote.sendInvoice(id)

    override suspend fun updateDraftOrder(
        draftOrderId: Long,
        customerId: Long,
        shippingAddress: ShippingAddressDto?,
        appliedDiscount: AppliedDiscountDto?,
        lineItems: List<DraftOrderItem>
    ) {
        val draftOrder = DraftOrderRequest(
            draftOrder = DraftOrderData(
                line_items = lineItems,
                customer = CustomerData(customerId),
                note = "CART-$customerId",
                shipping_address = shippingAddress,
                applied_discount = appliedDiscount,
                use_customer_default_address = false
            )
        )

        val res = remote.updateDraftOrder(draftOrderId, draftOrder)
        if (!res.isSuccessful) {
            throw Exception("Update draft failed: ${res.code()}")
        }
    }

    override suspend fun getOrders(customerId: Long): List<Orders> {
        val dtos = remote.getOrdersByCustomerId(customerId)
        return dtos.map { it.toDomain() }
    }
}
