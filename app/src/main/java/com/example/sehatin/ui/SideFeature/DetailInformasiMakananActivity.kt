package com.example.sehatin.ui.SideFeature

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.sehatin.R
import com.example.sehatin.databinding.ActivityDetailInformasiMakananBinding

class DetailInformasiMakananActivity : AppCompatActivity() {

    // 1. Deklarasi ViewBinding
    private lateinit var binding: ActivityDetailInformasiMakananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Inisialisasi ViewBinding
        binding = ActivityDetailInformasiMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur jarak aman layar (Edge-to-Edge) menggunakan binding.root
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // FUNGSI TOMBOL KEMBALI
        binding.btnBack.setOnClickListener {
            finish()
        }

        // MENERIMA DATA DARI HALAMAN SEBELUMNYA
        val judulMakanan = intent.getStringExtra("EXTRA_JUDUL") ?: "Makanan Sehat"
        val kaloriMakanan = intent.getIntExtra("EXTRA_KALORI", 0)
        val deskripsiMakanan = intent.getStringExtra("EXTRA_DESKRIPSI") ?: "Deskripsi tidak tersedia."
        val gambarUrl = intent.getStringExtra("EXTRA_GAMBAR_URL")

        // MEMASUKKAN TEKS LANGSUNG VIA BINDING (Tanpa findViewById lagi)
        binding.tvDetailTitle.text = judulMakanan
        binding.tvDetailCalories.text = "$kaloriMakanan Cal - 1 Porsi"
        binding.tvDetailDescription.text = deskripsiMakanan

        // MEMUAT GAMBAR MENGGUNAKAN GLIDE
        Glide.with(this)
            .load(gambarUrl)
            .placeholder(R.drawable.gambar_salad) // Gambar default saat loading (Pastikan gambar_salad ada di drawable Anda)
            .into(binding.ivFoodImage)
    }
}