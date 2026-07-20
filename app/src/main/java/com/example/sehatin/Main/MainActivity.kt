package com.example.sehatin.Main

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.sehatin.R
import com.example.sehatin.Utils.BackgroundMusicManager
import com.example.sehatin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 1. DEKLARASI SOUNDPOOL (Untuk Suara Zero-Delay)
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.container) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        BackgroundMusicManager.initialize(application, R.raw.steps_in_sunlight)

        // =======================================================
        // 2. INISIALISASI SOUNDPOOL
        // =======================================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION) // Mode efek UI
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1) // Hanya memutar 1 suara dalam satu waktu
            .setAudioAttributes(audioAttributes)
            .build()

        // Pre-load file suara ke memori agar instan saat diklik
        soundId = soundPool.load(this, R.raw.sound_effect_navbar, 1)

        // =======================================================
        // 3. NAVIGASI STANDAR BAWAAN ANDROID
        // =======================================================
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Hubungkan Bottom Navigation dengan NavController (Setup Awal)
        binding.navView.setupWithNavController(navController)

        // =======================================================
        // 4. KENDALIKAN KLIK UNTUK SUARA + PINDAH HALAMAN
        // =======================================================
        binding.navView.setOnItemSelectedListener { item ->

            // A. AMBIL PENGATURAN VOLUME DARI USER
            val sharedPrefs = getSharedPreferences("AudioSettings", Context.MODE_PRIVATE)
            val volumeSfxInt = sharedPrefs.getInt("VOLUME_SFX", 100) // Default 100 jika kosong

            // Konversi dari 0-100 menjadi format pecahan 0.0f - 1.0f
            val volumeSfxFloat = volumeSfxInt / 100f

            // B. MAINKAN SUARA SEKARANG JUGA DENGAN VOLUME KUSTOM!
            soundPool.play(soundId, volumeSfxFloat, volumeSfxFloat, 0, 0, 1f)

            // C. Lanjutkan perintah bawaan untuk berpindah fragment/halaman
            NavigationUI.onNavDestinationSelected(item, navController)
        }

        // =======================================================
        // 5. PENANGKAP INTENT DARI ACTIVITY LAIN (Untuk Pindah Tab)
        // =======================================================
        intent?.let { periksaIntentPindahTab(it) }
    }

    // Fungsi tambahan untuk meng-handle intent baru jika Activity sudah terbuka sebelumnya (singleTop/singleTask)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent?.let { periksaIntentPindahTab(it) }
    }

    // Logika utama untuk memindahkan tab
    private fun periksaIntentPindahTab(incomingIntent: Intent) {
        val fragmentTujuan = incomingIntent.getStringExtra("TAMPILKAN_FRAGMENT")

        when (fragmentTujuan) {
            "Pencapaian" -> {
                binding.navView.selectedItemId = R.id.navigation_pencapaian
            }
            "Tantangan" -> {
                binding.navView.selectedItemId = R.id.navigation_tantangan
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Bersihkan memori SoundPool jika aplikasi ditutup agar tidak membebani HP
        soundPool.release()
    }
}