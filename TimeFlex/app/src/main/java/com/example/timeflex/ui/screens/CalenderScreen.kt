package com.example.timeflex.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import com.example.timeflex.data.Class
import com.example.timeflex.repository.ClassRepository
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalenderScreen(
    navController: NavController,
    classRepository: ClassRepository
) {
    var classes by remember { mutableStateOf<List<Class>>(emptyList()) }

    LaunchedEffect(Unit) {
        classRepository.getAllClasses().collect { newClasses ->
            classes = newClasses
        }
    }

    val currentMonth = YearMonth.now()
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7 // Adjust to match grid layout
    val today = LocalDate.now()
    val dayNames = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Grid for days
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    dayNames.forEach { dayName ->
                        Text(
                            text = dayName.substring(0, 3), // Show only first 3 letters (Sun, Mon...)
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f).padding(bottom = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Fill grid with days of the month
            for (weekStart in 0 until (daysInMonth + firstDayOfMonth) step 7) {
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (dayOffset in 0..6) {
                            val dayOfMonth = weekStart + dayOffset - firstDayOfMonth + 1
                            val dayOfWeek = dayNames[dayOffset] // Map day offset to day name

                            if (dayOfMonth in 1..daysInMonth) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f) // Allows equal distribution of width among cells in the row
                                        .padding(4.dp) // Adds space around the box
                                        .height(100.dp)
                                        .background(
                                            color = if (dayOfMonth == today.dayOfMonth) Color.Gray else Color.LightGray,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            // Navigate or handle click
                                        }
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Top,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .wrapContentHeight() // Allow vertical expansion
                                    ) {
                                        // Display the day number
                                        Text(
                                            text = dayOfMonth.toString(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                        // Filter and display classes for the day of the week
                                        val dayClasses = classes.flatMap { classItem ->
                                            classItem.schedule.filter { it.day == dayOfWeek }
                                                .map {
                                                    classItem.name
                                                }
                                        }

                                        dayClasses.forEach { className ->
                                            Text(
                                                text = className,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(top = 4.dp),
                                                maxLines = 1, // Avoid text overflow
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
