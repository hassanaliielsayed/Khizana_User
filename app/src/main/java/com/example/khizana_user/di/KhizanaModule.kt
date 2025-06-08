package com.example.khizana_user.di

import com.example.khizana_user.data.dataSource.local.CustomerPreferencesDataSourceImpl
import com.example.khizana_user.data.dataSource.remote.RemoteDataSourceImp
import com.example.khizana_user.data.repository.CategoryRepositoryImp
import com.example.khizana_user.data.repository.CustomerPreferencesDataSource
import com.example.khizana_user.data.repository.CustomerPreferencesRepositoryImpl
import com.example.khizana_user.data.repository.RemoteDataSource
import com.example.khizana_user.data.repository.HomeRepositoryImp
import com.example.khizana_user.data.repository.ProductRepositoryImp
import com.example.khizana_user.data.repository.SettingRepositoryImpl
import com.example.khizana_user.data.repository.ShopifyRepositoryImpl
import com.example.khizana_user.domain.repository.CategoryRepository
import com.example.khizana_user.domain.repository.CustomerPreferencesRepository
import com.example.khizana_user.domain.repository.HomeRepository
import com.example.khizana_user.domain.repository.ProductRepository
import com.example.khizana_user.domain.repository.SettingRepository
import com.example.khizana_user.domain.repository.ShopifyRepository
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

    @Binds
    abstract fun bindSettingRepository(repo: SettingRepositoryImpl): SettingRepository

    @Binds
    abstract fun bindShopifyRepository(
        impl: ShopifyRepositoryImpl
    ): ShopifyRepository


    @Binds
    @Singleton
    abstract fun bindCustomerPreferencesDataSource(
        impl: CustomerPreferencesDataSourceImpl
    ): CustomerPreferencesDataSource

    @Binds
    @Singleton
    abstract fun bindCustomerPreferencesRepository(
        impl: CustomerPreferencesRepositoryImpl
    ): CustomerPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository (repo: CategoryRepositoryImp): CategoryRepository



//    @Provides
//    fun provideGetAllBrandsUseCase(
//        repository: HomeRepositoryIn
//    ): GetAllBrandsUseCase {
//        return GetAllBrandsUseCase(repository)
//    }

}