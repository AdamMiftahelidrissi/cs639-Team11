package com.example.timeflex.data

data class Class(
    val id: String = "",
    val instructorId: String = "",
    val name: String = "",
    val schedule: List<ScheduleEntry> = emptyList() // A structured schedule
)

data class ScheduleEntry(
    val day: String = "",         // E.g., "Monday", "Tuesday"
    val startTime: String = "",   // E.g., "10:00 AM"
    val endTime: String = ""      // E.g., "11:00 AM"
)

