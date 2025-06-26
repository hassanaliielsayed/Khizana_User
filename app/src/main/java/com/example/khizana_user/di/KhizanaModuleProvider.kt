package com.example.khizana_user.di

import android.content.Context
import com.example.khizana_user.data.dataSource.remote.api.KhizanaAPIService
import com.example.khizana_user.data.dataSource.remote.api.ShopifyDraftOrderService
import com.example.khizana_user.data.dataSource.remote.firebase.FirebaseAuthDataSourceImpl

import com.example.khizana_user.data.repository.HomeRepositoryImp
import com.example.khizana_user.data.repository.auth.AuthDataSource
import com.example.khizana_user.data.repository.auth.AuthRepositoryImp
import com.example.khizana_user.domain.repository.AuthRepository
import com.example.khizana_user.domain.repository.CartRepository
import com.example.khizana_user.domain.repository.CategoryRepository
import com.example.khizana_user.domain.repository.HomeRepository
import com.example.khizana_user.domain.repository.OrderRepository
import com.example.khizana_user.domain.repository.ProductRepository
import com.example.khizana_user.domain.repository.ShopifyRepository
import com.example.khizana_user.domain.repository.WishlistRepository
import com.example.khizana_user.domain.usecase.auth.GetShopifyCustomerByEmailUseCase
import com.example.khizana_user.domain.usecase.auth.LoginUseCase
import com.example.khizana_user.domain.usecase.auth.RegisterShopifyCustomerUseCase
import com.example.khizana_user.domain.usecase.auth.RegisterUseCase
import com.example.khizana_user.domain.usecase.cart.AddToCartUseCase
import com.example.khizana_user.domain.usecase.cart.ClearCartUseCase
import com.example.khizana_user.domain.usecase.cart.DecrementFromCartUseCase
import com.example.khizana_user.domain.usecase.cart.RemoveFromCartUseCase
import com.example.khizana_user.domain.usecase.cart.ValidateCouponUseCase
import com.example.khizana_user.domain.usecase.cartusecase.GetCartUseCase
import com.example.khizana_user.domain.usecase.category.GetAllProductsByCategoryUseCase
import com.example.khizana_user.domain.usecase.details.GetProductDetailsUseCase
import com.example.khizana_user.domain.usecase.favourite.AddToFavoritesUseCase
import com.example.khizana_user.domain.usecase.favourite.DeleteFavoritesUseCase
import com.example.khizana_user.domain.usecase.favourite.GetFavoritesUseCase
import com.example.khizana_user.domain.usecase.favourite.RemoveFromFavoritesUseCase
import com.example.khizana_user.domain.usecase.home.GetAllBrandsUseCase
import com.example.khizana_user.domain.usecase.home.GetAllProductsUseCase
import com.example.khizana_user.domain.usecase.order.CompleteDraftOrderUseCase
import com.example.khizana_user.domain.usecase.order.GetDraftOrderUseCase
import com.example.khizana_user.domain.usecase.order.GetOrderByIdUseCase
import com.example.khizana_user.domain.usecase.order.GetOrdersByCustomerIdUseCase
import com.example.khizana_user.domain.usecase.order.SendInvoiceUseCase
import com.example.khizana_user.utils.ConnectionLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


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
    @Singleton
    fun provideAuthDataSource(
        @ApplicationContext context: Context
    ): AuthDataSource = FirebaseAuthDataSourceImpl(context)

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

    @Provides
    fun provideAddToFavoritesUseCase(repo: WishlistRepository): AddToFavoritesUseCase =
        AddToFavoritesUseCase(repo)

    @Provides
    fun provideRemoveFromFavoritesUseCase(repo: WishlistRepository): RemoveFromFavoritesUseCase =
        RemoveFromFavoritesUseCase(repo)

    @Provides
    fun provideGetFavoritesUseCase(repo: WishlistRepository): GetFavoritesUseCase =
        GetFavoritesUseCase(repo)

    @Provides
    fun provideDeleteFavoritesUseCase(repo: WishlistRepository): DeleteFavoritesUseCase =
        DeleteFavoritesUseCase(repo)

    @Provides
    fun provideAddToCartUseCase(repository: CartRepository): AddToCartUseCase {
        return AddToCartUseCase(repository)
    }

    @Provides
    fun provideDecrementFromCartUseCase(repository: CartRepository): DecrementFromCartUseCase {
        return DecrementFromCartUseCase(repository)
    }

    @Provides
    fun provideGetCartUseCase(repository: CartRepository): GetCartUseCase {
        return GetCartUseCase(repository)
    }

    @Provides
    fun provideClearCartUseCase(repository: CartRepository): ClearCartUseCase {
        return ClearCartUseCase(repository)
    }

    @Provides
    fun provideValidateCouponUseCase(repository: CartRepository): ValidateCouponUseCase {
        return ValidateCouponUseCase(repository)
    }

    @Provides
    fun provideRemoveFromCartUseCase(repository: CartRepository): RemoveFromCartUseCase {
        return RemoveFromCartUseCase(repository)
    }

    @Provides
    fun provideCompleteDraftOrderUseCase(repo: OrderRepository): CompleteDraftOrderUseCase {
        return CompleteDraftOrderUseCase(repo)
    }

    @Provides
    fun provideGetDraftOrderUseCase(repo: OrderRepository): GetDraftOrderUseCase {
        return GetDraftOrderUseCase(repo)
    }

    @Provides
    fun provideSendInvoiceUseCase(repo: OrderRepository): SendInvoiceUseCase {
        return SendInvoiceUseCase(repo)
    }

    @Provides
    fun provideConnectionLiveData(
        @ApplicationContext context: Context
    ): ConnectionLiveData {
        return ConnectionLiveData(context)
    }

    @Provides
    fun provideDraftOrderService(
        @ShopifyApi retrofit: Retrofit
    ): ShopifyDraftOrderService {
        return retrofit.create(ShopifyDraftOrderService::class.java)
    }

    @Provides
    fun provideGetAllBrandsUseCase (repository: HomeRepositoryImp): GetAllBrandsUseCase {

        return GetAllBrandsUseCase(repository)

    }

    @Provides
    fun provideGetAllProductsByCategoryUseCase (repository: CategoryRepository): GetAllProductsByCategoryUseCase {

        return GetAllProductsByCategoryUseCase(repository)

    }

    @Provides
    fun provideGetAllProductsUseCase (repository: HomeRepository): GetAllProductsUseCase {

        return GetAllProductsUseCase(repository)

    }

    @Provides
    fun provideGetOrderByIdUseCase (repository: OrderRepository): GetOrderByIdUseCase {

        return GetOrderByIdUseCase(repository)

    }

    @Provides
    fun provideGetOrdersByCustomerIdUseCase(repository: OrderRepository): GetOrdersByCustomerIdUseCase {

        return GetOrdersByCustomerIdUseCase(repository)

    }

}