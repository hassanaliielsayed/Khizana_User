package com.example.khizana_user.utils

//fun String.toCurrentCurrency(): String {
//
//    return this.toDouble().times(CurrencyHelper.exchangeRates).toString()
//}

fun String.toCurrentCurrency(): String {
    return try {
        val convertedValue = this.toDouble() * CurrencyHelper.exchangeRates
        "%.2f".format(convertedValue)
    } catch (e: NumberFormatException) {
        "0.00"
    }
}

object CurrencyHelper {

    var exchangeRates: Double = 1.0
    var currencyUnit: String = "EGP"
}