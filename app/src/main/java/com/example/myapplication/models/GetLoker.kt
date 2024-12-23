package com.example.myapplication.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// Data class for Loker
import android.os.Parcel
import android.os.Parcelable

data class Loker(

    var jobId: String = "",  // Menambahkan jobId sebagai identifier unik
    val jenis_pekerjaan: String = "",
    val nama_perusahaan: String = "",
    val lokasi: String = "",
    val type: String = "",
    val deskripsi: String = "",
    val bayaran: Int = 0,
    val jumlah_loker: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(jobId)  // Menyimpan jobId
        parcel.writeString(jenis_pekerjaan)
        parcel.writeString(nama_perusahaan)
        parcel.writeString(lokasi)
        parcel.writeString(type)
        parcel.writeString(deskripsi)
        parcel.writeInt(bayaran)
        parcel.writeInt(jumlah_loker)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Loker> {
        override fun createFromParcel(parcel: Parcel): Loker {
            return Loker(parcel)
        }

        override fun newArray(size: Int): Array<Loker?> {
            return arrayOfNulls(size)
        }
    }
}


