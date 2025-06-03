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
}
