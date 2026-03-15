package com.example.sehatin.ui.Tantangan.Olahraga

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.sehatin.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class PreviewTantanganActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preview_tantangan)

        // Mengatur padding agar tidak tertutup notch/status bar HP
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ==========================================
        // 1. TANGKAP DATA DARI DETAIL TANTANGAN
        // ==========================================
        val namaMisi = intent.getStringExtra("NAMA_MISI") ?: "Tantangan"
        val targetAngka = intent.getIntExtra("TARGET_ANGKA", 0)
        val rewardPoin = intent.getIntExtra("REWARD_POIN", 0)
        val rewardExp = intent.getIntExtra("REWARD_EXP", 0)
        val fileGif = intent.getIntExtra("EXTRA_GIF_FILE", 0)

        // ==========================================
        // 2. HUBUNGKAN KE ID DI XML ANDA
        // ==========================================
        val btnBack = findViewById<MaterialCardView>(R.id.btn_back)
        val btnLakukan = findViewById<MaterialButton>(R.id.btn_lakukan)
        val tvPreviewTitle = findViewById<TextView>(R.id.tv_preview_title)
        val tvPreviewDesc = findViewById<TextView>(R.id.tv_preview_desc)
        val ivPreviewBg = findViewById<ImageView>(R.id.iv_preview_bg)

        // Aksi Tombol Kembali
        btnBack.setOnClickListener { finish() }

        // ==========================================
        // 3. TAMPILKAN DATA KE LAYAR (Otomatis Dinamis)
        // ==========================================
        // Membuat judul jadi seperti: "Lakukan Push Up 15 kali"
        tvPreviewTitle.text = "$namaMisi $targetAngka kali"

        // Membuat deskripsi yang menampilkan hadiah
        tvPreviewDesc.text = "Selesaikan tantangan ini untuk mendapatkan $rewardPoin Poin dan $rewardExp EXP tambahan hari ini!"

        // Putar GIF/Gambar dari Resourse menggunakan Glide
        if (fileGif != 0) {
            Glide.with(this)
                .load(fileGif)
                .centerCrop()
                .into(ivPreviewBg)
        }

        // ==========================================
        // 4. LEMPAR DATA KE AKTIVITAS BERIKUTNYA SAAT DIKLIK
        // ==========================================
        btnLakukan.setOnClickListener {
            // Catatan: Pastikan lokasi 'DetailAktivitasActivity' ini sesuai dengan package Anda.
            // Jika merah, klik Alt+Enter untuk meng-import class-nya.
            val intentLanjut = Intent(this, com.example.sehatin.ui.Tantangan.Olahraga.DetailAktivitasActivity::class.java).apply {
                putExtra("NAMA_MISI", namaMisi)
                putExtra("TARGET_ANGKA", targetAngka)
                putExtra("REWARD_POIN", rewardPoin)
                putExtra("REWARD_EXP", rewardExp)
                putExtra("EXTRA_GIF_FILE", fileGif)
            }
            startActivity(intentLanjut)
            finish() // Tutup halaman preview
        }
    }
}