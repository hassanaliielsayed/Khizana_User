package com.example.khizana_user

import com.example.khizana_user.domain.repositoryInterfaces.AuthDataSource
import com.example.khizana_user.data.firebase.FirebaseAuthDataSourceImpl
import com.example.khizana_user.data.repositoryImpl.AuthRepositoryImpl
import com.example.khizana_user.domain.repositoryInterfaces.AuthRepository
import com.example.khizana_user.domain.usecase.LoginUseCase
import com.example.khizana_user.domain.usecase.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAuthDataSource(): AuthDataSource = FirebaseAuthDataSourceImpl()

    @Provides
    fun provideAuthRepository(authDataSource: AuthDataSource): AuthRepository =
        AuthRepositoryImpl(authDataSource)

    @Provides
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase =
        LoginUseCase(repository)

    @Provides
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase =
        RegisterUseCase(repository)
}

