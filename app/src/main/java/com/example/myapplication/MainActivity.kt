package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.adapters.LokerAdapter
import com.example.myapplication.models.Loker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var lokerList: MutableList<Loker>
    private lateinit var adapter: LokerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lokerList = mutableListOf()
        adapter = LokerAdapter(lokerList)
        recyclerView.adapter = adapter


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




        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Users").child("Company")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lokerList.clear()
                for (companySnapshot in snapshot.children) {
                    val lokerSnapshot = companySnapshot.child("Loker")
                    for (jobSnapshot in lokerSnapshot.children) {
                        val job = jobSnapshot.getValue(Loker::class.java)
                        if (job != null) {
                            // Set jobId dengan key dari snapshot
                            job.jobId = jobSnapshot.key ?: ""
                            lokerList.add(job)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })


        adapter.onItemClick = { loker ->
            val intent = Intent(this, Isi::class.java)
            intent.putExtra("lokerDetail", loker)
            startActivity(intent)
        }

        adapter.onBookmarkClick = { loker ->
            saveToFirebase(loker)
        }

        val homeTextView: TextView = findViewById(R.id.myJobs)
        homeTextView.setOnClickListener {
            val intent = Intent(this, Bookmark::class.java)
            startActivity(intent)
        }

        val personTextView: TextView = findViewById(R.id.Person)
        personTextView.setOnClickListener {
            val intent = Intent(this, Person::class.java)
            startActivity(intent)
        }
    }

    private fun saveToFirebase(loker: Loker) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = FirebaseDatabase.getInstance().getReference("Users")
            .child("Person")
            .child(user.uid)
            .child("Bookmarks")

        // Cek apakah jobId sudah ada di Firebase
        userRef.orderByChild("jobId").equalTo(loker.jobId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Jika jobId sudah ada, tampilkan pesan bahwa data sudah dibookmark
                    Toast.makeText(this@MainActivity, "Loker sudah ada di My Jobs", Toast.LENGTH_SHORT).show()
                } else {
                    // Jika tidak ada, simpan loker ke Firebase
                    val bookmarkKey = userRef.push().key ?: return
                    userRef.child(bookmarkKey).setValue(loker)
                        .addOnSuccessListener {
                            Toast.makeText(this@MainActivity, "Loker berhasil disimpan di My Jobs", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(this@MainActivity, "Gagal menyimpan bookmark: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Gagal memeriksa bookmark: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}





