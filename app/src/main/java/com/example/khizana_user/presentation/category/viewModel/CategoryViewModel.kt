package com.example.khizana_user.presentation.category.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.ProductByCategory
import com.example.khizana_user.domain.usecase.category.GetAllProductsByCategoryUseCase
import com.example.khizana_user.utils.ConnectionLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getAllProductsByCategoryUseCase: GetAllProductsByCategoryUseCase
) : ViewModel() {

    private val _allProducts = MutableStateFlow<List<ProductByCategory>>(emptyList())
    private val _products = MutableStateFlow<List<ProductByCategory>>(emptyList())
    val products = _products.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentMainCategory: String = "All"
    private var currentSubCategory: String = "All"

    private var currentPrice: Float = Float.MAX_VALUE


    init {
        getAllProducts()
    }


    fun getAllProducts() {
        viewModelScope.launch {
            try {
                val result = getAllProductsByCategoryUseCase()
                _allProducts.value = result
                _products.value = result
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun filterProductsByTag(tag: String) {
        currentMainCategory = tag
        filterProducts()
    }

    fun filterProductsBySubCategory(subCategory: String) {
        currentSubCategory = subCategory
        filterProducts()
    }

    private fun filterProducts() {
        _products.value = _allProducts.value.filter { product ->
            val main = when (currentMainCategory) {
                "All" -> true
                else -> product.productTags.any { it.equals(currentMainCategory, ignoreCase = true) }
            }

            val sub = when (currentSubCategory) {
                "All" -> true
                else -> product.product_type?.equals(currentSubCategory, ignoreCase = true) ?: false
            }

            val priceFilter = product.productPrice <= currentPrice

            main && sub && priceFilter
        }
    }

    fun filterProductsByPrice(price: Float) {
        currentPrice = price
        filterProducts()
    }

    fun filterProductsBySearch(query: String) {
        _products.value = _allProducts.value.filter {
            it.productTitle.contains(query, ignoreCase = true)
        }
    }

}
