package com.example.timeflex.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.timeflex.repository.UserRepository
import com.example.timeflex.ui.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.example.timeflex.data.User
import java.time.LocalDate
import java.util.Locale
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    userRepository: UserRepository
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (!currentUserId.isNullOrEmpty()) {
            user = userRepository.getUser(currentUserId)
        }
    }

    TopAppBar(
        title = {
            Text(
                text = when (currentRoute) {
                    Routes.HOME -> "Home"
                    Routes.CALENDER -> LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    Routes.CLOCK -> "Clock In/Out"
                    Routes.PROFILE -> user?.let { "${it.firstName} ${it.lastName}" } ?: "Profile"
                    else -> "TimeFlex"
                },
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            // Show a back button for non-home screens
            if (currentRoute !in listOf(Routes.HOME, Routes.CALENDER, Routes.CLOCK, Routes.PROFILE)) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
