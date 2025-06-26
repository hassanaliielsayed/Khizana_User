package com.example.khizana_user.presentation.cart.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor() : ViewModel() {

    private val _selectedAddress = MutableStateFlow("")
    val selectedAddress = _selectedAddress.asStateFlow()

    private val _selectedLatLng = MutableStateFlow(LatLng(0.0, 0.0))
    val selectedLatLng = _selectedLatLng.asStateFlow()

    private var isManualSelection = false

    fun updateAddress(address: String, latLng: LatLng) {
        _selectedAddress.value = address
        _selectedLatLng.value = latLng
        isManualSelection = true
    }
}