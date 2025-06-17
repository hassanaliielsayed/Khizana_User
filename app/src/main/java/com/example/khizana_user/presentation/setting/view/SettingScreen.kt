package com.example.khizana_user.presentation.setting.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.khizana_user.R
import com.example.khizana_user.presentation.AppLogo
import com.example.khizana_user.presentation.home.view.NoInternetConnectionView
import com.example.khizana_user.presentation.setting.viewmodel.SettingViewModel
import com.example.khizana_user.utils.customFontFamily
import com.example.khizana_user.utils.isGuestUser


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onContactUsClick: () -> Unit = {},
    onAboutUsClick: () -> Unit = {},
    viewModel: SettingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val selectedCurrency by viewModel.state.collectAsStateWithLifecycle()
    val connectionState by viewModel.networkState.collectAsStateWithLifecycle()

    if (!connectionState) {
        NoInternetConnectionView()
        return
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.light_background))
                .padding(horizontal = 16.dp)
                .padding(innerPadding)
        ) {

            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 24.dp),
                fontFamily = customFontFamily
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    SettingItem(
                        title = stringResource(R.string.address),
                        value = stringResource(R.string.cairo_egypt),
                        onClick = { /* TODO */ }
                    )

                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = colorResource(R.color.dark_blue),
                        thickness = 2.dp
                    )

                    SettingItem(
                        title = stringResource(R.string.currency),
                        value = selectedCurrency,
                        onClick = { showCurrencyDialog = true }
                    )

                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = colorResource(R.color.dark_blue),
                        thickness = 2.dp
                    )

                    SettingItem(
                        title = stringResource(R.string.contact_us),
                        onClick = onContactUsClick
                    )

                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = colorResource(R.color.dark_blue),
                        thickness = 2.dp
                    )

                    SettingItem(
                        title = stringResource(R.string.about_us),
                        onClick = onAboutUsClick
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isGuestUser()) {
                Button(
                    onClick = {
                        navController.navigate(context.getString(R.string.login)) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                    )
                }
            } else {
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.logout),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                    )
                }
            }
        }
    }
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.select_currency),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = customFontFamily,
                )
            },
            text = {
                Column {
                    listOf(stringResource(R.string.egp), stringResource(R.string.usd)).forEach { currency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateCurrency(currency)
                                    viewModel.saveCurrency(currency)
                                    viewModel.getExchangeRate(
                                        context.getString(R.string.egp),
                                        selectedCurrency
                                    )
                                    showCurrencyDialog = false
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.currency_updated),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currency == selectedCurrency,
                                onClick = {
                                    viewModel.updateCurrency(currency)
                                    viewModel.saveCurrency(currency)
                                    viewModel.getExchangeRate(  context.getString(R.string.egp), selectedCurrency)
                                    showCurrencyDialog = false
                                    Toast.makeText(context,   context.getString(R.string.currency_updated), Toast.LENGTH_SHORT).show()
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = currency,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f),
                                fontFamily = customFontFamily,
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text(stringResource(R.string.cancel), fontFamily = customFontFamily)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.large
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.confirm_logout),
                    style = MaterialTheme.typography.titleLarge, fontFamily = customFontFamily,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.are_you_sure_you_want_to_logout),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = customFontFamily,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                        navController.navigate(context.getString(R.string.login)) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text(stringResource(R.string.logout), fontFamily = customFontFamily) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel), fontFamily = customFontFamily)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.large
        )
    }
}

@Composable
fun SettingItem(
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface, fontFamily = customFontFamily,
            )
            if (value != null) {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontFamily = customFontFamily,
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = stringResource(R.string.arrow),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}