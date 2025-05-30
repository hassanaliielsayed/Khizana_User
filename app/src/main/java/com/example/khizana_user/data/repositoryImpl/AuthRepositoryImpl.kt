package com.example.khizana_user.data.repositoryImpl

import com.example.khizana_user.domain.repositoryInterfaces.AuthDataSource
import com.example.khizana_user.domain.repositoryInterfaces.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return authDataSource.login(email, password)
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return authDataSource.register(email, password)
    }
}
