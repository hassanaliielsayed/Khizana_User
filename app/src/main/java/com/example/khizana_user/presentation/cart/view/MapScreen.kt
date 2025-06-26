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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.khizana_user.R
import com.example.khizana_user.presentation.AppLogo
import com.example.khizana_user.presentation.cart.viewmodel.LocationViewModel
import com.example.khizana_user.utils.LocationUtils
import com.example.khizana_user.utils.customFontFamily
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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    navController: NavController,
    locationViewModel: LocationViewModel = hiltViewModel(),
    defaultLocation: LatLng = LatLng(30.0444, 31.2357)
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationUtils = remember { LocationUtils(context) }

    var selected by remember { mutableStateOf(defaultLocation) }
    var address by remember { mutableStateOf(context.getString(R.string.select_a_location_on_map)) }
    val camera = rememberCameraPositionState()

    val placeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            val latLng = place.latLng
            val placeAddress = place.address ?: context.getString(R.string.unknown)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AppLogo()
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.light_blue)
                ),
            )
        },
    ) { innerPadding ->
        Column(Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
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
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(
                        id = R.color.content_color
                    )
                )
            ) {
                Text(stringResource(R.string.search_location), color = Color.White,
                    fontFamily = customFontFamily, fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
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
                Marker(state = MarkerState(position = selected), title = stringResource(R.string.selected_location))
            }

            Column(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.selected_address),
                    fontFamily = customFontFamily)
                Text(address,
                    fontFamily = customFontFamily)

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selected_location", Pair(selected, address))
                        locationViewModel.updateAddress(address, selected)
                        navController.popBackStack()
                    },
                    enabled = address != stringResource(R.string.selected_location),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(
                            id = R.color.dark_blue
                        )
                    )
                ){
                    Text(stringResource(R.string.save_location), fontFamily = customFontFamily, fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black)
                }
            }
        }
    }
}

suspend fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val result = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            result?.firstOrNull()?.getAddressLine(0) ?: context.getString(R.string.unknown_address)
        } catch (e: Exception) {
            context.getString(R.string.error_resolving_address)
        }
    }
}