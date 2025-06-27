package com.example.khizana_user.presentation.splash.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.example.khizana_user.ui.theme.Khizana_UserTheme
import kotlinx.coroutines.delay
import androidx.compose.ui.layout.ContentScale
import androidx.core.view.WindowCompat
import com.example.khizana_user.MainActivity
import com.example.khizana_user.R
import com.example.khizana_user.presentation.onboarding.OnboardingActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.khizana_user.presentation.auth.viewmodel.AuthViewModel

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        val sharedPreferences = getSharedPreferences("khizana_prefs", Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)

        setContent {
            Khizana_UserTheme {
                val viewModel: AuthViewModel = hiltViewModel()
                val customer by viewModel.currentCustomer.collectAsStateWithLifecycle()
                val sharedPreferences = remember {
                    getSharedPreferences("khizana_prefs", Context.MODE_PRIVATE)
                }
                val isFirstTime = remember { sharedPreferences.getBoolean("isFirstTime", true) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashContent(
                        onFinish = {
                            when {
                                isFirstTime -> {
                                    sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
                                    startActivity(Intent(this, OnboardingActivity::class.java))
                                }
                                customer != null -> {
                                    startActivity(Intent(this, MainActivity::class.java))
                                }
                                else -> {
                                    startActivity(Intent(this, MainActivity::class.java))
                                }
                            }
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SplashContent(
    onFinish: () -> Unit,
) {

    LaunchedEffect(Unit) {
        delay(2000)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.white)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = stringResource(R.string.project_name),
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
