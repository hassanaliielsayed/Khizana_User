package com.example.khizana_user.presentation.auth.view

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.khizana_user.R
import com.example.khizana_user.presentation.auth.viewmodel.AuthViewModel
import com.example.khizana_user.utils.AuthState
import com.example.khizana_user.utils.customFontFamily
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val customer by viewModel.currentCustomer.collectAsStateWithLifecycle()
    val state by viewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val googleSignInClient: GoogleSignInClient by remember {
        mutableStateOf(
            GoogleSignIn.getClient(
                context,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("864102683705-a6u9dmg1vb8ep00be4ndvda2c9n57obt.apps.googleusercontent.com")
                    .requestEmail()
                    .build()
            )
        )
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.loginWithGoogle(idToken)
            } else {
                Log.e("LoginScreen", "Google Sign-In failed: No ID token")
            }
        } catch (e: ApiException) {
            Log.e("LoginScreen", "Google Sign-In failed: ${e.message}", e)
        }
    }

    LaunchedEffect(state, customer) {
        if (state is AuthState.Success && customer != null) {
            viewModel.resetState()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEEF2F3),
                        Color(0xFF8E9EAB)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(R.string.login), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray,  fontFamily = customFontFamily)
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.userEmail), fontFamily = customFontFamily) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password), fontFamily = customFontFamily) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            )

            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.forgot_your_password),
                fontFamily = customFontFamily,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        if (email.isNotBlank()) {
                            viewModel.resetPassword(email)
                            Toast.makeText(
                                context,
                                context.getString(R.string.reset_email_sent_if_account_exists),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Please enter your email above first.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { viewModel.login(email.trim(), password.trim()) },
                modifier = Modifier
                    .align(Alignment.End)
                    .size(56.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(stringResource(R.string.sign_in_with_google), color = Color.DarkGray, fontFamily = customFontFamily)
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = { viewModel.loginAsGuest() }) {
                Text(stringResource(R.string.continue_as_guest), color = MaterialTheme.colorScheme.primary,  fontFamily = customFontFamily)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                stringResource(R.string.don_t_have_an_account_sign_up),
                modifier = Modifier.clickable { onNavigateToRegister() },
                color = MaterialTheme.colorScheme.primary,
                fontFamily = customFontFamily
            )

            when (state) {
                is AuthState.Loading -> {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
                is AuthState.Error -> {
                    Spacer(Modifier.height(16.dp))
                    Text("Error: ${(state as AuthState.Error).message}", color = Color.Red, fontFamily = customFontFamily)
                }
                else -> {}
            }
        }
    }
}
