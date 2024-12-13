package com.example.timeflex.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.timeflex.data.TimeSheet
import com.example.timeflex.repository.TimeSheetRepository
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSheetsScreen(
    navController: NavController,
    timeSheetRepository: TimeSheetRepository
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var allTimeSheets by remember { mutableStateOf<List<TimeSheet>>(emptyList()) }
    var filteredTimeSheets by remember { mutableStateOf<List<TimeSheet>>(emptyList()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // Fetch all timesheets for the logged-in user
    LaunchedEffect(Unit) {
        timeSheetRepository.getAllTimeSheetsForUser(userId).collect { timeSheets ->
            allTimeSheets = timeSheets
            filteredTimeSheets = timeSheets // Initially show all timesheets
        }
    }

    // Filter timesheets when the date is selected
    LaunchedEffect(selectedDate) {
        filteredTimeSheets = if (selectedDate != null) {
            val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
            val formattedSelectedDate = selectedDate!!.format(formatter)
            allTimeSheets.filter { it.date == formattedSelectedDate }
        } else {
            allTimeSheets
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Date Selector
        DatePickerSelector(
            label = "Select a Date",
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display Timesheets
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredTimeSheets) { timeSheet ->
                TimeSheetItem(timeSheet = timeSheet)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerSelector(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Initialize the calendar with the selected date if available
    selectedDate?.let {
        calendar.set(it.year, it.monthValue - 1, it.dayOfMonth)
    }

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Create a LocalDate from the selected date
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedButton(onClick = { datePickerDialog.show() }) {
        Text(text = selectedDate?.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) ?: label)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSheetItem(timeSheet: TimeSheet) {
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a") // Formatter for hours, minutes, and AM/PM

    // Function to parse Firestore time format
    fun parseFirestoreTime(time: String): String {
        return try {
            // Parse the time string and format it
            LocalTime.parse(time.substring(0, 8)).format(timeFormatter)
        } catch (e: Exception) {
            "--" // Return default if parsing fails
        }
    }

    // Format startTime and endTime
    val formattedStartTime = timeSheet.startTime.takeIf { it.isNotEmpty() }?.let { parseFirestoreTime(it) } ?: "--"
    val formattedEndTime = timeSheet.endTime.takeIf { it.isNotEmpty() }?.let { parseFirestoreTime(it) } ?: "--"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Date: ${timeSheet.date}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Start Time: $formattedStartTime", style = MaterialTheme.typography.bodyMedium)
            Text(text = "End Time: $formattedEndTime", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Total Time: ${timeSheet.totalTime}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}


