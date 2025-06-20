package com.example.khizana_user.presentation.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.model.Coupon
import com.example.khizana_user.domain.model.Product
import com.example.khizana_user.domain.usecase.home.GetAllBrandsUseCase
import com.example.khizana_user.domain.usecase.home.GetAllCouponsUseCase
import com.example.khizana_user.domain.usecase.home.GetAllProductsUseCase
import com.example.khizana_user.domain.usecase.home.GetExchangeRateUseCase
import com.example.khizana_user.domain.usecase.sharedperference.GetCurrencyUseCase
import com.example.khizana_user.utils.ConnectionLiveData
import com.example.khizana_user.utils.CurrencyHelper
import com.example.khizana_user.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchFocusType { BRAND, PRODUCT }

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllBrandsUseCase: GetAllBrandsUseCase,
    private val getAllCouponsUseCase: GetAllCouponsUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
    private val getCurrencyUseCase: GetCurrencyUseCase,
    private val connectionLiveData: ConnectionLiveData
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

    private val _networkState = MutableStateFlow(true)
    val networkState = _networkState.asStateFlow()

    private val _searchFocusType = MutableStateFlow<SearchFocusType?>(null)
    val searchFocusType: StateFlow<SearchFocusType?> = _searchFocusType

    fun setFocus(type: SearchFocusType?) {
        _searchFocusType.value = type
    }

    init {
        observeNetworkState()
        fetchInitialData()
        setupSearchHandler()
        setupCurrencyObserver()
    }

    private fun observeNetworkState() {
        viewModelScope.launch {
            connectionLiveData
                .asFlow()
                .distinctUntilChanged()
                .collect { isConnected ->
                    _networkState.value = isConnected
                    if (isConnected) {
                        fetchInitialData()
                    }
                }
        }
    }

    private fun setupCurrencyObserver() {
        viewModelScope.launch {
            getCurrencyUseCase().collect { savedCurrency ->
                CurrencyHelper.currencyUnit = savedCurrency ?: "EGP"
                CurrencyHelper.exchangeRates = if (CurrencyHelper.currencyUnit != "EGP" && _networkState.value) {
                    try {
                        getExchangeRateUseCase("EGP", CurrencyHelper.currencyUnit).rate
                    } catch (e: Exception) {
                        _error.value = "Failed to update exchange rates"
                        1.0
                    }
                } else 1.0
            }
        }
    }

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            _searchQuery.emit(query)
        }
    }

    private fun fetchInitialData() {
        if (_networkState.value) {
            fetchBrands()
            fetchCoupons()
            fetchAllProducts()
        } else {
            _error.value = "No internet connection"
        }
    }

    private fun setupSearchHandler() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (_networkState.value) {
                        handleSmartSearch(query)
                    } else {
                        _error.value = "Search unavailable offline"
                    }
                }
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

        _suggestions.value = (matchedBrands.map { "Brand: ${it.title}" } +
                matchedProducts.map { it.productTitle })
            .distinct()
            .take(5)

        val brandExact = _allBrands.value.find { it.title.equals(query, ignoreCase = true) }
        if (brandExact != null) {
            viewModelScope.launch {
                val brandProducts = getAllProductsUseCase(brandExact.title)
                _filteredProducts.value = brandProducts
            }
            return
        }

        _filteredProducts.value = matchedProducts

        if (matchedProducts.size == 1) {
            viewModelScope.launch {
                _navigateToProduct.emit(matchedProducts.first().id)
            }
        }
    }

    fun fetchBrands() {
        if (!_networkState.value) {
            _error.value = "Cannot fetch brands offline"
            return
        }

        viewModelScope.launch {
            try {
                val result = getAllBrandsUseCase()
                _brands.value = result
                _allBrands.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load brands"
            }
        }
    }

    fun fetchCoupons() {
        if (!_networkState.value) {
            _error.value = "Cannot fetch brands offline"
            return
        }

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
        if (!_networkState.value) {
            _error.value = "Cannot fetch products offline"
            return
        }
        viewModelScope.launch {
            try {
                val result = getAllProductsUseCase(vendor)
                _products.value = result
                _filteredProducts.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load products"
            }
        }
    }

    fun fetchAllProducts() {
        if (!_networkState.value) {
            _error.value = "Cannot fetch products offline"
            return
        }
        viewModelScope.launch {
            try {
                val result = getAllProductsUseCase("") // empty = fetch all
                _allProducts.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load products"
            }
        }
    }
}
