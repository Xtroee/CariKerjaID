package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.Loker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LokerAdapter(private val lokerList: List<Loker>) : RecyclerView.Adapter<LokerAdapter.LokerViewHolder>() {

    var onItemClick: ((Loker) -> Unit)? = null
    var onBookmarkClick: ((Loker) -> Unit)? = null
    var onDeleteClick: ((Loker) -> Unit)? = null  // Callback for delete button

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LokerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.info_loker, parent, false)
        return LokerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LokerViewHolder, position: Int) {
        val currentLoker = lokerList[position]

        holder.jenisPekerjaanTextView.text = currentLoker.jenis_pekerjaan
        holder.namaPerusahaanTextView.text = currentLoker.nama_perusahaan
        holder.jumlahLokerTextView.text = "Jumlah Loker: ${currentLoker.jumlah_loker}"

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentLoker)
        }

        holder.bookmarkButton.setOnClickListener {
            onBookmarkClick?.invoke(currentLoker)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick?.invoke(currentLoker)  // Invoke delete callback
        }
    }

    override fun getItemCount(): Int = lokerList.size

    class LokerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jenisPekerjaanTextView: TextView = itemView.findViewById(R.id.Jenis_Kerjaan)
        val namaPerusahaanTextView: TextView = itemView.findViewById(R.id.Nama_Perusahaan)
        val jumlahLokerTextView: TextView = itemView.findViewById(R.id.Kebutuhan)
        val bookmarkButton: TextView = itemView.findViewById(R.id.vk_Bookmark)
        val deleteButton: TextView = itemView.findViewById(R.id.Hapus)  // Delete button
    }
}


