package com.example.timeflex.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timeflex.ui.navigation.Routes


@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Attendance Records",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Time Sheets Button
        TextButton(
            onClick = { navController.navigate(Routes.TIME_SHEETS) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Time Sheets",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
