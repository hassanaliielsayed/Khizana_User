package com.example.khizana_user.di

import android.content.Context
import com.example.khizana_user.data.dataSource.local.CustomerPreferencesDataSourceImpl
import com.example.khizana_user.data.dataSource.remote.CartRemoteDataSourceImpl
import com.example.khizana_user.data.dataSource.remote.OrderRemoteDataSourceImpl
import com.example.khizana_user.data.dataSource.remote.RemoteDataSourceImp
import com.example.khizana_user.data.repository.CategoryRepositoryImp
import com.example.khizana_user.data.dataSource.remote.WishlistRemoteDataSourceImpl
import com.example.khizana_user.data.repository.CartRemoteDataSource
import com.example.khizana_user.data.repository.CartRepositoryImpl
import com.example.khizana_user.data.repository.CustomerPreferencesDataSource
import com.example.khizana_user.data.repository.CustomerPreferencesRepositoryImpl
import com.example.khizana_user.data.repository.RemoteDataSource
import com.example.khizana_user.data.repository.HomeRepositoryImp
import com.example.khizana_user.data.repository.OrderRemoteDataSource
import com.example.khizana_user.data.repository.OrderRepositoryImpl
import com.example.khizana_user.data.repository.ProductRepositoryImp
import com.example.khizana_user.data.repository.SettingRepositoryImpl
import com.example.khizana_user.data.repository.ShopifyRepositoryImpl
import com.example.khizana_user.domain.repository.CategoryRepository
import com.example.khizana_user.data.repository.WishlistRemoteDataSource
import com.example.khizana_user.data.repository.WishlistRepositoryImpl
import com.example.khizana_user.domain.repository.CartRepository
import com.example.khizana_user.domain.repository.CustomerPreferencesRepository
import com.example.khizana_user.domain.repository.HomeRepository
import com.example.khizana_user.domain.repository.OrderRepository
import com.example.khizana_user.domain.repository.ProductRepository
import com.example.khizana_user.domain.repository.SettingRepository
import com.example.khizana_user.domain.repository.ShopifyRepository
import com.example.khizana_user.domain.repository.WishlistRepository
import com.example.khizana_user.utils.ConnectionLiveData
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Binds
    @Singleton
    abstract fun bindWishlistRepository(
        impl: WishlistRepositoryImpl
    ): WishlistRepository

    @Binds
    @Singleton
    abstract fun bindWishlistRemoteDataSource(
        impl: WishlistRemoteDataSourceImpl
    ): WishlistRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCartRemoteDataSource(
        impl: CartRemoteDataSourceImpl
    ): CartRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindOrderRemoteDataSource(
        impl: OrderRemoteDataSourceImpl

    ): OrderRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: OrderRepositoryImpl
    ): OrderRepository


}