package com.example.khizana_user.presentation.auth.view

import android.widget.Toast
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
import androidx.compose.ui.text.TextStyle
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

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreeChecked by remember { mutableStateOf(false) }

    val state by viewModel.authState.collectAsStateWithLifecycle()
    val shopifyResult by viewModel.shopifyRegisterResult.collectAsStateWithLifecycle()

    LaunchedEffect(shopifyResult) {
        shopifyResult?.onFailure { error ->
            Toast.makeText(context, "Shopify Error: ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(state) {
        if (state is AuthState.VerificationEmailSent) {
            Toast.makeText(context,
                context.getString(R.string.verification_email_sent_please_check_your_inbox), Toast.LENGTH_LONG).show()
            viewModel.resetState()
            onRegisterSuccess()
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.sign_up), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, fontFamily = customFontFamily)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.name)) },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontFamily = customFontFamily)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.userEmail)) },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontFamily = customFontFamily)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text(stringResource(R.string.mobile)) },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontFamily = customFontFamily)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
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
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontFamily = customFontFamily)
            )

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = agreeChecked, onCheckedChange = { agreeChecked = it })
                Text(stringResource(R.string.i_agree_with),  fontFamily = customFontFamily)
                Text(stringResource(R.string.privacy), color = Color.White,fontFamily = customFontFamily, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.and),fontFamily = customFontFamily)
                Text(stringResource(R.string.policy), color = Color.White,fontFamily = customFontFamily, fontWeight = FontWeight.Bold)

            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (agreeChecked) {
                        viewModel.register(email.trim(), password.trim(), name.trim())
                    } else {
                        Toast.makeText(context,
                            context.getString(R.string.you_must_agree_to_the_policy_to_continue), Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .size(56.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }

            Spacer(Modifier.height(20.dp))

            Text(
                stringResource(R.string.already_have_an_account_sign_in),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onNavigateToLogin() },
                color = MaterialTheme.colorScheme.primary,
                fontFamily = customFontFamily
            )

            when (state) {
                is AuthState.Loading -> {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is AuthState.Error -> {
                    Spacer(Modifier.height(16.dp))
                    Text("Error: ${(state as AuthState.Error).message}", color = Color.Red, fontFamily = customFontFamily)
                }
                else -> Unit
            }
        }
    }
}
