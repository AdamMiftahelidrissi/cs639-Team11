package com.example.timeflex.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.timeflex.ui.components.AppBottomBar
import com.example.timeflex.ui.components.AppTopBar
import com.example.timeflex.ui.navigation.AppNavigation
import com.example.timeflex.ui.navigation.Routes
import com.example.timeflex.viewModel.SharedViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()

    Scaffold(
        topBar = {
            val currentDestination = navController.currentBackStackEntryAsState()?.value?.destination?.route
            println("Current top bar destination: $currentDestination")
            if (currentDestination != null && currentDestination !in listOf(
                    Routes.LOGIN, Routes.CREATE_ACCOUNT, Routes.FORGOT_PASSWORD)) {
                AppTopBar(navController)
            }
        },
        bottomBar = {
            val currentDestination = navController.currentBackStackEntryAsState()?.value?.destination?.route
            println("Current bottom bar destination: $currentDestination")
            if (currentDestination != null && currentDestination !in listOf(
                    Routes.LOGIN, Routes.CREATE_ACCOUNT, Routes.FORGOT_PASSWORD)) {
                AppBottomBar(navController)
            }
        }
    ) { innerPadding ->
        // Ensure AppNavigation is properly defined with all routes
        AppNavigation(
            navController = navController,
            sharedViewModel = sharedViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

