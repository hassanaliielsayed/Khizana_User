@file:Suppress("NAME_SHADOWING")

package com.example.khizana_user.presentation.cart.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.khizana_user.R
import com.example.khizana_user.data.dto.draftorderDto.AppliedDiscountDto
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderItem
import com.example.khizana_user.data.dto.draftorderDto.ShippingAddressDto
import com.example.khizana_user.presentation.cart.viewmodel.CartViewModel
import com.example.khizana_user.presentation.cart.viewmodel.LocationViewModel
import com.example.khizana_user.presentation.home.view.NoInternetConnectionView
import com.example.khizana_user.presentation.order.viewmodel.OrderViewModel
import com.example.khizana_user.utils.ConfirmationDialog
import com.example.khizana_user.utils.LocationUtils
import com.example.khizana_user.utils.PaymentMethod
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.customFontFamily
import com.example.khizana_user.utils.toCurrentCurrency
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    customerId: Long,
    viewModel: CartViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel(),
    totalPrice: Double,
    onBackClick: () -> Unit,
    onPlaceOrderClick: () -> Unit,
    onNavigateToOrderSuccess: () -> Unit,
    onAddressClick: () -> Unit,
    navController: NavController,
    onPaymentMethodClick: () -> Unit
) {

    val selectedAddress by locationViewModel.selectedAddress.collectAsState()
    val selectedLatLng by locationViewModel.selectedLatLng.collectAsState()

    // Handle saved state
    val savedLocation by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<Pair<LatLng, String>?>("selected_location", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(savedLocation) {
        savedLocation?.let { (latLng, address) ->
            locationViewModel.updateAddress(address, latLng)
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<Pair<LatLng, String>>("selected_location")
        }
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var couponCode by remember { mutableStateOf("") }
    var showConfirmationDialog by remember { mutableStateOf(false) }


    val couponState by viewModel.couponState.collectAsStateWithLifecycle()
    val cartState by viewModel.cartState.collectAsStateWithLifecycle()
    val orderState by orderViewModel.orderState.collectAsStateWithLifecycle()
    val invoiceUrlState by orderViewModel.invoiceUrl.collectAsStateWithLifecycle()


    val coupon = (couponState as? Result.Success)?.data
    val totalDiscount = if (coupon != null) totalPrice * (coupon.discount / 100.0) else 0.0
    val grandTotal = remember(totalPrice, totalDiscount) { totalPrice - totalDiscount }

//    val selectedAddress by locationViewModel.selectedAddress.collectAsStateWithLifecycle()
//    val selectedLatLng by locationViewModel.selectedLatLng.collectAsStateWithLifecycle()

    val locationUtils = remember { LocationUtils(context) }

    val lifecycleOwner = LocalLifecycleOwner.current
    var locationPermissionGranted by remember { mutableStateOf(locationUtils.hasLocationPermission()) }
    var locationEnabled by remember { mutableStateOf(locationUtils.isLocationEnabled()) }

    var userSelectedLocation by remember { mutableStateOf(false) }

    val autocompleteLauncher = LaunchPlacesAutoComplete(context) { place ->
        val address = place.address ?: context.getString(R.string.unknown)
        val latLng = place.latLng ?: LatLng(30.0, 31.0)
        userSelectedLocation = true
        locationViewModel.updateAddress(address, latLng)
    }

    val contect = LocalContext.current
    val forceOnlinePayment = grandTotal >= 2000
    var selectedPaymentMethod by remember {
        mutableStateOf(if (forceOnlinePayment) PaymentMethod.ONLINE else PaymentMethod.COD)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                locationEnabled = locationUtils.isLocationEnabled()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val fetchCurrentLocation = remember {
        suspend {
            try {
                val location = locationUtils.getCurrentLocation().first()
                val geocoder = Geocoder(context)
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val address = addresses?.firstOrNull()?.getAddressLine(0)
                    ?: contect.getString(R.string.unknown_address)
                locationViewModel.updateAddress(
                    address,
                    LatLng(location.latitude, location.longitude)
                )
            } catch (e: Exception) {
                Log.e("CheckoutScreen", "Error getting location: ${e.message}")
            }
        }
    }

    // Fetch location when screen loads or when location becomes enabled
    LaunchedEffect(locationEnabled) {
        if (locationPermissionGranted && locationEnabled && selectedAddress.isEmpty()) {
            fetchCurrentLocation()
        }
    }

    // Original location fetch (kept for initial load if already enabled)
    LaunchedEffect(Unit) {
        if (locationPermissionGranted && locationEnabled && selectedAddress.isEmpty()) {
            fetchCurrentLocation()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Request location permission if not granted
    if (!locationPermissionGranted) {
        LaunchedEffect(Unit) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Show location services dialog if disabled
    if (!locationEnabled) {
        AlertDialog(
            onDismissRequest = {
                // Allow dismiss now, but you might want to handle this case
                locationEnabled = locationUtils.isLocationEnabled()
            },
            title = { Text(stringResource(R.string.location_services_disabled)) },
            text = { Text(stringResource(R.string.please_enable_location_services_to_get_your_current_address)) },
            confirmButton = {
                Button(
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue))
                ) {
                    Text(
                        stringResource(R.string.enable_location),
                        color = Color.Black
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        locationEnabled = locationUtils.isLocationEnabled()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue))
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        color = Color.Black
                    )
                }
            }
        )
    }

    LaunchedEffect(customerId) {
        viewModel.loadCart(customerId)
    }

    LaunchedEffect(orderState) {
        if (orderState is Result.Success) onNavigateToOrderSuccess()
    }

    LaunchedEffect(invoiceUrlState) {
        if (invoiceUrlState is Result.Success) {
            val url = (invoiceUrlState as Result.Success).data
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text(stringResource(R.string.checkout)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        )

        // Shipping Address with Map
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(Modifier.padding(10.dp)) {
                Text(
                    stringResource(R.string.shipping_address),
                    color = Color(0xFFA1A6B0),
                    fontSize = 14.sp,
                    fontFamily = customFontFamily,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.address),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = customFontFamily,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedAddress.ifBlank { stringResource(R.string.please_select_a_location) },
                    color = colorResource(R.color.content_color),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = customFontFamily,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Button(
                        onClick = { autocompleteLauncher() },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.content_color))
                    ) {
                        Text(
                            stringResource(R.string.change_address),
                            color = Color.Black, fontFamily = customFontFamily,
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            userSelectedLocation = true
                            onAddressClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.content_color))
                    ) {
                        Text(
                            stringResource(R.string.select_on_map),
                            color = Color.Black, fontFamily = customFontFamily,

                            )
                    }
                }
            }
        }

        // Payment Method

        var expanded by remember { mutableStateOf(false) }


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    "Payment Method",
                    color = Color(0xFFA1A6B0),
                    fontSize = 14.sp,
                    fontFamily = customFontFamily,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_payment_method),
                        contentDescription = "Payment Method",
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    // Payment method text and dropdown
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            when (selectedPaymentMethod) {
                                PaymentMethod.COD -> "Cash on Delivery (COD)"
                                PaymentMethod.ONLINE -> "Online Payment"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = customFontFamily,
                        )
                    }

                    // Dropdown menu
                    Box(
                        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
                    ) {
                        IconButton(
                            onClick = { expanded = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select Payment Method"
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Cash on Delivery (COD)",
                                        fontFamily = customFontFamily,
                                    )
                                },
                                onClick = {
                                    if (!forceOnlinePayment) {
                                        selectedPaymentMethod = PaymentMethod.COD
                                        expanded = false
                                    }
                                },
                                enabled = !forceOnlinePayment
                            )
                            DropdownMenuItem(
                                text = { Text("Online Payment", fontFamily = customFontFamily) },
                                onClick = {
                                    selectedPaymentMethod = PaymentMethod.ONLINE
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Fees
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Sub Total",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                    )
                    Text(
                        totalPrice.toCurrentCurrency(),
                        color = Color(0xFF929292),
                        fontFamily = customFontFamily,
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Shipping Fees",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                    )
                    Text("0.0 EGP", color = Color(0xFF929292), fontFamily = customFontFamily)
                }
            }
        }

        // Coupon
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    "Coupon",
                    color = Color(0xFFA1A6B0),
                    fontSize = 14.sp,
                    fontFamily = customFontFamily,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = couponCode,
                        onValueChange = { couponCode = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Enter coupon code") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { viewModel.validateCoupon(couponCode) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue))
                    ) {
                        Text(
                            "Validate",
                            color = Color.Black, fontFamily = customFontFamily,
                        )
                    }
                }
            }
        }

        // Discount
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    "Discount",
                    color = Color(0xFFA1A6B0),
                    fontSize = 14.sp,
                    fontFamily = customFontFamily,
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Discount",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                    )
                    Text(
                        totalDiscount.toCurrentCurrency(),
                        color = Color(0xFF929292),
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                    )
                }
            }
        }

        // Grand Total
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    "Grand Total",
                    color = Color(0xFFA1A6B0),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = customFontFamily,
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total Amount",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                    )
                    Text(
                        grandTotal.toCurrentCurrency(),
                        color = Color(0xFF929292),
                        fontFamily = customFontFamily,
                    )
                }
            }
        }

        // Place Order Button
        Button(

            onClick = { showConfirmationDialog = true },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue)),
            elevation = ButtonDefaults.buttonElevation(8.dp),
        ) {
            Text(
                "Place Order",
                color = Color.Black,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

    }


    ConfirmationDialog(
        showDialog = showConfirmationDialog,
        onDismiss = { showConfirmationDialog = false },
        onConfirm = {
            if (forceOnlinePayment && selectedPaymentMethod == PaymentMethod.COD) {
                // Show error toast or dialog
                Toast.makeText(
                    context,
                    "Orders over 3000 EGP must be paid online",
                    Toast.LENGTH_LONG
                ).show()
                return@ConfirmationDialog
            }
            showConfirmationDialog = false
            coroutineScope.launch {
                val draftOrder = (cartState as? Result.Success)?.data
                val draftOrderId = draftOrder?.draftOrderId ?: return@launch

                val cityCountry = getCityAndCountryFromLatLng(context, selectedLatLng)
                val shippingAddressDto = ShippingAddressDto(
                    address1 = selectedAddress,
                    city = cityCountry?.first ?: "Unknown",
                    country = cityCountry?.second ?: "Unknown",
                    zip = "00000"
                )

                val appliedDiscount = coupon?.let {
                    AppliedDiscountDto(
                        title = it.title,
                        value = it.discount.toString(),
                        valueType = "percentage",
                        amount = (totalPrice * (it.discount / 100.0)).toString()
                    )
                }

                val draftLineItems = draftOrder.items.filterNotNull().map {
                    DraftOrderItem(variantId = it.variantId, quantity = it.quantity)
                }

                orderViewModel.updateDraftOrderBeforeCheckout(
                    draftOrderId = draftOrderId,
                    customerId = customerId,
                    shippingAddress = shippingAddressDto,
                    appliedDiscount = appliedDiscount,
                    lineItems = draftLineItems
                )

                when (selectedPaymentMethod) {
                    PaymentMethod.COD -> orderViewModel.completeCODOrder(draftOrderId)
                    PaymentMethod.ONLINE -> orderViewModel.initiateOnlinePayment(draftOrderId)
                }
            }
        },
        title = "Confirm Order",
        text = "Please confirm your order:\n\nShipping Address: $selectedAddress\nTotal Amount: ${grandTotal.toCurrentCurrency()}",
        confirmText = "Place Order",
        dismissText = "Cancel",
        confirmButtonColor = colorResource(R.color.dark_blue),
        dismissButtonColor = colorResource(R.color.dark_blue)
    )
}
