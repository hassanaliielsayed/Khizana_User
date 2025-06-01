package com.example.khizana_user.data.repository

import android.util.Log
import com.example.khizana_user.data.dto.ShopifyCreateCustomerRequest
import com.example.khizana_user.data.dto.ShopifyCustomerData
import com.example.khizana_user.data.repository.mapper.toDomain
import com.example.khizana_user.domain.model.Customer
import com.example.khizana_user.domain.repository.ShopifyRepository
import javax.inject.Inject

class ShopifyRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : ShopifyRepository {

    override suspend fun registerCustomerInShopify(name: String, email: String): Result<Customer> {
        return try {
            val request = ShopifyCreateCustomerRequest(
                customer = ShopifyCustomerData(
                    first_name = name,
                    email = email
                )
            )

            Log.d("ShopifyRepo", "Sending request to Shopify with payload: $request")
            val response = remoteDataSource.registerShopifyCustomer(request)

            val customerDto = response.body()?.customer
            return if (response.isSuccessful && customerDto != null) {
                Log.d("ShopifyRepo", "Customer created: $customerDto")
                Result.success(customerDto.toDomain())
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ShopifyRepo", "Register failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Shopify error: ${response.code()} - $errorBody"))
            }

        } catch (e: Exception) {
            Log.e("ShopifyRepo", "Exception in registerCustomerInShopify", e)
            Result.failure(e)
        }
    }

    override suspend fun searchCustomerByEmail(email: String): Result<Customer?> {
        return try {
            val query = "email:$email"
            val response = remoteDataSource.searchShopifyCustomerByEmail(query)

            if (response.isSuccessful) {
                val customerDto = response.body()?.customers?.firstOrNull()
                Log.d("ShopifyRepo", "Found customer: $customerDto")
                Result.success(customerDto?.toDomain())
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ShopifyRepo", "Search failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Search failed: ${response.code()} - $errorBody"))
            }

        } catch (e: Exception) {
            Log.e("ShopifyRepo", "Exception in searchCustomerByEmail", e)
            Result.failure(e)
        }
    }
}
