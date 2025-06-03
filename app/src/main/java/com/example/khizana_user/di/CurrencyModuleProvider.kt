package com.example.khizana_user.di

import com.example.khizana_user.data.dataSource.remote.api.CurrencyAPIService
import com.example.khizana_user.domain.repository.SettingRepository
import com.example.khizana_user.domain.usecase.GetExchangeRateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class CurrencyModuleProvider {

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", "cur_live_TZR969bx7lNqxW2aA0LibGtLS9CmvbsHdzrSuac3")
                .build()
            chain.proceed(request)
        }
        .build()

    @CurrencyApi
    @Singleton
    @Provides
    fun provideRetrofit (): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.currencyapi.com/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }


    @Provides
    fun provideCurrencyService (@CurrencyApi retrofit: Retrofit): CurrencyAPIService {
        return retrofit.create(CurrencyAPIService::class.java)
    }


    @Provides
    fun provideGetCurrencyRateUseCase(repo: SettingRepository): GetExchangeRateUseCase =
        GetExchangeRateUseCase(repo)







}