package com.example.khizana_user.presentation.productdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.ProductDetails
import com.example.khizana_user.domain.usecase.details.GetProductDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val useCase: GetProductDetailsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<Result>(Result.Loading)
    val state = _state.asStateFlow()

    fun loadProduct(productId: Long) {
        viewModelScope.launch {
            _state.value = Result.Loading
            try {
                val product = useCase.byProductId(productId)
                _state.value = Result.Success(product)
            } catch (e: Exception) {
                _state.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadProductByVariant(variantId: Long) {
        viewModelScope.launch {
            _state.value = Result.Loading
            try {
                val product = useCase.byVariantId(variantId)
                _state.value = Result.Success(product)
            } catch (e: Exception) {
                _state.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class Result {
        object Loading : Result()
        data class Success(val data: ProductDetails) : Result()
        data class Error(val message: String) : Result()
    }
}
