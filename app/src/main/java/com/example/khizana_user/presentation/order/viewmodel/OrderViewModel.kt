package com.example.khizana_user.presentation.order.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.data.dto.draftorderDto.AppliedDiscountDto
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderItem
import com.example.khizana_user.data.dto.draftorderDto.ShippingAddressDto
import com.example.khizana_user.domain.usecase.orderusecase.CompleteDraftOrderUseCase
import com.example.khizana_user.domain.usecase.orderusecase.GetDraftOrderUseCase
import com.example.khizana_user.domain.usecase.orderusecase.SendInvoiceUseCase
import com.example.khizana_user.domain.usecase.orderusecase.UpdateDraftOrderUseCase
import com.example.khizana_user.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val completeOrderUseCase: CompleteDraftOrderUseCase,
    private val getDraftOrderUseCase: GetDraftOrderUseCase,
    private val sendInvoiceUseCase: SendInvoiceUseCase,
    private val updateDraftOrderUseCase: UpdateDraftOrderUseCase
) : ViewModel() {

    private val _orderState = MutableStateFlow<Result<Unit>>(Result.Loading)
    val orderState: StateFlow<Result<Unit>> = _orderState

    private val _invoiceUrl = MutableStateFlow<Result<String>>(Result.Loading)
    val invoiceUrl: StateFlow<Result<String>> = _invoiceUrl

    fun completeCODOrder(draftOrderId: Long) {
        viewModelScope.launch {
            _orderState.value = Result.Loading
            try {
                Log.d("OrderVM", "Sending invoice for draft ID: $draftOrderId")
                //sendInvoiceUseCase(draftOrderId)

                Log.d("OrderVM", "Invoice sent. Completing order...")
                completeOrderUseCase(draftOrderId)

                _orderState.value = Result.Success(Unit)
                Log.d("OrderVM", "COD order completed successfully")
            } catch (e: Exception) {
                val error = e.message ?: "Order failed"
                _orderState.value = Result.Error(error)
                Log.e("OrderVM", "COD order failed: $error")
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
}
