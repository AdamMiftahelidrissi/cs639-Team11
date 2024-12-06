package com.example.timeflex.repository

import com.example.timeflex.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

    fun getUser(
        userId: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("USERS")
            .document(userId)
            .get() // Get the document
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Convert the snapshot to a User object
                    val user = documentSnapshot.toObject(User::class.java)
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onFailure(Exception("User data is null"))
                    }
                } else {
                    onFailure(Exception("User does not exist"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
