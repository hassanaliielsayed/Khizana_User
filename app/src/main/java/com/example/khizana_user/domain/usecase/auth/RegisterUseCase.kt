package com.example.khizana_user.domain.usecase.auth

import com.example.khizana_user.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return repository.register(email, password)
    }
}