package com.example.khizana_user.presentation.cart.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.khizana_user.utils.LocationUtils
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    navController: NavController,
    defaultLocation: LatLng = LatLng(30.0444, 31.2357)
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationUtils = remember { LocationUtils(context) }

    var selected by remember { mutableStateOf(defaultLocation) }
    var address by remember { mutableStateOf("Select a location on map") }
    val camera = rememberCameraPositionState()

    val placeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            val latLng = place.latLng
            val placeAddress = place.address ?: "Unknown"
            if (latLng != null) {
                selected = latLng
                address = placeAddress
                camera.position = CameraPosition.fromLatLngZoom(latLng, 14f)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (locationUtils.hasLocationPermission()) {
            val fused = LocationServices.getFusedLocationProviderClient(context)
            val location = withContext(Dispatchers.IO) {
                fused.lastLocation.await()
            }
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                selected = latLng
                camera.position = CameraPosition.fromLatLngZoom(latLng, 14f)
                address = getAddressFromLatLng(context, latLng)
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        Button(
            onClick = {
                val fields = listOf(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS)
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields
                ).build(context)
                placeLauncher.launch(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Search Location")
        }

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            cameraPositionState = camera,
            onMapClick = {
                selected = it
                coroutineScope.launch {
                    address = getAddressFromLatLng(context, it)
                }
            }
        ) {
            Marker(state = MarkerState(position = selected), title = "Selected Location")
        }

        Column(Modifier.padding(16.dp)) {
            Text("Selected Address:")
            Text(address)

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_location", Pair(selected, address))
                    navController.popBackStack()
                },
                enabled = address != "Select a location on map",
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Location")
            }
        }
    }
}

suspend fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val result = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            result?.firstOrNull()?.getAddressLine(0) ?: "Unknown Address"
        } catch (e: Exception) {
            "Error resolving address"
        }
    }
}