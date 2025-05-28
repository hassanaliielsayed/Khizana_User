package com.example.khizana_user.di

import com.example.khizana_user.data.remote.HomeRemoteDataSourceImp
import com.example.khizana_user.data.repositoryImpl.HomeRemoteDataSourceIn
import com.example.khizana_user.data.repositoryImpl.HomeRepositoryImp
import com.example.khizana_user.domain.repositoryInterfaces.HomeRepositoryIn
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeKhizanaModule {

    @Binds
    abstract fun bindHomeRemoteDataSource (remote: HomeRemoteDataSourceImp): HomeRemoteDataSourceIn

    @Binds
    @Singleton
    abstract fun bindHomeRepository (repo: HomeRepositoryImp): HomeRepositoryIn

//    @Provides
//    fun provideGetAllBrandsUseCase(
//        repository: HomeRepositoryIn
//    ): GetAllBrandsUseCase {
//        return GetAllBrandsUseCase(repository)
//    }

}