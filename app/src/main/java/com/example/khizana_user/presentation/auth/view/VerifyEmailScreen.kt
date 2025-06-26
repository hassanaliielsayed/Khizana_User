package com.example.khizana_user.presentation.auth.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.khizana_user.R
import com.example.khizana_user.presentation.auth.viewmodel.AuthViewModel
import com.example.khizana_user.utils.AuthState
import com.example.khizana_user.utils.customFontFamily
import kotlinx.coroutines.delay

@Composable
fun VerifyEmailScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginComplete: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current

    val email by viewModel.currentUserEmail.collectAsStateWithLifecycle()
    val isVerified by viewModel.isEmailVerified.collectAsStateWithLifecycle()
    val state by viewModel.authState.collectAsStateWithLifecycle()

    var canResend by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(60) }
    var stopPolling by remember { mutableStateOf(false) }
    var timeoutReached by remember { mutableStateOf(false) }

    // Countdown timer for resend
    LaunchedEffect(key1 = canResend) {
        if (!canResend) {
            for (i in 60 downTo 1) {
                delay(1000)
                timeLeft = i - 1
            }
            canResend = true
        }
    }

    // Poll every 3 seconds to detect if email is verified
    LaunchedEffect(key1 = stopPolling, key2 = timeoutReached) {
        if (!stopPolling && !timeoutReached) {
            repeat(40) { i -> // 40 * 3s = 2 minutes timeout
                Log.d("VerifyEmailScreen", "Polling... attempt=${i + 1}")
                viewModel.reloadUser()
                delay(3000)
                if (viewModel.isEmailVerified.value) {
                    Log.d("VerifyEmailScreen", "Email is verified, stopping polling.")
                    stopPolling = true
                    return@repeat
                }
            }
            timeoutReached = true
            Log.w("VerifyEmailScreen", "Email verification polling timed out.")
        }
    }

    // Handle verification state
    LaunchedEffect(isVerified) {
        if (isVerified && !stopPolling) {
            Log.d("VerifyEmailScreen", "LaunchedEffect: email verified, navigating...")
            Toast.makeText(context,
                context.getString(R.string.email_verified_logging_you_in), Toast.LENGTH_SHORT).show()
            stopPolling = true
            viewModel.resetState()
            onLoginComplete()
        }
    }

    // Handle errors
    LaunchedEffect(state) {
        if (state is AuthState.Error) {
            val errorMsg = (state as AuthState.Error).message ?: "Unknown error"
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    // UI layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.verify_your_email), style = MaterialTheme.typography.headlineSmall,fontFamily = customFontFamily)
        Spacer(Modifier.height(16.dp))

        Text(
            stringResource(
                R.string.we_ve_sent_an_email_to_please_verify_it_before_continuing,
                email ?: stringResource(R.string.your_account)
            ), fontFamily = customFontFamily)
        Spacer(Modifier.height(16.dp))

        if (canResend) {
            Text(
                stringResource(R.string.resend_email),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    viewModel.sendEmailVerification()
                    Toast.makeText(context,
                        context.getString(R.string.verification_email_resent), Toast.LENGTH_SHORT).show()
                    canResend = false
                    timeLeft = 60
                },
                fontFamily = customFontFamily
            )
        } else {
            Text(stringResource(R.string.you_can_resend_in_seconds, timeLeft) , fontFamily = customFontFamily)
        }

        Spacer(Modifier.height(24.dp))

        if (state is AuthState.Loading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
        }

        if (timeoutReached) {
            Text(stringResource(R.string.verification_timeout_please_try_again), color = MaterialTheme.colorScheme.error, fontFamily = customFontFamily)
            Spacer(Modifier.height(16.dp))
        }

        TextButton(onClick = onBackToLogin) {
            Text(stringResource(R.string.back_to_login), fontFamily = customFontFamily)
        }
    }
}
