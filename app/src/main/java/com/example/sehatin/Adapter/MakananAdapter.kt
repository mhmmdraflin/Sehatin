package com.example.sehatin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sehatin.R
import com.google.android.material.card.MaterialCardView

// 1. Data Makanan (Sudah ditambahkan gambarUrl)
data class MakananSehat(
    val judul: String,
    val kalori: Int,
    val deskripsiLengkap: String,
    val gambarUrl: String
)

// 2. Adapter untuk RecyclerView
class MakananAdapter(
    private val listMakanan: List<MakananSehat>,
    private val onItemClick: (MakananSehat) -> Unit
) : RecyclerView.Adapter<MakananAdapter.MakananViewHolder>() {

    class MakananViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.tv_judul_makanan)
        val tvKalori: TextView = view.findViewById(R.id.tv_kalori_makanan)
        val ivGambar: ImageView = view.findViewById(R.id.iv_gambar_makanan) // Tambahkan inisialisasi gambar di sini agar lebih rapi
        val cardItem: MaterialCardView = view as MaterialCardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakananViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_informasi_makanan, parent, false)
        return MakananViewHolder(view)
    }

    override fun onBindViewHolder(holder: MakananViewHolder, position: Int) {
        val makanan = listMakanan[position]

        // Memasukkan teks
        holder.tvJudul.text = makanan.judul
        holder.tvKalori.text = "🔥 ${makanan.kalori} Kalori"

        // Memuat gambar dari Link URL menggunakan GLIDE
        Glide.with(holder.itemView.context)
            .load(makanan.gambarUrl)
            .centerCrop()
            .placeholder(R.drawable.logo_informasi_makanan) // Gambar sementara saat loading
            .into(holder.ivGambar)

        // Aksi ketika kartu diklik
        holder.cardItem.setOnClickListener {
            onItemClick(makanan)
        }
    }

    override fun getItemCount(): Int = listMakanan.size
}