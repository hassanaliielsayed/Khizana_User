package com.example.khizana_user.utils


sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object VerificationEmailSent : AuthState()
    data class Error(val message: String?) : AuthState()
}

