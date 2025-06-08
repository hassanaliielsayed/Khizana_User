package com.example.khizana_user.presentation.setting.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.usecase.sharedperfernceusecase.ClearCustomerUseCase
import com.example.khizana_user.domain.usecase.GetCurrencyUseCase
import com.example.khizana_user.domain.usecase.GetExchangeRateUseCase
import com.example.khizana_user.domain.usecase.SaveCurrencyUseCase
import com.example.khizana_user.utils.CurrencyHelper
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
    private val clearCustomerUseCase: ClearCustomerUseCase,
    private val saveCurrencyUseCase: SaveCurrencyUseCase,
    private val getCurrencyUseCase: GetCurrencyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow("EGP")
    val state = _state.asStateFlow()

    init {

        viewModelScope.launch {
            getCurrencyUseCase().collect { savedCurrency ->
                CurrencyHelper.currencyUnit = savedCurrency ?: "EGP"
                _state.value = CurrencyHelper.currencyUnit
                Log.i("taag", ": ${CurrencyHelper.currencyUnit} ")
            }
        }

    }

    fun getExchangeRate(base: String, target: String) {
        viewModelScope.launch {
            try {
                val result = getExchangeRateUseCase(base, target)
                CurrencyHelper.exchangeRates = result.rate
                CurrencyHelper.currencyUnit = result.code
                Log.i("SettingViewModel", "Updated exchange rate: ${result.rate} ${result.code}")
            } catch (e: Exception) {
                Log.e("SettingViewModel", "Error fetching exchange rate: ${e.message}")
            }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        viewModelScope.launch {
            clearCustomerUseCase()
            Log.d("SettingViewModel", "User logged out and DataStore cleared.")
        }
    }

    fun saveCurrency(currency: String) {
        viewModelScope.launch {
            saveCurrencyUseCase(currency)
        }

    }

    fun updateCurrency(currency: String) {
        _state.value = currency
    }

}
