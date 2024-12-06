package com.example.timeflex.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.timeflex.repository.ClassRepository
import com.example.timeflex.data.User
import com.example.timeflex.data.Class
import com.example.timeflex.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    userRepository: UserRepository,
    classRepository: ClassRepository
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var currentUser by remember { mutableStateOf<User?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Fetch user data
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotBlank()) {
            userRepository.getUser(
                userId = currentUserId,
                onSuccess = { user ->
                    currentUser = user
                },
                onFailure = { exception ->
                    errorMessage = "Error fetching user: ${exception.message}"
                }
            )
        } else {
            errorMessage = "User is not logged in."
        }
    }

    // Fetch classes for the instructor
    val classesFlow = remember(currentUser) {
        currentUser?.id?.let { instructorId ->
            classRepository.getClassesForInstructor(instructorId)
        } ?: flowOf(emptyList())
    }
    val classes = classesFlow.collectAsState(initial = emptyList()).value

    // Get the current day
    val currentDay = LocalDate.now().dayOfWeek.name

    // Split classes into "today's classes" and "other classes"
    val classesToday = classes.filter { classItem ->
        classItem.schedule.any { entry ->
            entry.day.equals(currentDay, ignoreCase = true)
        }
    }

    val otherClasses = classes.filterNot { it in classesToday }

    // Get the current date
    val currentDate = LocalDate.now()
    val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val month = currentDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val dayOfMonth = currentDate.dayOfMonth

    // Format the day with ordinal suffix
    val dayWithSuffix = when (dayOfMonth % 10) {
        1 -> if (dayOfMonth != 11) "$dayOfMonth" + "st" else "$dayOfMonth" + "th"
        2 -> if (dayOfMonth != 12) "$dayOfMonth" + "nd" else "$dayOfMonth" + "th"
        3 -> if (dayOfMonth != 13) "$dayOfMonth" + "rd" else "$dayOfMonth" + "th"
        else -> "$dayOfMonth" + "th"
    }

    // Combine to create the desired format
    val formattedDate = "$dayOfWeek, $month $dayWithSuffix"

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Display the current date at the top
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display classes
            if (currentUser != null) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Section for today's classes
                    if (classesToday.isNotEmpty()) {
                        item {
                            Text(
                                text = "Today's Classes",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(classesToday) { classItem ->
                            ClassCard(classItem, currentUser!!)
                        }
                    }

                    // Section for other classes
                    if (otherClasses.isNotEmpty()) {
                        item {
                            Text(
                                text = "Other Classes",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(otherClasses) { classItem ->
                            ClassCard(classItem, currentUser!!)
                        }
                    }
                }
            } else if (errorMessage == null) {
                Text(
                    text = "Loading user data...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Error Message
            if (!errorMessage.isNullOrEmpty()) {
                LaunchedEffect(errorMessage) {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = errorMessage!!)
                        errorMessage = null // Clear after showing
                    }
                }
            }
        }
    }
}

@Composable
fun ClassCard(classItem: Class, user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Class Name
            Text(
                text = "Class: ${classItem.name}",
                style = MaterialTheme.typography.bodyLarge
            )

            // Instructor
            Text(
                text = "Instructor: ${user.firstName} ${user.lastName}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Schedule
            if (classItem.schedule.isNotEmpty()) {
                Text(
                    text = "Schedule:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                classItem.schedule.forEach { entry ->
                    Text(
                        text = "${entry.day}: ${entry.startTime} - ${entry.endTime}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            } else {
                Text(
                    text = "No schedule available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

