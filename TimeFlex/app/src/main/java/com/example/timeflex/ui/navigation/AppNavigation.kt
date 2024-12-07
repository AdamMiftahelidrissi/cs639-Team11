package com.example.timeflex.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.timeflex.repository.AttendanceRepository
import com.example.timeflex.repository.ClassRepository
import com.example.timeflex.repository.UserRepository
import com.example.timeflex.ui.screens.AttendanceScreen
import com.example.timeflex.ui.screens.CalenderScreen
import com.example.timeflex.ui.screens.ClockScreen
import com.example.timeflex.ui.screens.CreateAccountScreen
import com.example.timeflex.ui.screens.ForgotPasswordScreen
import com.example.timeflex.ui.screens.HomeScreen
import com.example.timeflex.ui.screens.LoginScreen
import com.example.timeflex.ui.screens.ProfileScreen
import com.example.timeflex.viewModel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    val firebaseAuth = FirebaseAuth.getInstance()

    // Mutable state to track whether the user is logged in
    var isLoggedIn by remember { mutableStateOf(firebaseAuth.currentUser != null) }

    // Listen for changes in the authentication state
    DisposableEffect(Unit) {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            isLoggedIn = auth.currentUser != null
            if (!isLoggedIn) {
                // Navigate to login screen when user logs out
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            }
        }
        firebaseAuth.addAuthStateListener(authListener)

        onDispose {
            firebaseAuth.removeAuthStateListener(authListener)
        }
    }

    // Define the navigation graph
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.HOME else Routes.LOGIN,
        modifier = modifier
    ) {
        composable(route = Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(route = Routes.CREATE_ACCOUNT) {
            CreateAccountScreen(navController = navController)
        }
        composable(route = Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(route = Routes.HOME) {
            HomeScreen(
                navController = navController,
                userRepository = UserRepository(),
                classRepository = ClassRepository(),
                sharedViewModel = sharedViewModel
            )
        }
        composable(route = Routes.ATTENDANCE) {
            AttendanceScreen(
                navController = navController,
                classRepository = ClassRepository(),
                attendanceRepository = AttendanceRepository(),
                sharedViewModel = sharedViewModel
            )
        }
        composable(route = Routes.CALENDER) {
            CalenderScreen(navController = navController)
        }
        composable(route = Routes.CLOCK) {
            ClockScreen(navController = navController)
        }
        composable(route = Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }
    }
}