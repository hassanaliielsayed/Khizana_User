package com.example.khizana_user.data.repository

interface AuthDataSource {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String): Result<Unit>
}
