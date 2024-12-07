package com.example.timeflex.repository

import com.example.timeflex.data.Attendance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.callbackFlow

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

    fun getAttendanceForClassAndDate(classId: String, date: String): Flow<List<Attendance>> = callbackFlow {
        val firestore = FirebaseFirestore.getInstance()

        // Query Firestore for attendance records matching the classId and date
        val query = firestore.collection("ATTENDANCE")
            .whereEqualTo("classId", classId)
            .whereEqualTo("date", date)

        val listenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error) // Propagate the error to the Flow
                println("Error fetching attendance records: ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot != null) {
                try {
                    val attendanceList = snapshot.documents.mapNotNull { document ->
                        document.toObject(Attendance::class.java)?.copy(id = document.id)
                    }
                    println("Fetched attendance records: $attendanceList")
                    trySend(attendanceList) // Emit the fetched attendance records
                } catch (e: Exception) {
                    close(e) // Propagate parsing exceptions
                }
            }
        }

        // Close the callbackFlow when canceled
        awaitClose { listenerRegistration.remove() }
    }

}