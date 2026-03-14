package com.example.sehatin.ui.SideFeature.Olahraga

import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class HasilOlahragaActivity : AppCompatActivity() {

    private lateinit var viewModel: OlahragaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hasil_olahraga)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // INISIALISASI MVVM
        val pref = OlahragaPreferences.getInstance(applicationContext.dataStore)
        val factory = OlahragaViewModelFactory(OlahragaRepository(pref))
        viewModel = ViewModelProvider(this, factory)[OlahragaViewModel::class.java]

        // TANGKAP DATA HASIL
        val idGerakanSelesai = intent.getIntExtra("HASIL_ID_GERAKAN", 0)
        val kaloriTerbakar = intent.getIntExtra("HASIL_KALORI", 0)
        val durasiDetik = intent.getIntExtra("HASIL_WAKTU", 0)
        val expDidapat = intent.getIntExtra("HASIL_EXP", 0)

        // EKSEKUSI PENYIMPANAN KE VIEWMODEL
        if (idGerakanSelesai != 0) {
            viewModel.simpanGerakanSelesai(idGerakanSelesai)
            viewModel.tambahKaloriDanExp(kaloriTerbakar, expDidapat)
        }

        // HUBUNGKAN KE XML
        val tvHasilKali = findViewById<TextView>(R.id.tv_hasil_kali)
        val tvHasilKalori = findViewById<TextView>(R.id.tv_hasil_kalori)
        val tvHasilWaktu = findViewById<TextView>(R.id.tv_hasil_waktu)
        val tvHasilExp = findViewById<TextView>(R.id.tv_hasil_exp)

        // TAMPILKAN DATA
        tvHasilKali.text = "1"
        tvHasilKalori.text = kaloriTerbakar.toString()
        tvHasilExp.text = "+ $expDidapat EXP"

        val menit = durasiDetik / 60
        val sisaDetik = durasiDetik % 60
        tvHasilWaktu.text = if (menit > 0) "$menit:$sisaDetik" else "0:$durasiDetik"

        // ==========================================
        // EKSEKUSI ANIMASI KEMENANGAN
        // ==========================================
        jalankanAnimasiKemenangan()

        // TOMBOL KEMBALI
        findViewById<MaterialButton>(R.id.btn_kembali_dashboard).setOnClickListener {
            // Pastikan MainActivity adalah tujuan Dashboard Anda
            val intent = Intent(this, com.example.sehatin.Main.MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun jalankanAnimasiKemenangan() {
        val cardMain = findViewById<MaterialCardView>(R.id.card_main_hasil)
        val ivTrofi = findViewById<ImageView>(R.id.iv_trofi)
        val cardExp = findViewById<MaterialCardView>(R.id.card_hasil_exp)

        // 1. Set kondisi awal (Sembunyikan dan kecilkan komponen)
        cardMain.alpha = 0f
        cardMain.translationY = 150f // Posisikan agak ke bawah

        ivTrofi.scaleX = 0f
        ivTrofi.scaleY = 0f

        cardExp.scaleX = 0f
        cardExp.scaleY = 0f

        // 2. Mulai Animasi Berantai
        // A. Kartu Utama Muncul dari bawah
        cardMain.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .withEndAction {

                // B. Trofi Muncul memantul (Overshoot)
                ivTrofi.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500)
                    .setInterpolator(OvershootInterpolator(1.5f))
                    .withEndAction {

                        // C. Badge EXP Membesar (Pulse)
                        cardExp.animate()
                            .scaleX(1.3f) // Membesar sedikit melebihi ukuran asli
                            .scaleY(1.3f)
                            .setDuration(300)
                            .withEndAction {
                                // Kembali ke ukuran normal
                                cardExp.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(200)
                                    .start()
                            }.start()
                    }.start()
            }.start()
    }
}