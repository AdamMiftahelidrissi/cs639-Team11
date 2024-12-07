package com.example.timeflex.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timeflex.repository.ClassRepository
import com.example.timeflex.repository.UserRepository
import kotlinx.coroutines.launch
import com.example.timeflex.data.User
import com.example.timeflex.data.Class
import com.example.timeflex.viewModel.SharedViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.timeflex.data.Attendance
import com.example.timeflex.data.AttendanceRecord
import com.example.timeflex.data.AttendanceStatus
import com.example.timeflex.repository.AttendanceRepository
import com.example.timeflex.ui.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AttendanceScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    classRepository: ClassRepository,
    attendanceRepository: AttendanceRepository
) {
    val selectedClass = sharedViewModel.selectedClass.collectAsState().value
    val studentsState = selectedClass?.let { classRepository.getStudentsForClass(it.id).collectAsState(initial = emptyList()) }
    val students = studentsState?.value ?: emptyList()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Track attendance status for each student name
    val attendanceState = remember(students) {
        mutableStateMapOf<String, AttendanceStatus>().apply {
            students.forEach { studentName ->
                this[studentName] = AttendanceStatus.ABSENT
            }
        }
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = "Attendance for ${selectedClass?.name ?: "Class"}",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message
            if (!errorMessage.isNullOrEmpty()) {
                LaunchedEffect(errorMessage) {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = errorMessage!!)
                        errorMessage = null // Clear after showing
                    }
                }
            }

            // Display the list of students
            if (students.isNotEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(students) { studentName ->
                        StudentAttendanceCard(
                            studentName = studentName,
                            attendanceStatus = attendanceState[studentName] ?: AttendanceStatus.ABSENT,
                            onAttendanceMarked = { status ->
                                attendanceState[studentName] = status
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val attendanceRecords = attendanceState.map { (studentName, status) ->
                                    AttendanceRecord(studentName = studentName, status = status)
                                }
                                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                val attendance = Attendance(
                                    classId = selectedClass?.id ?: "",
                                    date = LocalDate.now().toString(),
                                    submittedAt = System.currentTimeMillis(),
                                    submittedBy = userId,
                                    records = attendanceRecords
                                )
                                println(attendance)
                                attendanceRepository.submitAttendance(attendance)
                                snackbarHostState.showSnackbar("Attendance submitted successfully!")
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.HOME) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Failed to submit attendance: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Attendance")
                }
            } else {
                Text(
                    text = "No students enrolled in this class.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun StudentAttendanceCard(
    studentName: String,
    attendanceStatus: AttendanceStatus,
    onAttendanceMarked: (AttendanceStatus) -> Unit
) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = studentName,
                style = MaterialTheme.typography.bodyLarge
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onAttendanceMarked(AttendanceStatus.PRESENT) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (attendanceStatus == AttendanceStatus.PRESENT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Present")
                }
                Button(
                    onClick = { onAttendanceMarked(AttendanceStatus.ABSENT) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (attendanceStatus == AttendanceStatus.ABSENT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Absent")
                }
            }
        }
    }
}
