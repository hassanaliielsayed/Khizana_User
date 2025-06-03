package com.example.khizana_user.presentation.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.usecase.*
import com.example.khizana_user.utils.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val registerShopifyCustomerUseCase: RegisterShopifyCustomerUseCase,
    private val getShopifyCustomerByEmailUseCase: GetShopifyCustomerByEmailUseCase,
    private val saveCustomerUseCase: SaveCustomerUseCase,
    getCustomerUseCase: GetCustomerUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _shopifyRegisterResult = MutableStateFlow<Result<Customer>?>(null)
    val shopifyRegisterResult: StateFlow<Result<Customer>?> = _shopifyRegisterResult

    val currentCustomer: StateFlow<Customer?> = getCustomerUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = loginUseCase(email, password)
            if (result.isSuccess) {
                fetchShopifyCustomer(email)
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message)
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = registerUseCase(email, password)
            if (result.isSuccess) {
                Log.d("AuthViewModel", "Registering to Shopify with: $name, $email")
                registerWithShopify(name, email)
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message)
            }
        }
    }

    private fun registerWithShopify(name: String, email: String) {
        viewModelScope.launch {
            _shopifyRegisterResult.value = null
            val result = registerShopifyCustomerUseCase(name, email)
            _shopifyRegisterResult.value = result

            if (result.isSuccess) {
                val customer = result.getOrNull()
                Log.d("AuthViewModel", "Customer registered successfully: $customer")
                customer?.let {
                    saveCustomer(it)
                    _authState.value = AuthState.Success
                }
            } else {
                Log.e("AuthViewModel", "Shopify registration failed: ${result.exceptionOrNull()?.message}")
                _authState.value = AuthState.Error("Shopify error: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    private fun fetchShopifyCustomer(email: String) {
        viewModelScope.launch {
            val result = getShopifyCustomerByEmailUseCase(email)
            if (result.isSuccess) {
                val customer = result.getOrNull()
                if (customer != null) {
                    saveCustomer(customer)
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Customer not found in Shopify")
                }
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message)
            }
        }
    }

    fun saveCustomer(customer: Customer) {
        viewModelScope.launch {
            saveCustomerUseCase(customer)
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
        _shopifyRegisterResult.value = null
    }
}
