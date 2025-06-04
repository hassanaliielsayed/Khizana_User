package com.example.khizana_user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.khizana_user.presentation.nav.AppNavGraph
import com.example.khizana_user.ui.theme.Khizana_UserTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Khizana_UserTheme {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}
