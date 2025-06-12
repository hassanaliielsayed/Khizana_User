package com.example.khizana_user.presentation.order.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.usecase.orderusecase.CompleteDraftOrderUseCase
import com.example.khizana_user.domain.usecase.orderusecase.GetDraftOrderUseCase
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
) : ViewModel() {

    private val _orderState = MutableStateFlow<Result<Unit>>(Result.Loading)
    val orderState: StateFlow<Result<Unit>> = _orderState

    private val _invoiceUrl = MutableStateFlow<Result<String>>(Result.Loading)
    val invoiceUrl: StateFlow<Result<String>> = _invoiceUrl

    fun completeCODOrder(draftOrderId: Long) {
        viewModelScope.launch {
            _orderState.value = Result.Loading
            try {
                Log.d("OrderVM", "🛒 Starting COD order for draft ID: $draftOrderId")
                completeOrderUseCase(draftOrderId)
                _orderState.value = Result.Success(Unit)
                Log.d("OrderVM", "✅ COD order completed successfully")
            } catch (e: Exception) {
                val error = e.message ?: "Order failed"
                _orderState.value = Result.Error(error)
                Log.e("OrderVM", "❌ COD order failed: $error")
            }
        }
    }

    fun initiateOnlinePayment(draftOrderId: Long) {
        viewModelScope.launch {
            _invoiceUrl.value = Result.Loading
            try {
                Log.d("OrderVM", "💳 Fetching invoice URL for draft ID: $draftOrderId")
                val order = getDraftOrderUseCase(draftOrderId)
                val url = order.invoiceUrl ?: throw Exception("No invoice URL")
                _invoiceUrl.value = Result.Success(url)
                Log.d("OrderVM", "✅ Invoice URL fetched: $url")
            } catch (e: Exception) {
                val error = e.message ?: "Failed to get invoice"
                _invoiceUrl.value = Result.Error(error)
                Log.e("OrderVM", "❌ Failed to fetch invoice URL: $error")
            }
        }
    }
}