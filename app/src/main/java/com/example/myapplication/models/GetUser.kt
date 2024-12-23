package com.example.myapplication.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

data class User(
    val uid: String,
    val name: String,
    val profileImageUrl: String?,
    val email: String?
)

class UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    // Save user to Firebase Realtime Database
    fun saveUser(user: FirebaseUser, onComplete: (Boolean, String?) -> Unit) {
        val userId = user.uid
        val userRef = database.getReference("Users").child("Person").child(userId)

        val userData = User(
            uid = user.uid,
            name = user.displayName ?: "User",
            profileImageUrl = user.photoUrl?.toString(),
            email = user.email
        )

        userRef.setValue(userData)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful, task.exception?.message)
            }
    }

    // Register user with email and password
    fun registerUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            // Save user data to Firebase Realtime Database
                            val userData = User(
                                uid = user.uid,
                                name = "User", // Default name
                                profileImageUrl = null,
                                email = user.email
                            )
                            val userRef = database.getReference("Users").child("Person").child(user.uid)
                            userRef.setValue(userData)
                                .addOnCompleteListener { saveTask ->
                                    onComplete(saveTask.isSuccessful, saveTask.exception?.message)
                                }
                        } else {
                            onComplete(false, "User is null after registration")
                        }
                    } else {
                        onComplete(false, task.exception?.message)
                    }
                }
        } else {
            onComplete(false, "Please fill in all fields!")
        }
    }


    // Login user with email and password
    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            // Check if user data exists in the "Person" node
                            val userRef = database.getReference("Users").child("Person").child(user.uid)
                            userRef.get().addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    val snapshot = dbTask.result
                                    if (snapshot.exists()) {
                                        onComplete(true, null) // User found in the "Person" node
                                    } else {
                                        onComplete(false, "User data not found in the database")
                                    }
                                } else {
                                    onComplete(false, "Error retrieving user data from the database")
                                }
                            }
                        } else {
                            onComplete(false, "User not found after login")
                        }
                    } else {
                        onComplete(false, task.exception?.message)
                    }
                }
        } else {
            onComplete(false, "Please enter email and password")
        }
    }

}

