package com.example.khizana_user.presentation.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.usecase.*
import com.example.khizana_user.domain.usecase.authusecases.CheckEmailVerifiedUseCase
import com.example.khizana_user.domain.usecase.authusecases.GetCurrentUserEmailUseCase
import com.example.khizana_user.domain.usecase.authusecases.GetCurrentUserNameUseCase
import com.example.khizana_user.domain.usecase.authusecases.LoginAsGuestUseCase
import com.example.khizana_user.domain.usecase.authusecases.LoginUseCase
import com.example.khizana_user.domain.usecase.authusecases.LoginWithGoogleUseCase
import com.example.khizana_user.domain.usecase.authusecases.RegisterUseCase
import com.example.khizana_user.domain.usecase.authusecases.ResetPasswordUseCase
import com.example.khizana_user.domain.usecase.authusecases.SendEmailVerificationUseCase
import com.example.khizana_user.domain.usecase.sharedperfernceusecase.GetCustomerUseCase
import com.example.khizana_user.domain.usecase.sharedperfernceusecase.SaveCustomerUseCase
import com.example.khizana_user.utils.AuthState
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
    val getCurrentUserNameUseCase: GetCurrentUserNameUseCase,
    getCustomerUseCase: GetCustomerUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _shopifyRegisterResult = MutableStateFlow<Result<Customer>?>(null)
    val shopifyRegisterResult: StateFlow<Result<Customer>?> = _shopifyRegisterResult

    private val _resetPasswordState = MutableStateFlow<Result<Unit>?>(null)
    val resetPasswordState: StateFlow<Result<Unit>?> = _resetPasswordState

    private val _emailVerificationState = MutableStateFlow<Result<Unit>?>(null)
    val emailVerificationState: StateFlow<Result<Unit>?> = _emailVerificationState

    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified

    private val _currentUserEmail = MutableStateFlow<String?>(null)

    val currentUserEmail: StateFlow<String?> = _currentUserEmail
    private var didRegisterShopify = false

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
                    val name = getCurrentUserNameUseCase() ?: "User"
                    registerWithShopify(name, email)
                }
            }.collect()
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
            _authState.value = AuthState.Loading
            val result = registerUseCase(email, password)
            if (result.isSuccess) {
                _currentUserEmail.value = email
                sendEmailVerificationUseCase().onSuccess {
                    _authState.value = AuthState.VerificationEmailSent
                }.onFailure {
                    _authState.value = AuthState.Error("Could not send verification email")
                }
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message)
            }
        }
    }


    fun registerWithShopify(name: String, email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = registerShopifyCustomerUseCase(name, email)
            result.onSuccess { customer ->
                saveCustomerUseCase(customer)
                _authState.value = AuthState.Success
            }.onFailure {
                _authState.value = AuthState.Error("Shopify registration failed: ${it.message}")
            }
        }
    }

    fun fetchShopifyCustomer(email: String) {
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

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = loginWithGoogleUseCase(idToken)

            if (result.isSuccess) {
                val email = getCurrentUserEmailUseCase()
                val name = getCurrentUserNameUseCase() ?: "Guest"

                if (email != null) {
                    Log.d("AuthViewModel", "Google login success. Checking Shopify for $email")

                    val shopifyResult = getShopifyCustomerByEmailUseCase(email)

                    if (shopifyResult.isSuccess) {
                        val customer = shopifyResult.getOrNull()
                        if (customer != null) {
                            Log.d("AuthViewModel", "Shopify customer found: $customer")
                            saveCustomer(customer)
                            _authState.value = AuthState.Success
                        } else {
                            Log.d("AuthViewModel", "Shopify customer not found. Registering new customer.")
                            registerWithShopify(name, email)
                        }
                    } else {
                        val errorMsg = shopifyResult.exceptionOrNull()?.message ?: "Unknown Shopify error"
                        Log.e("AuthViewModel", "Shopify fetch failed: $errorMsg")
                        _authState.value = AuthState.Error(errorMsg)
                    }
                } else {
                    Log.e("AuthViewModel", "Google sign-in failed: Email is null")
                    _authState.value = AuthState.Error("Google sign-in failed: Missing email")
                }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Unknown login error"
                Log.e("AuthViewModel", "Google login failed: $errorMsg")
                _authState.value = AuthState.Error(errorMsg)
            }
        }
    }


    fun loginAsGuest() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = loginAsGuestUseCase()

            if (result.isSuccess) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user?.isAnonymous == true) {
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
                    _authState.value = AuthState.Error("Anonymous user creation failed.")
                }
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
            val isVerified = checkEmailVerifiedUseCase()
            _isEmailVerified.value = isVerified

            if (isVerified) {
                val email = getCurrentUserEmailUseCase()
                if (!email.isNullOrEmpty()) {
                    Log.d("AuthViewModel", "Email verified: $email")
                    onVerified(email)
                } else {
                    _authState.value = AuthState.Error("Email not found after verification.")
                }
            }
        }
    }

    fun reloadUser() {
        viewModelScope.launch {
            _isEmailVerified.value = checkEmailVerifiedUseCase()

            val verified = _isEmailVerified.value
            val email = _currentUserEmail.value

            if (verified && !email.isNullOrBlank() && !didRegisterShopify) {
                Log.d("AuthViewModel", "Detected email verification for $email, registering with Shopify...")
                didRegisterShopify = true
                val name = getCurrentUserNameUseCase() ?: "User"
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
