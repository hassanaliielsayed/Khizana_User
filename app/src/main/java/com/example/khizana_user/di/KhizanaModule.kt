package com.example.khizana_user.di

import com.example.khizana_user.data.dataSource.remote.RemoteDataSourceImp
import com.example.khizana_user.data.repository.RemoteDataSource
import com.example.khizana_user.data.repository.HomeRepositoryImp
import com.example.khizana_user.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class KhizanaModule {

    @Binds
    abstract fun bindRemoteDataSource (remote: RemoteDataSourceImp): RemoteDataSource

    @Binds
    @Singleton
    abstract fun bindHomeRepository (repo: HomeRepositoryImp): HomeRepository




//    @Provides
//    fun provideGetAllBrandsUseCase(
//        repository: HomeRepositoryIn
//    ): GetAllBrandsUseCase {
//        return GetAllBrandsUseCase(repository)
//    }

}