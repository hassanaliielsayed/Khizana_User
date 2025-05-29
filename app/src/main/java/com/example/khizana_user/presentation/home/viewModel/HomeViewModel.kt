package com.example.khizana_user.presentation.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.model.Brand
import com.example.khizana_user.domain.usecase.GetAllBrandsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.forEach



@HiltViewModel
class HomeViewModel @Inject constructor(private val getAllBrandsUseCase: GetAllBrandsUseCase) : ViewModel() {

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: StateFlow<List<Brand>> = _brands

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchBrands()
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
}



//class AllBrandsViewModelFactory (private val useCase: GetAllBrandsUseCase):ViewModelProvider.Factory{
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return BrandViewModel(useCase) as T
//    }
//}