package com.example.khizana_user.di

import com.example.khizana_user.data.api.KhizanaAPIService
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

    @Provides
    fun provideRetrofit (): Retrofit {
        return Retrofit.Builder().baseUrl("https://mad45-sv-and4.myshopify.com/admin/api/2025-04/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    fun provideKhizanaService (retrofit: Retrofit): KhizanaAPIService {
        return retrofit.create(KhizanaAPIService::class.java)
    }
}