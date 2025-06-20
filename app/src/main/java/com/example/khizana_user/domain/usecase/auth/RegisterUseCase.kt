package com.example.khizana_user.domain.usecase.auth

import com.example.khizana_user.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return repository.register(email, password)
    }
}