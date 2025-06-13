package com.example.khizana_user.presentation.cart.view

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.khizana_user.R
import com.example.khizana_user.data.dto.draftorderDto.AppliedDiscountDto
import com.example.khizana_user.data.dto.draftorderDto.CustomerData
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderData
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderItem
import com.example.khizana_user.data.dto.draftorderDto.DraftOrderRequest
import com.example.khizana_user.data.dto.draftorderDto.ShippingAddressDto
import com.example.khizana_user.presentation.cart.viewmodel.CartViewModel
import com.example.khizana_user.presentation.cart.viewmodel.LocationViewModel
import com.example.khizana_user.presentation.home.view.NoInternetConnectionView
import com.example.khizana_user.presentation.order.viewmodel.OrderViewModel
import com.example.khizana_user.utils.ConfirmationDialog
import com.example.khizana_user.utils.PaymentMethod
import com.example.khizana_user.utils.Result
import com.example.khizana_user.utils.toCurrentCurrency
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

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
    onPaymentMethodClick: () -> Unit
) {
    var couponCode by remember { mutableStateOf("") }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.COD) }

    val couponState by viewModel.couponState.collectAsStateWithLifecycle()
    val cartState by viewModel.cartState.collectAsState()
    val orderState by orderViewModel.orderState.collectAsState()
    val invoiceUrlState by orderViewModel.invoiceUrl.collectAsState()

    val coupon = (couponState as? Result.Success)?.data
    val totalDiscount = if (coupon != null) totalPrice * (coupon.discount / 100.0) else 0.0
    val grandTotal = remember(totalPrice, totalDiscount) { totalPrice - totalDiscount }

    val selectedAddress by locationViewModel.selectedAddress.collectAsState()
    val selectedLatLng by locationViewModel.selectedLatLng.collectAsState()
    val context = LocalContext.current
    val autocompleteLauncher = LaunchPlacesAutoComplete(context) { place ->
        val address = place.address ?: "Unknown"
        val latLng = place.latLng ?: LatLng(30.0, 31.0)
        locationViewModel.updateAddress(address, latLng)
    }

    val connectionState by viewModel.networkState.collectAsState()

    LaunchedEffect(customerId) {
        if (connectionState) {
            viewModel.loadCart(customerId)
        }
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

    if (!connectionState) {
        NoInternetConnectionView()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                    Text("Shipping Address", color = Color(0xFFA1A6B0), fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Address", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedAddress,
                        color = Color(0xFF929292),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(onClick = autocompleteLauncher) {
                            Text("Search Address")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = onAddressClick) {
                            Text("Select on Map")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(selectedLatLng, 12f)
                        }
                    ) {
                        Marker(
                            state = MarkerState(position = selectedLatLng),
                            title = "Your Address"
                        )
                    }
                }
            }

            // Payment Method
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Payment Method", color = Color(0xFFA1A6B0), fontSize = 14.sp)
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
                        Text(
                            if (selectedPaymentMethod == PaymentMethod.COD) "Cash on Delivery (COD)" else "Online Payment",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .padding(15.dp)
                                .background(Color(0xFFE3E5E8), RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedPaymentMethod =
                                        if (selectedPaymentMethod == PaymentMethod.COD)
                                            PaymentMethod.ONLINE else PaymentMethod.COD
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Change Payment",
                                modifier = Modifier.size(20.dp)
                            )
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
                    Text("Fees", color = Color(0xFFA1A6B0), fontSize = 14.sp)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sub Total", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(totalPrice.toCurrentCurrency(), color = Color(0xFF929292))
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Shipping Fees", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("0.0 EGP", color = Color(0xFF929292))
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
                    Text("Coupon", color = Color(0xFFA1A6B0), fontSize = 14.sp)
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
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Validate")
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
                    Text("Discount", color = Color(0xFFA1A6B0), fontSize = 14.sp)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Discount", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(totalDiscount.toCurrentCurrency(), color = Color(0xFF929292))
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
                    Text("Grand Total", color = Color(0xFFA1A6B0), fontSize = 14.sp)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(grandTotal.toCurrentCurrency(), color = Color(0xFF929292))
                    }
                }
            }

            // Place Order Button
            Button(
                onClick = { showConfirmationDialog = true },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                elevation = ButtonDefaults.buttonElevation(8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Place Order")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_group_right_arrow),
                    contentDescription = "Place Order"
                )
            }

        }
    }

    val coroutineScope = rememberCoroutineScope()

    ConfirmationDialog(
        showDialog = showConfirmationDialog,
        onDismiss = { showConfirmationDialog = false },
        onConfirm = {
            coroutineScope.launch {
                Log.i("CheckoutScreen", "Cart State: $cartState")
                val draftOrder = (cartState as? Result.Success)?.data
                val draftOrderId = draftOrder?.draftOrderId

                if (draftOrderId != null) {
                    Log.i("CheckoutScreen", "Draft ID: $draftOrderId")
                    Log.i("CheckoutScreen", "Customer ID: $customerId")
                    Log.i("CheckoutScreen", "Selected Payment Method: $selectedPaymentMethod")
                    Log.i("CheckoutScreen", "Address: $selectedAddress")
                    Log.i("CheckoutScreen", "atLng: $selectedLatLng")

                    val cityCountry = getCityAndCountryFromLatLng(context, selectedLatLng)
                    val city = cityCountry?.first ?: "Unknown"
                    val country = cityCountry?.second ?: "Unknown"

                    Log.i("CheckoutScreen", "City: $city, Country: $country")

                    val shippingAddressDto = ShippingAddressDto(
                        address1 = selectedAddress,
                        city = city,
                        country = country,
                        zip = "00000"
                    )

                    val discountAmount = totalPrice * (coupon?.discount?.toDouble() ?: 0.0) / 100.0
                    Log.i(
                        "CheckoutScreen",
                        "Discount: ${coupon?.discount}% (${discountAmount.toCurrentCurrency()})"
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


                    val draftRequest = DraftOrderRequest(
                        draftOrder = DraftOrderData(
                            line_items = draftLineItems,
                            customer = CustomerData(customerId),
                            note = "CART-$customerId",
                            shipping_address = shippingAddressDto,
                            applied_discount = appliedDiscount,
                            use_customer_default_address = false
                        )
                    )


                    Log.i("CheckoutScreen", "DraftOrderRequest Body:\n$draftRequest")

                    orderViewModel.updateDraftOrderBeforeCheckout(
                        draftOrderId = draftOrderId,
                        customerId = customerId,
                        shippingAddress = shippingAddressDto,
                        appliedDiscount = appliedDiscount,
                        lineItems = draftLineItems
                    )



                    when (selectedPaymentMethod) {
                        PaymentMethod.COD -> {
                            Log.i("CheckoutScreen", "placing COD order...")
                            orderViewModel.completeCODOrder(draftOrderId)
                        }

                        PaymentMethod.ONLINE -> {
                            Log.i("CheckoutScreen", "Initiating online payment...")
                            orderViewModel.initiateOnlinePayment(draftOrderId)
                        }
                    }
                } else {
                    Log.e("CheckoutScreen", "Draft order ID is null.")
                }
            }
        },
        title = "Confirm Order",
        text = "Please confirm your order:\n\nShipping Address: $selectedAddress\nTotal Amount: ${grandTotal.toCurrentCurrency()}",
        confirmText = "Place Order",
        dismissText = "Cancel",
        confirmButtonColor = Color(0xFF6200EE)
    )
}