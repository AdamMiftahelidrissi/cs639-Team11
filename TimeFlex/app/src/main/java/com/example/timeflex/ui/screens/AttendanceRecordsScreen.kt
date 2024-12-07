package com.example.timeflex.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.timeflex.repository.AttendanceRepository
import com.example.timeflex.repository.ClassRepository
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.timeflex.data.AttendanceRecord
import com.example.timeflex.data.AttendanceStatus
import com.example.timeflex.data.User
import com.example.timeflex.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import com.example.timeflex.data.Class

@Composable
fun AttendanceRecordsScreen(
    navController: NavController,
    userRepository: UserRepository,
    attendanceRepository: AttendanceRepository,
    classRepository: ClassRepository
) {
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (!currentUserId.isNullOrEmpty()) {
            user = userRepository.getUser(currentUserId)
        }
    }

    var selectedClass by remember { mutableStateOf<Class?>(null) }
    var classes by remember { mutableStateOf<List<Class>>(emptyList()) }
    var selectedDate by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(user) {
        if (user != null) {
            scope.launch {
                classRepository.getClassesForInstructor(user!!.id)
                    .collect { classList ->
                        classes = classList // Ensure you're assigning the list of Class objects directly
                    }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Dropdown for selecting a class
        Text("Select Class", style = MaterialTheme.typography.bodyMedium)
        DropdownForClasses(
            selectedOption = selectedClass,
            options = classes,
            onOptionSelected = { selectedClass = it },
            label = "Class"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date Picker
        if (selectedClass != null) {
            DatePickerSelector(
                label = "Select a Date",
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Display the attendance records or an empty message
        if (selectedClass != null && selectedDate != null) {
            val attendanceRecords = remember { mutableStateOf<List<AttendanceRecord>>(emptyList()) }

            LaunchedEffect(selectedClass, selectedDate) {
                scope.launch {
                    attendanceRepository.getAttendanceForClassAndDate(
                        classId = selectedClass!!.id,
                        date = selectedDate!!
                    ).collect { attendanceList ->
                        // Flatten the attendance records from the attendance list
                        attendanceRecords.value = attendanceList.flatMap { it.records }
                    }
                }
            }

            if (attendanceRecords.value.isEmpty()) {
                Text("No records found for the selected class and date.", style = MaterialTheme.typography.bodySmall)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(attendanceRecords.value) { record ->
                        AttendanceRecordCard(record)
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownForClasses(
    selectedOption: Class?,
    options: List<Class>,
    onOptionSelected: (Class) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(text = label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown Icon")
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.name) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun DatePickerSelector(
    label: String,
    selectedDate: String?,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Initialize the calendar with the selected date if available
    selectedDate?.let {
        val parts = it.split("-")
        if (parts.size == 3) {
            calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
        }
    }

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Format the selected date as YYYY-MM-DD
            val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDateSelected(date)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    println("Selected Date: $selectedDate")
    OutlinedButton(onClick = { datePickerDialog.show() }) {
        Text(text = selectedDate ?: label)
    }
}

@Composable
fun AttendanceRecordCard(record: AttendanceRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = record.studentName,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = record.status.name,
                style = MaterialTheme.typography.bodyMedium,
                color = if (record.status == AttendanceStatus.PRESENT) Color.Green else Color.Red
            )
        }
    }
}
