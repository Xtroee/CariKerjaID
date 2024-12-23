package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapters.LokerAdapter
import com.example.myapplication.models.Loker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Bookmark : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LokerAdapter
    private lateinit var bookmarkList: MutableList<Loker>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        bookmarkList = mutableListOf()
        adapter = LokerAdapter(bookmarkList)
        recyclerView.adapter = adapter

        loadBookmarks()

        adapter.onDeleteClick = { loker ->
            deleteBookmark(loker)
        }

        // Navigasi ke halaman lain
        findViewById<TextView>(R.id.Messages).setOnClickListener {
            val intent = Intent(this, Messages::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.Home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.Person).setOnClickListener {
            val intent = Intent(this, Person::class.java)
            startActivity(intent)
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

    }

    private fun loadBookmarks() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = FirebaseDatabase.getInstance().getReference("Users")
            .child("Person")
            .child(user.uid)
            .child("Bookmarks")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookmarkList.clear()  // Clear previous data
                for (bookmarkSnapshot in snapshot.children) {
                    val bookmark = bookmarkSnapshot.getValue(Loker::class.java)
                    if (bookmark != null) {
                        bookmarkList.add(bookmark)
                    }
                }
                adapter.notifyDataSetChanged()  // Notify adapter of data change
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Bookmark, "Failed to load bookmarks: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        adapter.onItemClick = { loker ->
            val intent = Intent(this, Isi::class.java)
            intent.putExtra("lokerDetail", loker)
            startActivity(intent)
        }
    }



    private fun deleteBookmark(loker: Loker) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = FirebaseDatabase.getInstance().getReference("Users")
            .child("Person")
            .child(user.uid)
            .child("Bookmarks")

        userRef.orderByChild("jobId").equalTo(loker.jobId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Get the key of the bookmark to delete
                    val bookmarkKey = snapshot.children.first().key
                    if (bookmarkKey != null) {
                        userRef.child(bookmarkKey).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(this@Bookmark, "Loker telah dihapus di My Jobs", Toast.LENGTH_SHORT).show()
                                loadBookmarks()  // Reload the list after deletion
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(this@Bookmark, "Failed to delete bookmark: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Bookmark, "Failed to check bookmark: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

