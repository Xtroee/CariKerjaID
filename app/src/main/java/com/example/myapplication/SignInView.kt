package com.example.myapplication

import com.example.myapplication.models.UserRepository
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class SignInView : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_view)

        // Initialize FirebaseAuth
        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()

        val emailField = findViewById<EditText>(R.id.editEmail)
        val passwordField = findViewById<EditText>(R.id.editPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val googleSignInButton = findViewById<Button>(R.id.btnGoogle)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id)) // Ensure correct client_id in strings.xml
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up Google Sign-In button
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signInWithEmailPassword(email, password)
        }

        val myJobsTextView: TextView = findViewById(R.id.PindahDaftar)
        myJobsTextView.setOnClickListener {
            val intent = Intent(this, First::class.java)
            startActivity(intent)
        }
    }

    // Function to handle Google Sign-In
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // Handle the result of the Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { authResult ->
                    if (authResult.isSuccessful) {
                        // Sign-in successful
                        Toast.makeText(this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java) // Replace with your main activity
                        startActivity(intent)
                        finish()
                    } else {
                        // Sign-in failed
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: ApiException) {
            // Google Sign-In failed
            Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to handle email and password login
    private fun signInWithEmailPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        // Cek apakah user ada di node "Person" di Realtime Database
                        val userRef = FirebaseDatabase.getInstance().getReference("Users").child("Person").child(user.uid)
                        userRef.get().addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                val snapshot = dbTask.result
                                if (snapshot.exists()) {
                                    // Login berhasil dan user ditemukan di node "Person"
                                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java) // Ganti dengan activity utama
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // Jika user tidak ditemukan di node "Person"
                                    Toast.makeText(this, "User not found in the system.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Error saat mengambil data user dari database
                                Toast.makeText(this, "Error fetching user data: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    // Login gagal
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
