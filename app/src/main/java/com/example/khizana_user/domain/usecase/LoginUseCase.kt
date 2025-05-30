package com.example.khizana_user.domain.usecase

import com.example.khizana_user.domain.repositoryInterfaces.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return repository.login(email, password)
    }
}