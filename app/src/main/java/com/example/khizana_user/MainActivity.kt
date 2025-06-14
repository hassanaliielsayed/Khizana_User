package com.example.khizana_user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.khizana_user.presentation.nav.AppNavGraph
import com.example.khizana_user.presentation.order.view.OrdersScreen
import com.example.khizana_user.ui.theme.Khizana_UserTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.khizana_user.presentation.auth.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Khizana_UserTheme {
                val navController = rememberNavController()
                AppNavGraph(navController)

//                val authViewModel = hiltViewModel<AuthViewModel>()
//                val customer = authViewModel.currentCustomer.collectAsStateWithLifecycle().value
//                if (customer != null)
//                    OrdersScreen(customerId = 7858653888625)
            }
        }
    }
}
