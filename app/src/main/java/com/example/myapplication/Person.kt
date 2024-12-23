package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Person : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)

        // Referensi ke TextView untuk email dan nama
        val emailTextView: TextView = findViewById(R.id.Email)
        val usernameTextView: TextView = findViewById(R.id.Username)

        // Ambil pengguna dari FirebaseAuth
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            // Set email pengguna ke TextView
            emailTextView.text = currentUser.email

            // Ambil nama pengguna dari Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("Users").child("Person").child(currentUser.uid)

            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    if (snapshot.exists()) {
                        // Ambil nama pengguna dari snapshot dan set ke TextView
                        val name = snapshot.child("name").getValue(String::class.java)
                        usernameTextView.text = name ?: "No name available"
                    } else {
                        usernameTextView.text = "User data not found"
                    }
                } else {
                    usernameTextView.text = "Error retrieving user data"
                }
            }
        } else {
            // Jika pengguna tidak ditemukan
            emailTextView.text = "No email available"
            usernameTextView.text = "No name available"
        }

        // Navigasi ke aktivitas lain
        val gantiUsername = findViewById<ConstraintLayout>(R.id.gantiUsername)
        gantiUsername.setOnClickListener {
            val intent = Intent(this, GantiUsername::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.myJobs).setOnClickListener {
            val intent = Intent(this, Bookmark::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.Home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.Messages).setOnClickListener {
            val intent = Intent(this, Messages::class.java)
            startActivity(intent)
        }

        // Handle logout functionality with confirmation dialog
        val logoutLayout = findViewById<ConstraintLayout>(R.id.keluar)
        logoutLayout.setOnClickListener {
            // Show a confirmation dialog before logging out
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    // Sign out the user
                    FirebaseAuth.getInstance().signOut()
                    // Show a toast message for logout
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    // Redirect to login screen or main activity
                    val intent = Intent(this, First::class.java) // Replace with your login activity
                    startActivity(intent)
                    finish() // Optionally finish the current activity
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss() // Close the dialog if the user cancels
                }
            val alert = builder.create()
            alert.show()
        }
    }

    // Override the back button to navigate to the previous screen
    override fun onBackPressed() {
        super.onBackPressed() // Calls the default back press behavior
        // You can add any additional code here if needed
    }
}
