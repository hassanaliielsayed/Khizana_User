package com.example.khizana_user.presentation.cart.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor() : ViewModel() {

    private val _selectedAddress = MutableStateFlow("Cairo, Ain Shams")
    val selectedAddress: StateFlow<String> = _selectedAddress

    private val _selectedLatLng = MutableStateFlow(LatLng(30.0444, 31.2357))
    val selectedLatLng: StateFlow<LatLng> = _selectedLatLng

    fun updateAddress(address: String, latLng: LatLng) {
        _selectedAddress.value = address
        _selectedLatLng.value = latLng
    }
}