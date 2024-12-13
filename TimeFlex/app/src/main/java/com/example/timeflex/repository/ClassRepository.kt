package com.example.timeflex.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import com.google.firebase.firestore.ktx.toObject
import com.example.timeflex.data.Class
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class ClassRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getAllClasses(): Flow<List<Class>> = callbackFlow {
        val listenerRegistration = firestore.collection("CLASSES")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val classList = snapshot?.documents?.mapNotNull { it.toObject<Class>() } ?: emptyList()
                trySend(classList)
            }

        awaitClose { listenerRegistration.remove() }
    }

    fun getClassesForInstructor(instructorId: String): Flow<List<Class>> = callbackFlow {
        val listenerRegistration = firestore.collection("CLASSES")
            .whereEqualTo("instructorId", instructorId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val classList = snapshot?.documents?.mapNotNull { it.toObject<Class>() } ?: emptyList()
                trySend(classList)
            }

        awaitClose { listenerRegistration.remove() }
    }

    fun getStudentsForClass(classId: String): Flow<List<String>> = callbackFlow {
        val firestore = FirebaseFirestore.getInstance()
        val classDocumentRef = firestore.collection("CLASSES").document(classId)

        val listener = classDocumentRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error) // Emit an error and close the flow
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val students = snapshot.get("students") as? List<String> ?: emptyList()
                trySend(students).isSuccess // Emit the list of students
            } else {
                trySend(emptyList()).isSuccess // Emit an empty list if the document doesn't exist
            }
        }

        // Remove the listener when the flow is closed
        awaitClose { listener.remove() }
    }

}