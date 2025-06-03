package com.example.khizana_user.di

import com.example.khizana_user.data.dataSource.remote.api.KhizanaAPIService
import com.example.khizana_user.data.dataSource.remote.firebase.FirebaseAuthDataSourceImpl
import com.example.khizana_user.data.repository.AuthDataSource
import com.example.khizana_user.data.repository.AuthRepositoryImp
import com.example.khizana_user.domain.repository.AuthRepository
import com.example.khizana_user.domain.repository.ProductRepository
import com.example.khizana_user.domain.repository.ShopifyRepository
import com.example.khizana_user.domain.usecase.GetProductDetailsUseCase
import com.example.khizana_user.domain.usecase.GetShopifyCustomerByEmailUseCase
import com.example.khizana_user.domain.usecase.LoginUseCase
import com.example.khizana_user.domain.usecase.RegisterShopifyCustomerUseCase
import com.example.khizana_user.domain.usecase.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
class KhizanaModuleProvider {

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Shopify-Access-Token", "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")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    @ShopifyApi
    @Provides
    fun provideRetrofit (): Retrofit {
        return Retrofit.Builder().baseUrl("https://mad45-sv-and4.myshopify.com/admin/api/2025-04/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    fun provideKhizanaService (@ShopifyApi retrofit: Retrofit): KhizanaAPIService {
        return retrofit.create(KhizanaAPIService::class.java)
    }


    @Provides
    fun provideAuthDataSource(): AuthDataSource = FirebaseAuthDataSourceImpl()

    @Provides
    fun provideAuthRepository(authDataSource: AuthDataSource): AuthRepository =
        AuthRepositoryImp(authDataSource)

    @Provides
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase =
        LoginUseCase(repository)

    @Provides
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase =
        RegisterUseCase(repository)

    @Provides
    fun provideGetProductDetailsUseCase(repo: ProductRepository): GetProductDetailsUseCase =
        GetProductDetailsUseCase(repo)

    @Provides
    fun provideRegisterShopifyCustomerUseCase(
        repository: ShopifyRepository
    ): RegisterShopifyCustomerUseCase = RegisterShopifyCustomerUseCase(repository)

    @Provides
    fun provideGetShopifyCustomerByEmailUseCase(repo: ShopifyRepository): GetShopifyCustomerByEmailUseCase =
        GetShopifyCustomerByEmailUseCase(repo)


}