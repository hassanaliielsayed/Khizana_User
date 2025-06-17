package com.example.khizana_user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.khizana_user.presentation.nav.AppNavGraph
import com.example.khizana_user.presentation.order.view.OrdersScreen
import com.example.khizana_user.ui.theme.Khizana_UserTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.khizana_user.presentation.auth.viewmodel.AuthViewModel
import com.example.khizana_user.presentation.onboarding.OnboardingScreen
import androidx.compose.runtime.setValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Khizana_UserTheme {
                val navController = rememberNavController()
                AppNavGraph(navController)
//                var showOnboarding by remember { mutableStateOf(true) }
//
//                if (showOnboarding) {
//                    OnboardingScreen {
//                        showOnboarding = false
//                    }
//                }

            }
        }
    }
}
