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
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

// =======================================================
// IMPORT MESIN TANTANGAN (BANK PUSAT EXP)
// =======================================================
import com.example.sehatin.ui.Tantangan.TantanganPreferences
import com.example.sehatin.ui.Tantangan.TantanganRepository
import com.example.sehatin.ui.Tantangan.TantanganViewModel
import com.example.sehatin.ui.Tantangan.TantanganViewModelFactory
import com.example.sehatin.ui.Tantangan.dataStoreTantangan

class HasilOlahragaActivity : AppCompatActivity() {

    private lateinit var viewModelOlahraga: OlahragaViewModel
    private lateinit var viewModelTantangan: TantanganViewModel // Tambahan mesin utama

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hasil_olahraga)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // =======================================================
        // 1. AMBIL IDENTITAS USER AKTIF (MULTI-AKUN)
        // =======================================================
        val userPref = UserPreference(this)
        val userKey = userPref.getName() ?: "guest_user"

        // =======================================================
        // 2. INISIALISASI KEDUA VIEWMODEL
        // =======================================================
        // A. Mesin Olahraga (Untuk menyimpan Kalori & History Misi Olahraga)
        val prefOlahraga = OlahragaPreferences.getInstance(applicationContext.dataStore)
        val factoryOlahraga = OlahragaViewModelFactory(OlahragaRepository(prefOlahraga))
        viewModelOlahraga = ViewModelProvider(this, factoryOlahraga)[OlahragaViewModel::class.java]

        // B. Mesin Tantangan (Untuk menyetorkan EXP Utama ke Dashboard)
        val prefTantangan = TantanganPreferences.getInstance(applicationContext.dataStoreTantangan)
        val factoryTantangan = TantanganViewModelFactory(TantanganRepository(prefTantangan))
        viewModelTantangan = ViewModelProvider(this, factoryTantangan)[TantanganViewModel::class.java]

        // =======================================================
        // 3. TANGKAP DATA HASIL OLAHRAGA
        // =======================================================
        val idGerakanSelesai = intent.getIntExtra("HASIL_ID_GERAKAN", 0)
        val kaloriTerbakar = intent.getIntExtra("HASIL_KALORI", 0)
        val durasiDetik = intent.getIntExtra("HASIL_WAKTU", 0)
        val expDidapat = intent.getIntExtra("HASIL_EXP", 0)

        // =======================================================
        // 4. DISTRIBUSIKAN DATA KE TEMPAT YANG TEPAT
        // =======================================================
        if (idGerakanSelesai != 0) {
            // A. Simpan Status Selesai Olahraga ke Mesin Olahraga
            viewModelOlahraga.simpanGerakanSelesai(idGerakanSelesai)

            // Catatan: Anda bisa mengubah tambahKaloriDanExp di OlahragaViewModel menjadi tambahKalori saja nanti
            viewModelOlahraga.tambahKaloriDanExp(kaloriTerbakar, expDidapat)

            // B. Setorkan EXP murni ke BANK PUSAT (Tantangan) berdasarkan Akun Aktif
            viewModelTantangan.tambahExp(userKey, expDidapat)
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

        cardMain.alpha = 0f
        cardMain.translationY = 150f

        ivTrofi.scaleX = 0f
        ivTrofi.scaleY = 0f

        cardExp.scaleX = 0f
        cardExp.scaleY = 0f

        cardMain.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .withEndAction {
                ivTrofi.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500)
                    .setInterpolator(OvershootInterpolator(1.5f))
                    .withEndAction {
                        cardExp.animate()
                            .scaleX(1.3f)
                            .scaleY(1.3f)
                            .setDuration(300)
                            .withEndAction {
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