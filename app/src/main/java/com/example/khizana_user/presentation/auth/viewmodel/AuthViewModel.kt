package com.example.khizana_user.presentation.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.usecase.*
import com.example.khizana_user.domain.usecase.sharedperfernceusecase.GetCustomerUseCase
import com.example.khizana_user.domain.usecase.sharedperfernceusecase.SaveCustomerUseCase
import com.example.khizana_user.utils.AuthState
import com.example.khizana_user.utils.ConnectionLiveData
import com.google.firebase.auth.FirebaseAuth
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
    getCustomerUseCase: GetCustomerUseCase,
    private val connectionLiveData: ConnectionLiveData
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _shopifyRegisterResult = MutableStateFlow<Result<Customer>?>(null)
    val shopifyRegisterResult: StateFlow<Result<Customer>?> = _shopifyRegisterResult

    private val _networkState = MutableStateFlow(true)
    val networkState: StateFlow<Boolean> = _networkState

    val currentCustomer: StateFlow<Customer?> = getCustomerUseCase()
        .onEach { Log.d("AuthViewModel", "currentCustomer loaded: $it") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        observeNetworkState()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null && currentUser.email != null) {
            Log.d("AuthViewModel", "App init: re-fetching Shopify customer for ${currentUser.email}")
            fetchShopifyCustomer(currentUser.email!!)
        }
    }

    private fun observeNetworkState() {
        viewModelScope.launch {
            connectionLiveData.asFlow().collect { isConnected ->
                _networkState.value = isConnected
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Logging in with Firebase: $email")
            _authState.value = AuthState.Loading
            val result = loginUseCase(email, password)
            if (result.isSuccess) {
                Log.d("AuthViewModel", "Firebase login success")
                fetchShopifyCustomer(email)
            } else {
                Log.e("AuthViewModel", "Firebase login failed: ${result.exceptionOrNull()?.message}")
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message)
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Registering Firebase user: $email")
            _authState.value = AuthState.Loading
            val result = registerUseCase(email, password)
            if (result.isSuccess) {
                Log.d("AuthViewModel", "Firebase registration success")
                registerWithShopify(name, email)
            } else {
                Log.e("AuthViewModel", "Firebase registration failed: ${result.exceptionOrNull()?.message}")
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message)
            }
        }
    }

    private fun registerWithShopify(name: String, email: String) {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Registering Shopify customer: $name <$email>")
            _shopifyRegisterResult.value = null
            val result = registerShopifyCustomerUseCase(name, email)
            _shopifyRegisterResult.value = result

            if (result.isSuccess) {
                val customer = result.getOrNull()
                Log.d("AuthViewModel", "Shopify registration success: $customer")
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
            Log.d("AuthViewModel", "Fetching Shopify customer by email: $email")
            val result = getShopifyCustomerByEmailUseCase(email)
            if (result.isSuccess) {
                val customer = result.getOrNull()
                if (customer != null) {
                    Log.d("AuthViewModel", "Shopify customer found: $customer")
                    saveCustomer(customer)
                    _authState.value = AuthState.Success
                } else {
                    Log.e("AuthViewModel", "No customer found in Shopify for $email")
                    _authState.value = AuthState.Error("Customer not found in Shopify")
                }
            } else {
                Log.e("AuthViewModel", "Shopify fetch failed: ${result.exceptionOrNull()?.message}")
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message)
            }
        }
    }

    fun saveCustomer(customer: Customer) {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Saving customer to DataStore: $customer")
            saveCustomerUseCase(customer)
        }
    }


    fun resetState() {
        Log.d("AuthViewModel", "Resetting auth state")
        _authState.value = AuthState.Idle
        _shopifyRegisterResult.value = null
    }
}
