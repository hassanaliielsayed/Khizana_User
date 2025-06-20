package com.example.khizana_user.domain.usecase.auth

import com.example.khizana_user.domain.repository.AuthRepository
import javax.inject.Inject

class SendEmailVerificationUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.sendEmailVerification()
    }
}
