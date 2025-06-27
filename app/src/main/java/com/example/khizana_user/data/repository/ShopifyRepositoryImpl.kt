package com.example.khizana_user.data.repository

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

            val response = remoteDataSource.registerShopifyCustomer(request)

            val customerDto = response.body()?.customer
            return if (response.isSuccessful && customerDto != null) {
                Result.success(customerDto.toDomain())
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Shopify error: ${response.code()} - $errorBody"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchCustomerByEmail(email: String): Result<Customer?> {
        return try {
            val query = "email:$email"
            val response = remoteDataSource.searchShopifyCustomerByEmail(query)

            if (response.isSuccessful) {
                val customerDto = response.body()?.customers?.firstOrNull()
                Result.success(customerDto?.toDomain())
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Search failed: ${response.code()} - $errorBody"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
