package com.example.khizana_user.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.khizana_user.MainActivity
import com.example.khizana_user.R
import com.example.khizana_user.presentation.AppLogo
import com.example.khizana_user.presentation.category.view.ui.theme.Khizana_UserTheme
import com.example.khizana_user.utils.customFontFamily
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch


class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Khizana_UserTheme {
                OnboardingScreen(
                    onFinish = {
                        val sharedPreferences = getSharedPreferences("khizana_prefs", MODE_PRIVATE)
                        sharedPreferences.edit().putBoolean("isFirstTime", false).apply()

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_one),
            description = stringResource(R.string.onboarding_description_one),
            lottieRes = R.raw.spalsh_lottie_animation
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_two),
            description = stringResource(R.string.onboarding_tdescription_two),
            imageRes = R.drawable.onboarding_bg1
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_three),
            description = stringResource(R.string.onboarding_description_three),
            imageRes = R.drawable.onboarding_bg3
        )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.white)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TopAppBar(
            title = {
                AppLogo()
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(R.color.white),
                titleContentColor = colorResource(R.color.black)
            )
        )

        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(pages[page])
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.padding(16.dp),
                activeColor = colorResource(R.color.dark_blue),
                inactiveColor = colorResource(R.color.light_gray),
                indicatorWidth = 12.dp,
                spacing = 8.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage < pages.lastIndex) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(pages.lastIndex)
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            stringResource(R.string.skip),
                            color = colorResource(R.color.text_secondary),
                            fontFamily = customFontFamily,
                            fontSize = 18.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage == pages.lastIndex) {
                            onFinish()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(if (pagerState.currentPage < pages.lastIndex) 0.7f else 1f),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.dark_blue),
                        contentColor = colorResource(R.color.black)
                    )
                ) {
                    Text(
                        if (pagerState.currentPage == pages.lastIndex) stringResource(R.string.get_started) else stringResource(
                            R.string.next
                        ),
                        fontFamily = customFontFamily,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        page.lottieRes?.let { lottie ->
            LottieAnimation(
                composition = rememberLottieComposition(LottieCompositionSpec.RawRes(lottie)).value,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
            )
        }

        page.imageRes?.let { image ->
            Image(
                painter = painterResource(id = image),
                contentDescription = page.title,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = page.title,
            fontSize = 28.sp,
            color = colorResource(R.color.dark_blue),
            fontFamily = customFontFamily,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 16.sp,
            color = colorResource(R.color.text_secondary),
            fontFamily = customFontFamily,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val lottieRes: Int? = null,
    val imageRes: Int? = null
)

