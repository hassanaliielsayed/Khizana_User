package com.example.khizana_user.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khizana_user.MainActivity
import com.example.khizana_user.R
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
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}


@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            title = "Welcome to Khizana App",
            description = "Discover amazing products from various vendors.",
            imageRes = R.drawable.onboarding_bg1
        ),
        OnboardingPage(
            title = "Easy Shopping",
            description = "Add to cart and complete your shopping seamlessly.",
            imageRes = R.drawable.onboarding_bg2
        ),
        OnboardingPage(
            title = "Fast Delivery",
            description = "Your orders delivered fast and safe.",
            imageRes = R.drawable.onboarding_bg3
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.white)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
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
                            "Skip",
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
                        backgroundColor = colorResource(R.color.dark_blue),
                        contentColor = colorResource(R.color.black)
                    )
                ) {
                    Text(
                        if (pagerState.currentPage == pages.lastIndex) "Get Started" else "Next",
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
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = page.title,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit
        )

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
    val imageRes: Int
)
