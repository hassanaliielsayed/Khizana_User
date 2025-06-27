package com.example.khizana_user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.khizana_user.presentation.auth.viewmodel.AuthViewModel
import com.example.khizana_user.presentation.nav.AppNavGraph
import com.example.khizana_user.presentation.nav.ScreenRoute
import com.example.khizana_user.presentation.ui.theme.Khizana_UserTheme
import com.example.khizana_user.utils.ConnectivityObserver
import com.example.khizana_user.utils.NetworkConnectivityObserver
import com.example.khizana_user.utils.NoInternetConnectionScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val connectivityObserver = NetworkConnectivityObserver(applicationContext)

        setContent {
            Khizana_UserTheme {
                val navController = rememberNavController()

                val viewModel: AuthViewModel = hiltViewModel()
                val customer by viewModel.currentCustomer.collectAsStateWithLifecycle()

                val networkStatus by connectivityObserver.observe()
                    .collectAsStateWithLifecycle(initialValue = null)

                val lostInternetConnection = when (networkStatus) {
                    ConnectivityObserver.Status.Available -> false
                    ConnectivityObserver.Status.UnAvailable,
                    ConnectivityObserver.Status.Lost,
                    ConnectivityObserver.Status.Loosing -> true
                    null -> null
                }

                AppNavGraph(navController = navController)

                LaunchedEffect(customer) {
                    if (customer == null) {
                        navController.navigate(ScreenRoute.Login.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }

                Scaffold { innerPadding ->
                    when (lostInternetConnection) {
                        true -> NoInternetConnectionScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                        false -> AppNavGraph(
                            navController = navController
                        )
                        null -> {}
                    }
                }
            }
        }
    }
}
