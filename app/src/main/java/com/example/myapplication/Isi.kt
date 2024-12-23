package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.models.Loker

class Isi : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_isian_loker)




        val lokerDetail: Loker? = intent.getParcelableExtra("lokerDetail")

        // Menampilkan data Loker di TextView
        lokerDetail?.let { loker ->
            findViewById<TextView>(R.id.Jenis_Kerjaan).text = loker.jenis_pekerjaan
            findViewById<TextView>(R.id.Nama_Perusahaan).text = loker.nama_perusahaan
            findViewById<TextView>(R.id.Location).text = loker.lokasi
            findViewById<TextView>(R.id.Deskripsi).text = loker.deskripsi
            findViewById<TextView>(R.id.Job_Type).text = loker.type
            findViewById<TextView>(R.id.Bayaran).text = "Bayaran: ${loker.bayaran}"
        }

        val backButton: TextView = findViewById(R.id.Back)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // Replace 'PreviousActivity' with the target activity
            startActivity(intent)
            finish() // Optionally finish the current activity to prevent going back to it
        }

        // In Isi activity (onCreate method):
        val applyButton = findViewById<Button>(R.id.Apply)
        applyButton.setOnClickListener {
            val intent = Intent(this, Apply::class.java)
            intent.putExtra("lokerDetail", lokerDetail)  // Pass the Loker object
            startActivity(intent)
        }




    }

}
