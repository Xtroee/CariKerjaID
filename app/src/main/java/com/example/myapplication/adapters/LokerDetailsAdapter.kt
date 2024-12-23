package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.Loker

class AdapterDetails(private val lokerList: List<Loker>) : RecyclerView.Adapter<AdapterDetails.LokerDetailsViewHolder>() {

    // Membuat ViewHolder untuk layout item_loker_detail.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LokerDetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_isian_loker, parent, false)
        return LokerDetailsViewHolder(itemView)
    }

    // Mengikat data ke tampilan item
    override fun onBindViewHolder(holder: LokerDetailsViewHolder, position: Int) {
        val currentLoker = lokerList[position]
        holder.jenisPekerjaanTextView.text = currentLoker.jenis_pekerjaan
        holder.namaPerusahaanTextView.text = currentLoker.nama_perusahaan
        holder.lokasiTextView.text = currentLoker.lokasi
        holder.deskripsiTextView.text = currentLoker.deskripsi
        holder.typeTextView.text = currentLoker.type
        holder.bayaranTextView.text = "Bayaran: ${currentLoker.bayaran}"
    }

    // Mengembalikan jumlah item dalam list
    override fun getItemCount(): Int {
        return lokerList.size
    }

    // ViewHolder untuk item dengan semua detail
    class LokerDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jenisPekerjaanTextView: TextView = itemView.findViewById(R.id.Jenis_Kerjaan)
        val namaPerusahaanTextView: TextView = itemView.findViewById(R.id.Nama_Perusahaan)
        val lokasiTextView: TextView = itemView.findViewById(R.id.Location)
        val deskripsiTextView: TextView = itemView.findViewById(R.id.Deskripsi)
        val typeTextView: TextView = itemView.findViewById(R.id.Job_Type)
        val bayaranTextView: TextView = itemView.findViewById(R.id.Bayaran)
    }
}
