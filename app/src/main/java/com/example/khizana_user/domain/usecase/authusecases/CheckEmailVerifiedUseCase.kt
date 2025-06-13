package com.example.khizana_user.domain.usecase.authusecases

import com.example.khizana_user.domain.repository.AuthRepository
import javax.inject.Inject

class CheckEmailVerifiedUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(): Boolean = repo.isEmailVerified()
}