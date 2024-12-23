package com.example.myapplication

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class GantiUsername: AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var saveButton: ShapeableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_username)

        editTextUsername = findViewById(R.id.ganti)
        saveButton = findViewById(R.id.input)

        saveButton.setOnClickListener {
            val newUsername = editTextUsername.text.toString()
            if (newUsername.isNotEmpty()) {
                saveNewUsername(newUsername)
            } else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveNewUsername(newUsername: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child("Person").child(userId)

            userRef.child("name").setValue(newUsername)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show()
                        finish() // Kembali ke halaman sebelumnya
                    } else {
                        Toast.makeText(this, "Failed to update username", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        }
    }
}
