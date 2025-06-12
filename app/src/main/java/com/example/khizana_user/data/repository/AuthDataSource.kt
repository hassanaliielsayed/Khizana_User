package com.example.khizana_user.data.repository

interface AuthDataSource {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun loginAsGuest(): Result<Unit>
    fun isAnonymousUser(): Boolean
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun isEmailVerified(): Boolean
    fun getCurrentUserEmail(): String?
    fun getCurrentUserName(): String?
}
