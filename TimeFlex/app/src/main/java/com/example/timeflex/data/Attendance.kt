package com.example.timeflex.data

data class Attendance(
    val id: String = "", // Unique identifier for the attendance record (e.g., classId + date)
    val classId: String = "", // ID of the associated class
    val date: String = "", // Date in ISO format, e.g., "2024-12-05"
    val submittedBy: String = "", // ID of the user who submitted the attendance
    val submittedAt: Long = System.currentTimeMillis(), // Timestamp in milliseconds, set when submitted
    val records: List<AttendanceRecord> = emptyList() // List of student attendance records
)

data class AttendanceRecord(
    val studentName: String = "", // Unique identifier for the student
    val status: AttendanceStatus = AttendanceStatus.ABSENT // Attendance status
)

enum class AttendanceStatus {
    PRESENT,
    ABSENT
}


