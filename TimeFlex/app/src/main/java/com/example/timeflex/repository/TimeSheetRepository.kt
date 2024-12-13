package com.example.timeflex.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import com.google.firebase.firestore.ktx.toObject
import com.example.timeflex.data.Class
import com.example.timeflex.data.TimeSheet
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.sql.Time

class TimeSheetRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun addTimeSheet(timeSheet: TimeSheet) {
        val documentId = firestore.collection("TIMESHEETS").document().id // Generate a document ID
        val timeSheetWithId = timeSheet.copy(id = documentId) // Create a copy with the ID set

        firestore.collection("TIMESHEETS")
            .document(documentId)
            .set(timeSheetWithId)
            .await()
    }

    fun getAllTimeSheetsForUser(userId: String): Flow<List<TimeSheet>> = callbackFlow {
        val listenerRegistration = firestore.collection("TIMESHEETS")
            .whereEqualTo("userId", userId) // Filter by userId
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val timeSheetList = snapshot?.documents?.mapNotNull { it.toObject<TimeSheet>() } ?: emptyList()
                trySend(timeSheetList)
            }

        awaitClose { listenerRegistration.remove() }
    }
}