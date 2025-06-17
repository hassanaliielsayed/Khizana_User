package com.example.khizana_user.presentation.splash.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.khizana_user.ui.theme.Khizana_UserTheme
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.example.khizana_user.MainActivity
import com.example.khizana_user.R
import com.example.khizana_user.presentation.onboarding.OnboardingActivity
import com.example.khizana_user.presentation.onboarding.OnboardingPageContent
import com.example.khizana_user.presentation.onboarding.OnboardingScreen
import com.example.khizana_user.utils.customFontFamily
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull


@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)


        if (isFirstTime) {
            setContent {
                Khizana_UserTheme {
                    SplashContent(
                        onFinish = {
                            sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
                            startActivity(Intent(this, OnboardingActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}


@Composable
fun SplashContent(
    onFinish: () -> Unit,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.spalsh_lottie_animation))

    LaunchedEffect(Unit) {
        delay(2000)
        onFinish()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LottieAnimation(
            composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = stringResource(R.string.project_name),
            fontSize = 32.sp,
            fontFamily = customFontFamily,
            fontWeight = FontWeight.ExtraLight,
            color = colorResource(id = R.color.dark_blue)
        )
    }
}
