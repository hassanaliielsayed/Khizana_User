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
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.khizana_user.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.forEach

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllBrandsUseCase: GetAllBrandsUseCase,
    private val getAllCouponsUseCase: GetAllCouponsUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase
) : ViewModel() {

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: StateFlow<List<Brand>> = _brands

    private val _coupons = MutableStateFlow<Result<List<Coupon>>>(Result.Loading)
    val coupons = _coupons.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchBrands()
        fetchCoupons()
    }

    fun fetchBrands() {
        Log.d("AllBrandsDebug", "fetchBrands method call")

        viewModelScope.launch {
            try {

                val result = getAllBrandsUseCase()
                _brands.value = result

                result.forEach {
                    Log.d("AllBrandsDebug", "Brand: ${it.title}, Image: ${it.imageUrl}")
                }
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
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}



//class AllBrandsViewModelFactory (private val useCase: GetAllBrandsUseCase):ViewModelProvider.Factory{
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return BrandViewModel(useCase) as T
//    }
//}