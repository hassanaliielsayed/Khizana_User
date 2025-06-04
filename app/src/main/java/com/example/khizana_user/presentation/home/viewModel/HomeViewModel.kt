package com.example.khizana_user.presentation.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.model.Product
import com.example.khizana_user.domain.usecase.GetAllBrandsUseCase
import com.example.khizana_user.domain.usecase.GetAllCouponsUseCase
import com.example.khizana_user.domain.usecase.GetAllProductsUseCase
import com.example.khizana_user.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllBrandsUseCase: GetAllBrandsUseCase,
    private val getAllCouponsUseCase: GetAllCouponsUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase
) : ViewModel() {

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands = _brands.asStateFlow()

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _allBrands = MutableStateFlow<List<Brand>>(emptyList())

    private val _coupons = MutableStateFlow<Result<List<Coupon>>>(Result.Loading)
    val coupons = _coupons.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts = _filteredProducts.asStateFlow()

    private val _searchQuery = MutableSharedFlow<String>(replay = 1)
    val searchQuery = _searchQuery.asSharedFlow()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions = _suggestions.asStateFlow()

    private val _navigateToProduct = MutableSharedFlow<Long>()
    val navigateToProduct = _navigateToProduct.asSharedFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        fetchBrands()
        fetchCoupons()
        fetchAllProducts()

        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    handleSmartSearch(query)
                }
        }
    }

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            _searchQuery.emit(query)
        }
    }

    private fun handleSmartSearch(query: String) {
        if (query.isBlank()) {
            _filteredProducts.value = _products.value
            _suggestions.value = emptyList()
            return
        }

        val matchedBrands = _allBrands.value.filter {
            it.title.contains(query, ignoreCase = true)
        }

        val matchedProducts = _allProducts.value.filter {
            it.productTitle.contains(query, ignoreCase = true)
        }

        // Combine suggestions: "Brand: X" + product titles
        _suggestions.value = (matchedBrands.map { "Brand: ${it.title}" } +
                matchedProducts.map { it.productTitle })
            .distinct()

        // If full brand name typed exactly
        val brandExact = _allBrands.value.find { it.title.equals(query, ignoreCase = true) }
        if (brandExact != null) {
            viewModelScope.launch {
                val brandProducts = getAllProductsUseCase(brandExact.title)
                _filteredProducts.value = brandProducts
            }
            return
        }

        _filteredProducts.value = matchedProducts

        // Auto-navigate if only one exact product
        if (matchedProducts.size == 1) {
            viewModelScope.launch {
                _navigateToProduct.emit(matchedProducts.first().id)
            }
        }
    }

    fun fetchBrands() {
        viewModelScope.launch {
            try {
                val result = getAllBrandsUseCase()
                _brands.value = result
                _allBrands.value = result
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun fetchCoupons() {
        viewModelScope.launch {
            try {
                val result = getAllCouponsUseCase()
                _coupons.value = Result.Success(result)
            } catch (e: Exception) {
                _coupons.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchProductsByVendor(vendor: String) {
        viewModelScope.launch {
            try {
                val result = getAllProductsUseCase(vendor)
                _products.value = result
                _filteredProducts.value = result
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun fetchAllProducts() {
        viewModelScope.launch {
            try {
                val result = getAllProductsUseCase("") // empty = fetch all
                _allProducts.value = result
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
