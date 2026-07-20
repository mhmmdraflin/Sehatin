package com.example.sehatin.ui.SideFeature

import android.media.AudioAttributes // IMPORT AUDIO ATTRIBUTES
import android.media.SoundPool       // IMPORT SOUNDPOOL
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

    // VARIABEL SOUNDPOOL (Pour Suara Zero-Delay)
    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Inisialisasi ViewBinding
        binding = ActivityDetailInformasiMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // =======================================================
        // INISIALISASI SOUNDPOOL
        // =======================================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load suara ke memori sejak awal Activity dibuka
        clickSoundId = soundPool?.load(this, R.raw.tombol_klik_sehatin, 1) ?: 0
        // =======================================================

        // Mengatur jarak aman layar (Edge-to-Edge) menggunakan binding.root
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // FUNGSI TOMBOL KEMBALI
        binding.btnBack.setOnClickListener {
            mainkanSuaraKlik() // MEMAINKAN SUARA KLIK
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
            .placeholder(R.drawable.gambar_salad) // Gambar default saat loading
            .into(binding.ivFoodImage)
    }

    // ========================================================
    // FUNGSI UNTUK MEMAINKAN SUARA KLIK (ZERO-DELAY)
    // ========================================================
    private fun mainkanSuaraKlik() {
        soundPool?.play(clickSoundId, 1f, 1f, 0, 0, 1f)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Bersihkan memori SoundPool agar tidak terjadi kebocoran memori (memory leak)
        soundPool?.release()
        soundPool = null
    }
}