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
    ) : ViewModel() {

    private val _cartState = MutableStateFlow<Result<FavoriteList>>(Result.Loading)
    val cartState = _cartState.asStateFlow()

    private val _couponState = MutableStateFlow<Result<Coupon>>(Result.Loading)
    val couponState = _couponState.asStateFlow()


    fun loadCart(customerId: Long) {
        viewModelScope.launch{
            try {
                val cart = getCartUseCase(customerId) // Will now return empty cart if not found
                _cartState.value = Result.Success(cart)
            } catch (e: Exception) {
                _cartState.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun validateCoupon(code: String) {
        viewModelScope.launch {
            Log.i("aaaa", "validateCoupon: $code")
            try {
                val coupons = validateCouponUseCase(code)
                _couponState.value = Result.Success(coupons)
                Log.i("aaaa", "validateCoupon: + ${coupons.discount}")
            } catch (e: Exception) {
                _couponState.value = Result.Error(e.message ?: "Unknown error")
                Log.i("aaaa", "validateCoupon: ${e.message}")
            }
        }

    }

    fun addToCart(customerId: Long, variantId: Long) {
        viewModelScope.launch {
            Log.d("CartViewModel", "Adding variantId $variantId to cart")
            addToCartUseCase(customerId, variantId).onSuccess {
                loadCart(customerId)
            }.onFailure {
                Log.e("CartViewModel", "Add to cart failed: ${it.message}")
            }
        }
    }

    fun decrementFromCart(customerId: Long, variantId: Long) {
        viewModelScope.launch {
            Log.d("CartViewModel", "Decrementing variantId $variantId from cart")
            decrementFromCartUseCase(customerId, variantId).onSuccess {
                loadCart(customerId)
            }.onFailure {
                Log.e("CartViewModel", "Decrement failed: ${it.message}")
            }
        }
    }

    fun clearCart(customerId: Long) {
        viewModelScope.launch {
            Log.d("CartViewModel", "Clearing cart for customerId: $customerId")
            clearCartUseCase(customerId).onSuccess {
                loadCart(customerId)
            }.onFailure {
                Log.e("CartViewModel", "Clear cart failed: ${it.message}")
            }
        }
    }
}