package com.example.khizana_user.data.repository

import com.example.khizana_user.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return authDataSource.login(email, password)
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return authDataSource.register(email, password)
    }

    override suspend fun loginWithGoogle(idToken: String): Result<Unit> {
        return authDataSource.loginWithGoogle(idToken)
    }

    override suspend fun logout(): Result<Unit> {
        return authDataSource.logout()
    }

    override suspend fun loginAsGuest() = authDataSource.loginAsGuest()

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return authDataSource.sendPasswordResetEmail(email)
    }

    override suspend fun sendEmailVerification() = authDataSource.sendEmailVerification()

    override suspend fun isEmailVerified() = authDataSource.isEmailVerified()

    override fun getCurrentUserEmail(): String? {
        return authDataSource.getCurrentUserEmail()
    }

    override fun getCurrentUserName(): String? {
        return authDataSource.getCurrentUserName()
    }
}
