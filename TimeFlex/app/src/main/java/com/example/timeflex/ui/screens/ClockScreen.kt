package com.example.timeflex.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timeflex.data.TimeSheet
import com.example.timeflex.repository.TimeSheetRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockScreen(
    navController: NavController,
    timeSheetRepository: TimeSheetRepository
) {
    val scope = rememberCoroutineScope()

    // State variables for clock-in and clock-out times
    var clockInTime by remember { mutableStateOf<LocalTime?>(null) }
    var clockOutTime by remember { mutableStateOf<LocalTime?>(null) }
    var totalWorkedTime by remember { mutableStateOf("") }

    // Get today's date
    val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display today's date
        Text(
            text = todayDate,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Clock-in and Clock-out buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clock-in button
            Button(
                onClick = {
                    clockInTime = LocalTime.now()
                    clockOutTime = null // Reset clock-out when clock-in is pressed
                    totalWorkedTime = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Clock In")
            }
            // Clock-out button
            Button(
                onClick = {
                    scope.launch {
                        clockOutTime = LocalTime.now()
                        val duration = java.time.Duration.between(clockInTime, clockOutTime)
                        val hours = duration.toHours()
                        val minutes = duration.toMinutes() % 60
                        totalWorkedTime = "${hours}h ${minutes}m"
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        val newTimeSheet = TimeSheet(
                            userId = userId,
                            date = todayDate,
                            startTime = clockInTime.toString(),
                            endTime = clockOutTime.toString(),
                            totalTime = totalWorkedTime
                        )
                        timeSheetRepository.addTimeSheet(newTimeSheet)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Clock Out")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Display clock-in and clock-out times
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Clock-in time
            Text(
                text = if (clockInTime != null) {
                    "Clock In: ${clockInTime?.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
                } else {
                    "Clock In: --"
                },
                modifier = Modifier.weight(1f)
            )
            // Clock-out time
            Text(
                text = if (clockOutTime != null) {
                    "Clock Out: ${clockOutTime?.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
                } else {
                    "Clock Out: --"
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Display total worked hours and minutes
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (totalWorkedTime.isNotEmpty()) {
                "Total Time Worked: $totalWorkedTime"
            } else {
                "Total Time Worked: --"
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
