package com.example.khizana_user.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    onLocationSelected: (LatLng) -> Unit,
    defaultLocation: LatLng = LatLng(30.0444, 31.2357)
) {
    var selected by remember { mutableStateOf(defaultLocation) }
    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = camera,
        onMapClick = {
            selected = it
            onLocationSelected(it)
        }
    ) {
        Marker(
            state = MarkerState(position = selected),
            title = "Selected Location"
        )
    }
}