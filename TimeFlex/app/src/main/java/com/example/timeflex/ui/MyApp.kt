package com.example.timeflex.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.timeflex.ui.components.AppBottomBar
import com.example.timeflex.ui.components.AppTopBar
import com.example.timeflex.ui.navigation.AppNavigation
import com.example.timeflex.ui.navigation.Routes

@Composable
fun MyApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            val currentDestination = navController.currentBackStackEntryAsState()?.value?.destination?.route
            println("Current destination: $currentDestination")
            if (currentDestination != null && currentDestination !in listOf(Routes.LOGIN, Routes.CREATE_ACCOUNT, Routes.FORGOT_PASSWORD)) {
                AppTopBar(navController)
            }
        },
        bottomBar = {
            val currentDestination = navController.currentBackStackEntryAsState()?.value?.destination?.route
            println("Current destination: $currentDestination")
            if (currentDestination != null && currentDestination !in listOf(Routes.LOGIN, Routes.CREATE_ACCOUNT, Routes.FORGOT_PASSWORD)) {
                AppBottomBar(navController)
            }
        }
    ) { innerPadding ->
        // Pass inner padding to the navigation content
        AppNavigation(modifier = Modifier.padding(innerPadding))
    }
}
