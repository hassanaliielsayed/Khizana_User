package com.example.khizana_user.data.dataSource.remote

import android.util.Log
import com.example.khizana_user.data.dataSource.remote.api.KhizanaAPIService
import com.example.khizana_user.data.dto.BrandResponseDto
import com.example.khizana_user.data.dto.CouponsResponseDto
import com.example.khizana_user.data.dto.ProductDetailsDto
import com.example.khizana_user.data.dto.ProductDto
import com.example.khizana_user.data.dto.ProductResponseDto
import com.example.khizana_user.data.dto.ShopifyCreateCustomerRequest
import com.example.khizana_user.data.dto.ShopifyCustomerCreatedResponse
import com.example.khizana_user.data.dto.ShopifyCustomerSearchResponseDto
import com.example.khizana_user.data.repository.RemoteDataSource
import retrofit2.Response
import javax.inject.Inject


class RemoteDataSourceImp @Inject constructor(private val apiService: KhizanaAPIService) : RemoteDataSource {

    override suspend fun fetchAllBrands():  List<BrandResponseDto> {

        val response = apiService.getAllBrands()
        val body = response.body()

        Log.d("AllBrandsDebug", "code: ${response.code()}")
        Log.d("AllBrandsDebug", "All Brands body: ${response.body()?.allBrands}")

        if (response.isSuccessful && body != null) {

            return body.allBrands

        } else {

            throw Exception("Error fetching brands: ${response.message()}")

        }
    }

    override suspend fun getCoupons() = apiService.getCoupons()

    override suspend fun fetchAllProducts(vendor: String): List<ProductDto>{

        val response = apiService.getAllProducts(vendor)
        val body = response.body()

        Log.d("AllProductsDebug", "code: ${response.code()}")
        Log.d("AllProductsDebug", "All Product body: ${response.body()?.products}")

        return if (response.isSuccessful && body != null) {

            body.products

        } else {

            throw Exception("Error fetching products: ${response.message()}")

        }
    }

    override suspend fun getProductById(id: Long): ProductDetailsDto {
        val response = apiService.getProductById(id)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.product
        } else {
            throw Exception("Error fetching product: ${response.message()}")
        }
    }

    override suspend fun registerShopifyCustomer(request: ShopifyCreateCustomerRequest): Response<ShopifyCustomerCreatedResponse> {
        return apiService.registerCustomer(request)
    }

    override suspend fun searchShopifyCustomerByEmail(query: String): Response<ShopifyCustomerSearchResponseDto> {
        return apiService.searchCustomerByEmail(query)
    }

}
