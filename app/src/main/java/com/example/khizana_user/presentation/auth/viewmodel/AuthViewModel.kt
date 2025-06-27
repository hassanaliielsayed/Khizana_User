package com.example.khizana_user.presentation.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.usecase.auth.CheckEmailVerifiedUseCase
import com.example.khizana_user.domain.usecase.auth.GetCurrentUserEmailUseCase
import com.example.khizana_user.domain.usecase.auth.GetCurrentUserNameUseCase
import com.example.khizana_user.domain.usecase.auth.GetShopifyCustomerByEmailUseCase
import com.example.khizana_user.domain.usecase.auth.LoginAsGuestUseCase
import com.example.khizana_user.domain.usecase.auth.LoginUseCase
import com.example.khizana_user.domain.usecase.auth.LoginWithGoogleUseCase
import com.example.khizana_user.domain.usecase.auth.RegisterShopifyCustomerUseCase
import com.example.khizana_user.domain.usecase.auth.RegisterUseCase
import com.example.khizana_user.domain.usecase.auth.ResetPasswordUseCase
import com.example.khizana_user.domain.usecase.auth.SendEmailVerificationUseCase
import com.example.khizana_user.domain.usecase.sharedperference.GetCustomerUseCase
import com.example.khizana_user.domain.usecase.sharedperference.SaveCustomerUseCase
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
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val loginAsGuestUseCase: LoginAsGuestUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val checkEmailVerifiedUseCase: CheckEmailVerifiedUseCase,
    private val getCurrentUserEmailUseCase: GetCurrentUserEmailUseCase,
    private val getCurrentUserNameUseCase: GetCurrentUserNameUseCase,
    getCustomerUseCase: GetCustomerUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _shopifyRegisterResult = MutableStateFlow<Result<Customer>?>(null)
    val shopifyRegisterResult = _shopifyRegisterResult.asStateFlow()

    private val _resetPasswordState = MutableStateFlow<Result<Unit>?>(null)
    val resetPasswordState = _resetPasswordState.asStateFlow()

    private val _emailVerificationState = MutableStateFlow<Result<Unit>?>(null)
    val emailVerificationState = _emailVerificationState.asStateFlow()

    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified = _isEmailVerified.asStateFlow()

    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail = _currentUserEmail.asStateFlow()

    private var didRegisterShopify = false
    private var registeredUserName: String? = null

    val currentCustomer: StateFlow<Customer?> = getCustomerUseCase()
        .onEach { Log.d("AuthViewModel", "currentCustomer loaded: $it") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        observeVerificationAndRegister()
    }

    private fun observeVerificationAndRegister() {
        viewModelScope.launch {
            combine(_isEmailVerified, _currentUserEmail) { verified, email ->
                if (verified && !email.isNullOrBlank() && !didRegisterShopify) {
                    didRegisterShopify = true
                    registerWithShopify(registeredUserName ?: "Yousef", email)
                }
            }.collect()
        }

        viewModelScope.launch {
            val email = getCurrentUserEmailUseCase()
            if (!email.isNullOrBlank()) {
                fetchShopifyCustomer(email)
            }
        }
    }

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
                _currentUserEmail.value = email
                registeredUserName = name

                sendEmailVerificationUseCase().onSuccess {
                    _authState.value = AuthState.VerificationEmailSent
                }.onFailure {
                    _authState.value = AuthState.Error("Could not send verification email")
                }
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown")
            }
        }
    }

    fun registerWithShopify(name: String, email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = registerShopifyCustomerUseCase(name, email)
            result.onSuccess {
                saveCustomerUseCase(it)
                _authState.value = AuthState.Success
            }.onFailure {
                _authState.value = AuthState.Error("Shopify registration failed: ${it.message}")
            }
        }
    }

    fun fetchShopifyCustomer(email: String) {
        viewModelScope.launch {
            val result = getShopifyCustomerByEmailUseCase(email)
            result.onSuccess { customer ->
                if (customer != null) {
                    saveCustomerUseCase(customer)
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Customer not found in Shopify")
                }
            }.onFailure {
                _authState.value = AuthState.Error(it.message)
            }
        }
    }

    fun saveCustomer(customer: Customer) {
        viewModelScope.launch { saveCustomerUseCase(customer) }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
        _shopifyRegisterResult.value = null
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = loginWithGoogleUseCase(idToken)

            if (result.isSuccess) {
                val email = getCurrentUserEmailUseCase()
                val name = getCurrentUserNameUseCase() ?: "Guest"
                if (!email.isNullOrBlank()) {
                    getShopifyCustomerByEmailUseCase(email).onSuccess { customer ->
                        if (customer != null) {
                            saveCustomer(customer)
                            _authState.value = AuthState.Success
                        } else {
                            registerWithShopify(name, email)
                        }
                    }.onFailure {
                        _authState.value = AuthState.Error(it.message)
                    }
                } else {
                    _authState.value = AuthState.Error("Google sign-in failed: Missing email")
                }
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown login error")
            }
        }
    }

    fun loginAsGuest() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = loginAsGuestUseCase()
            if (result.isSuccess) {
                val guest = Customer(
                    id = -1L,
                    name = "Guest",
                    email = "guest@anonymous.com",
                    isVerified = false,
                    currency = "EGP"
                )
                saveCustomerUseCase(guest)
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message)
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPasswordState.value = runCatching {
                resetPasswordUseCase(email).getOrThrow()
            }
        }
    }

    fun checkEmailVerificationStatus(onVerified: (String) -> Unit) {
        viewModelScope.launch {
            val verified = checkEmailVerifiedUseCase()
            _isEmailVerified.value = verified

            if (verified) {
                val email = getCurrentUserEmailUseCase()
                if (!email.isNullOrEmpty()) {
                    onVerified(email)
                } else {
                    _authState.value = AuthState.Error("Email not found after verification.")
                }
            }
        }
    }

    fun reloadUser() {
        viewModelScope.launch {
            val verified = checkEmailVerifiedUseCase()
            val email = getCurrentUserEmailUseCase()
            _isEmailVerified.value = verified
            _currentUserEmail.value = email
            if (verified && !email.isNullOrBlank() && !didRegisterShopify) {
                didRegisterShopify = true
                val name = registeredUserName ?: "Yousef"
                registerWithShopify(name, email)
            }
        }
    }

    fun sendEmailVerification() {
        viewModelScope.launch {
            sendEmailVerificationUseCase()
        }
    }
}
