package com.example.khizana_user.di

import com.example.khizana_user.data.dataSource.remote.RemoteDataSourceImp
import com.example.khizana_user.data.repository.RemoteDataSource
import com.example.khizana_user.data.repository.HomeRepositoryImp
import com.example.khizana_user.data.repository.ProductRepositoryImp
import com.example.khizana_user.domain.repository.HomeRepository
import com.example.khizana_user.domain.repository.ProductRepository
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

    @Binds
    abstract fun bindProductRepository(repo: ProductRepositoryImp): ProductRepository





//    @Provides
//    fun provideGetAllBrandsUseCase(
//        repository: HomeRepositoryIn
//    ): GetAllBrandsUseCase {
//        return GetAllBrandsUseCase(repository)
//    }

}