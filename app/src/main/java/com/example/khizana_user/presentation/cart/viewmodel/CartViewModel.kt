package com.example.khizana_user.presentation.cart.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.model.FavoriteList
import com.example.khizana_user.domain.usecase.cartusecase.AddToCartUseCase
import com.example.khizana_user.domain.usecase.cartusecase.ClearCartUseCase
import com.example.khizana_user.domain.usecase.cartusecase.DecrementFromCartUseCase
import com.example.khizana_user.domain.usecase.cartusecase.GetCartUseCase
import com.example.khizana_user.domain.usecase.cartusecase.RemoveFromCartUseCase
import com.example.khizana_user.domain.usecase.cartusecase.ValidateCouponUseCase
import com.example.khizana_user.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val addToCartUseCase: AddToCartUseCase,
    private val decrementFromCartUseCase: DecrementFromCartUseCase,
    private val getCartUseCase: GetCartUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val validateCouponUseCase: ValidateCouponUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
) : ViewModel() {

    private val _cartState = MutableStateFlow<Result<FavoriteList>>(Result.Loading)
    val cartState = _cartState.asStateFlow()

    private val _couponState = MutableStateFlow<Result<Coupon>>(Result.Loading)
    val couponState = _couponState.asStateFlow()


    fun loadCart(customerId: Long) {
        viewModelScope.launch {
            try {
                val cart = getCartUseCase(customerId)
                _cartState.value = Result.Success(cart)
            } catch (e: Exception) {
                _cartState.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun validateCoupon(code: String) {
        viewModelScope.launch {
            try {
                val coupons = validateCouponUseCase(code)
                _couponState.value = Result.Success(coupons)
            } catch (e: Exception) {
                _couponState.value = Result.Error(e.message ?: "Unknown error")

            }
        }

    }

    fun addToCart(customerId: Long, variantId: Long) {
        viewModelScope.launch {
            addToCartUseCase(customerId, variantId).onSuccess {
                loadCart(customerId)
            }.onFailure {
                Log.e("CartViewModel", "Add to cart failed: ${it.message}")
            }
        }
    }

    fun decrementFromCart(customerId: Long, variantId: Long) {
        viewModelScope.launch {
            decrementFromCartUseCase(customerId, variantId).onSuccess {
                loadCart(customerId)
            }.onFailure {
                Log.e("CartViewModel", "Decrement failed: ${it.message}")
            }
        }
    }

    fun removeFromCart(customerId: Long, variantId: Long) {
        viewModelScope.launch {
            removeFromCartUseCase(customerId, variantId).onSuccess {
                loadCart(customerId)
            }.onFailure {
                Log.e("CartViewModel", "Decrement failed: ${it.message}")
            }
        }
    }

    fun clearCart(customerId: Long) {
        viewModelScope.launch {
            clearCartUseCase(customerId).onSuccess {
                loadCart(customerId)
            }.onFailure {
                Log.e("CartViewModel", "Clear cart failed: ${it.message}")
            }
        }
    }

}