package com.example.khizana_user.domain.usecase.authusecases

import com.example.khizana_user.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<Unit> {
        return repository.loginWithGoogle(idToken)
    }
}