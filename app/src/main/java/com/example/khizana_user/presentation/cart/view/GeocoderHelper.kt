package com.example.khizana_user.presentation.cart.view

import android.content.Context
import android.location.Geocoder
import android.location.Address
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

suspend fun getCityAndCountryFromLatLng(
    context: Context,
    latLng: LatLng
): Pair<String, String>? = withContext(Dispatchers.IO) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).orEmpty()

        if (addresses.isNotEmpty()) {
            val city = addresses[0].locality ?: addresses[0].subAdminArea ?: "Unknown City"
            val country = addresses[0].countryName ?: "Unknown Country"
            city to country
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("Geocoder", "Failed to get address: ${e.message}", e)
        null
    }
}
