package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Messages : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messages)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referensi ke TextView untuk nama
        val usernameTextView: TextView = findViewById(R.id.Username)

        // Ambil pengguna dari FirebaseAuth
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
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
            usernameTextView.text = "No name available"
        }


        // Navigasi ke Bookmark Activity
        val homeTextView: TextView = findViewById(R.id.myJobs)
        homeTextView.setOnClickListener {
            val intent = Intent(this, Bookmark::class.java)
            startActivity(intent)
        }

        // Navigasi ke Bookmark Activity
        val messagesTextView: TextView = findViewById(R.id.Home)
        messagesTextView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // Navigasi ke Bookmark Activity
        val personTextView: TextView = findViewById(R.id.Person)
        personTextView.setOnClickListener {
            val intent = Intent(this, Person::class.java)
            startActivity(intent)
        }
    }
}