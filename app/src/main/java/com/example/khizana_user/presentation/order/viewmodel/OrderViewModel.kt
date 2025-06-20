package com.example.khizana_user.presentation.order.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.data.dto.draftorderDto.AppliedDiscountDto
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderItem
import com.example.khizana_user.data.dto.draftorderDto.ShippingAddressDto
import com.example.khizana_user.domain.model.Orders
import com.example.khizana_user.domain.usecase.order.CompleteDraftOrderUseCase
import com.example.khizana_user.domain.usecase.order.GetDraftOrderUseCase
import com.example.khizana_user.domain.usecase.order.GetOrderByIdUseCase
import com.example.khizana_user.domain.usecase.order.GetOrdersByCustomerIdUseCase
import com.example.khizana_user.domain.usecase.order.SendInvoiceUseCase
import com.example.khizana_user.domain.usecase.order.UpdateDraftOrderUseCase
import com.example.khizana_user.domain.usecase.order.getProductImageUseCase
import com.example.khizana_user.utils.ConnectionLiveData
import com.example.khizana_user.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val completeOrderUseCase: CompleteDraftOrderUseCase,
    private val getDraftOrderUseCase: GetDraftOrderUseCase,
    private val sendInvoiceUseCase: SendInvoiceUseCase,
    private val updateDraftOrderUseCase: UpdateDraftOrderUseCase,
    private val getOrdersByCustomerIdUseCase: GetOrdersByCustomerIdUseCase,
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val connectionLiveData: ConnectionLiveData,
    private val getProductImageUseCase: getProductImageUseCase
) : ViewModel() {

    private val _orderState = MutableStateFlow<Result<Unit>>(Result.Loading)
    val orderState: StateFlow<Result<Unit>> = _orderState

    private val _invoiceUrl = MutableStateFlow<Result<String>>(Result.Loading)
    val invoiceUrl: StateFlow<Result<String>> = _invoiceUrl

    private val _orders = MutableStateFlow<Result<List<Orders>>>(Result.Loading)
    val orders: StateFlow<Result<List<Orders>>> = _orders

    private val _orderDetails = MutableStateFlow<Result<Orders>>(Result.Loading)
    val orderDetails: StateFlow<Result<Orders>> = _orderDetails

    private val _productImages = MutableStateFlow<Map<Long, String>>(emptyMap())
    val productImages: StateFlow<Map<Long, String>> = _productImages


    fun completeCODOrder(draftOrderId: Long) {
        viewModelScope.launch {
            _orderState.value = Result.Loading
            try {
                Log.d("OrderVM", "Completing order (COD) with receipt...")
                completeOrderUseCase(draftOrderId)
                _orderState.value = Result.Success(Unit)
            } catch (e: Exception) {
                _orderState.value = Result.Error(e.message ?: "Order failed")
            }
        }
    }

    fun initiateOnlinePayment(draftOrderId: Long) {
        viewModelScope.launch {
            _invoiceUrl.value = Result.Loading
            try {
                Log.d("OrderVM", "Fetching invoice URL for draft ID: $draftOrderId")
                val order = getDraftOrderUseCase(draftOrderId)
                val url = order.invoiceUrl ?: throw Exception("No invoice URL")
                _invoiceUrl.value = Result.Success(url)
                Log.d("OrderVM", "Invoice URL fetched: $url")
            } catch (e: Exception) {
                val error = e.message ?: "Failed to get invoice"
                _invoiceUrl.value = Result.Error(error)
                Log.e("OrderVM", "Failed to fetch invoice URL: $error")
            }
        }
    }

    fun updateDraftOrderBeforeCheckout(
        draftOrderId: Long,
        customerId: Long,
        shippingAddress: ShippingAddressDto?,
        appliedDiscount: AppliedDiscountDto?,
        lineItems: List<DraftOrderItem>
    ) {
        viewModelScope.launch {
            try {
                Log.i("OrderVM", "Updating draft before checkout:")
                Log.i("OrderVM", "  draftOrderId: $draftOrderId")
                Log.i("OrderVM", "  customerId: $customerId")
                Log.i("OrderVM", "  shipping: $shippingAddress")
                Log.i("OrderVM", "  discount: $appliedDiscount")
                Log.i("OrderVM", "  lineItems: $lineItems")

                updateDraftOrderUseCase(
                    draftOrderId = draftOrderId,
                    customerId = customerId,
                    shippingAddress = shippingAddress,
                    appliedDiscount = appliedDiscount,
                    lineItems = lineItems
                )

                Log.i("OrderVM", "Draft updated successfully before checkout")
            } catch (e: Exception) {
                Log.e("OrderVM", "Failed to update draft before checkout: ${e.message}")
                _orderState.value = Result.Error(e.message ?: "Update draft failed")
            }
        }
    }

    fun fetchOrders(customerId: Long) {
        viewModelScope.launch {
            _orders.value = Result.Loading
            try {
                val orderList = getOrdersByCustomerIdUseCase(customerId)
                _orders.value = Result.Success(orderList)

            } catch (e: Exception) {
                val error = e.message ?: "Error fetching orders"
                _orders.value = Result.Error(error)
            }
        }
    }

    fun fetchOrderDetails(orderId: Long) {
        viewModelScope.launch {
            _orderDetails.value = Result.Loading
            try {
                val order = getOrderByIdUseCase(orderId)
                _orderDetails.value = Result.Success(order)
            } catch (e: Exception) {
                _orderDetails.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchProductImage(productId: Long) {
        viewModelScope.launch {
            try {
                val images = getProductImageUseCase(productId)

                val src = images.firstOrNull()?.src.orEmpty()
                _productImages.update { it + (productId to src) }
            } catch (e: Exception) {

            }
        }
    }
}
