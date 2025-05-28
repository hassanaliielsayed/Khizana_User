package com.example.khizana_user

import com.example.khizana_user.data.repositoryImpl.AuthRepositoryImpl
import com.example.khizana_user.domain.repositoryInterfaces.AuthRepository
import com.example.khizana_user.domain.usecase.LoginUseCase
import com.example.khizana_user.domain.usecase.RegisterUseCase

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl()

    @Provides
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase =
        LoginUseCase(repository)

    @Provides
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase =
        RegisterUseCase(repository)
}
