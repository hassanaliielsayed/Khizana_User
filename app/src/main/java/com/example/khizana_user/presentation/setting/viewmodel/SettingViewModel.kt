package com.example.khizana_user.presentation.setting.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khizana_user.domain.usecase.GetExchangeRateUseCase
import com.example.khizana_user.utils.CurrencyHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getExchangeRateUseCase: GetExchangeRateUseCase
) : ViewModel() {



    fun getExchangeRate(base: String, target: String) {

        viewModelScope.launch {
            try {
                val result = getExchangeRateUseCase(base, target)
                CurrencyHelper.exchangeRates = result.rate
                CurrencyHelper.currencyUnit = result.code
                Log.i("asdf ->>",  "${CurrencyHelper.exchangeRates}")
            }  catch (e: Exception) {
                //_state.value = Result.Error(e.message ?: "Unknown error")
                Log.i("asdf ->>",  "${e.message}")
            }
        }
    }

}