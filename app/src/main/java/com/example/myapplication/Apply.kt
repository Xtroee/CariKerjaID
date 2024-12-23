package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.models.Loker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Apply : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var lokerDetail: Loker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply)

        // Inisialisasi Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        // Retrieve the passed Loker object
        lokerDetail = intent.getParcelableExtra("lokerDetail")!!

        // Set up UI to display Loker details if needed
        // For example:
        // findViewById<TextView>(R.id.JobTypeTextView).text = lokerDetail.jenis_pekerjaan

        val uploadLayout = findViewById<View>(R.id.uploadLayout)
        uploadLayout.setOnClickListener {
            openFilePicker()
        }

        val driveLayout = findViewById<View>(R.id.driveLayout)
        driveLayout.setOnClickListener {
            openDrivePicker()
        }
    }

    // Method to handle file upload and save both file and Loker data to Firebase
    private fun saveFileToFirebase(fileName: String, fileUri: String, source: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userUid = currentUser.uid
        val fileData = mapOf(
            "name" to fileName,
            "uri" to fileUri,
            "source" to source,
            "timestamp" to System.currentTimeMillis(),
            "lokerDetail" to lokerDetail // Save Loker details here
        )

        database.child("Users").child("Person").child(userUid).child("Apply").push().setValue(fileData)
            .addOnSuccessListener {
                Toast.makeText(this, "File data saved to Firebase", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save file data: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firebase", "Error saving file data", e)
            }
    }

    // Open file picker to upload files
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(Intent.createChooser(intent, "Select a File"), REQUEST_CODE_UPLOAD)
    }

    // Open Google Drive file picker
    private fun openDrivePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/*"
        startActivityForResult(intent, REQUEST_CODE_DRIVE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val uri: Uri? = data.data
            when (requestCode) {
                REQUEST_CODE_UPLOAD -> {
                    uri?.let {
                        handleFileUpload(it)
                    }
                }
                REQUEST_CODE_DRIVE -> {
                    uri?.let {
                        handleDriveFile(it)
                    }
                }
            }
        }
    }

    private fun handleFileUpload(uri: Uri) {
        val fileName = getFileName(uri)
        saveFileToFirebase(fileName, uri.toString(), "upload")
        Toast.makeText(this, "Uploaded: $fileName", Toast.LENGTH_SHORT).show()
    }

    private fun handleDriveFile(uri: Uri) {
        val fileName = getFileName(uri)
        saveFileToFirebase(fileName, uri.toString(), "drive")
        Toast.makeText(this, "Added from Drive: $fileName", Toast.LENGTH_SHORT).show()
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "Unknown"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    companion object {
        const val REQUEST_CODE_UPLOAD = 1
        const val REQUEST_CODE_DRIVE = 2
    }
}

