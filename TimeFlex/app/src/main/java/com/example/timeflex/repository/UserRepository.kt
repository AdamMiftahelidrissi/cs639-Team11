package com.example.timeflex.repository

import com.example.timeflex.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun addUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val currentUser = firebaseAuth.currentUser

        // Ensure the user is authenticated
        if (currentUser == null) {
            onFailure(Exception("User is not authenticated. Cannot add user to Firestore."))
            return
        }

        val userId = currentUser.uid // Get the authenticated user's UID

        // Save the user data with UID as the document ID
        firestore.collection("USERS")
            .document(userId) // Use UID as the document ID
            .set(user.copy(id = userId)) // Ensure the user object includes the UID
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    suspend fun getUser(userId: String): User {
        return try {
            val documentSnapshot = firestore.collection("USERS")
                .document(userId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(User::class.java)
                    ?: throw Exception("User data is null")
            } else {
                throw Exception("User does not exist")
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch user: ${e.message}", e)
        }
    }
}
