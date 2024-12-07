package com.example.timeflex.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timeflex.ui.navigation.Routes
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top, // Aligns items from the top
        horizontalAlignment = Alignment.Start // Aligns all items to the start (left)
    ) {
        // Title
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Attendance Records Button
        TextButton(
            onClick = { navController.navigate(Routes.ATTENDANCE_RECORDS) },
        ) {
            Text(
                text = "Attendance Records",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Time Sheets Button
        TextButton(
            onClick = { navController.navigate(Routes.TIME_SHEETS) },
        ) {
            Text(
                text = "Time Sheets",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sign Out Button
        TextButton(
            onClick = {
                navController.navigate(Routes.LOGIN)
                FirebaseAuth.getInstance().signOut()
            },
        ) {
            Text(
                text = "Sign Out",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
