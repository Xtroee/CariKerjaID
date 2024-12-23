package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class First : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.first)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mySignView: TextView = findViewById(R.id.Signin)

        mySignView.setOnClickListener {
            val intent = Intent(this, SignInView::class.java)
            startActivity(intent)
        }

        val mySignUpView: TextView = findViewById(R.id.Create)

        mySignUpView.setOnClickListener {
            val intent = Intent(this, SignUpView::class.java)
            startActivity(intent)
        }
    }

}