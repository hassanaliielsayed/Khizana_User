package com.example.khizana_user.data.dataSource.remote

import com.example.khizana_user.data.dataSource.remote.api.ShopifyDraftOrderService
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderDto
import com.example.khizana_user.data.repository.OrderRemoteDataSource
import javax.inject.Inject

class OrderRemoteDataSourceImpl @Inject constructor(
    private val api: ShopifyDraftOrderService
) : OrderRemoteDataSource {

    override suspend fun completeDraftOrder(id: Long) {
        val res = api.completeDraftOrder(id)
        if (!res.isSuccessful) throw Exception("Complete failed")
    }

    override suspend fun getDraftOrder(id: Long): DraftOrderDto {
        val res = api.getDraftOrder(id)
        if (!res.isSuccessful) throw Exception("Fetch failed")
        return res.body()?.draftOrder ?: throw Exception("No draft found")
    }

    override suspend fun sendInvoice(id: Long) {
        val res = api.sendInvoice(id)
        if (!res.isSuccessful) throw Exception("Send invoice failed")
    }
}