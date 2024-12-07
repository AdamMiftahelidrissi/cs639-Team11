package com.example.timeflex.repository

import com.example.timeflex.data.Attendance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AttendanceRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun submitAttendance(attendance: Attendance) {
        try {
            // Query Firestore to find a document with the same classId and date
            val querySnapshot = firestore.collection("ATTENDANCE")
                .whereEqualTo("classId", attendance.classId)
                .whereEqualTo("date", attendance.date)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                // If a document already exists, update it
                val existingDocument = querySnapshot.documents.first()
                val documentId = existingDocument.id

                firestore.collection("ATTENDANCE")
                    .document(documentId)
                    .update(
                        mapOf(
                            "records" to attendance.records,
                            "submittedAt" to attendance.submittedAt
                        )
                    )
                    .await()
            } else {
                // If no document exists, create a new one
                val documentId = firestore.collection("ATTENDANCE").document().id // Generate a document ID
                val attendanceWithId = attendance.copy(id = documentId) // Create a copy with the ID set

                firestore.collection("ATTENDANCE")
                    .document(documentId)
                    .set(attendanceWithId)
                    .await()
            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., network issues, permission errors)
            throw Exception("Failed to submit attendance: ${e.message}")
        }
    }
}