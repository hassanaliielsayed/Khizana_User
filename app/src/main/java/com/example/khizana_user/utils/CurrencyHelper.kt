package com.example.khizana_user.utils

fun String.toCurrentCurrency(): String {

    return this.toDouble().times(CurrencyHelper.exchangeRates).toString()
}

object CurrencyHelper {

    var exchangeRates: Double = 1.0
    var currencyUnit: String = "EGP"
}