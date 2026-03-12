package com.example.sehatin.ui.SideFeature

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.sehatin.R
import com.google.android.material.card.MaterialCardView

class DetailInformasiMakananActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_informasi_makanan)

        // Mengatur jarak aman layar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // FUNGSI TOMBOL KEMBALI
        val btnBack = findViewById<MaterialCardView>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        // MENERIMA DATA DARI HALAMAN SEBELUMNYA
        val judulMakanan = intent.getStringExtra("EXTRA_JUDUL") ?: "Makanan Sehat"
        val kaloriMakanan = intent.getIntExtra("EXTRA_KALORI", 0)
        val deskripsiMakanan = intent.getStringExtra("EXTRA_DESKRIPSI") ?: "Deskripsi tidak tersedia."
        val gambarUrl = intent.getStringExtra("EXTRA_GAMBAR_URL")

        // MENYIAPKAN KOMPONEN TAMPILAN
        val tvTitle = findViewById<TextView>(R.id.tv_detail_title)
        val tvCalories = findViewById<TextView>(R.id.tv_detail_calories)
        val tvDescription = findViewById<TextView>(R.id.tv_detail_description)
        val ivFoodImage = findViewById<ImageView>(R.id.iv_food_image)

        // MEMASUKKAN TEKS
        tvTitle.text = judulMakanan
        tvCalories.text = "$kaloriMakanan Cal - 1 Porsi"
        tvDescription.text = deskripsiMakanan

        // MEMUAT GAMBAR MENGGUNAKAN GLIDE
        Glide.with(this)
            .load(gambarUrl)
            .placeholder(R.drawable.gambar_salad) // Gambar default saat loading
            .into(ivFoodImage)
    }
}