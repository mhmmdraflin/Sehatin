package com.example.sehatin.ui.Tantangan.Olahraga

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sehatin.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

// ==========================================
// 1. DEKLARASI ALGORITMA FINITE STATE MACHINE (FSM)
// ==========================================
enum class TantanganOlahragaState {
    BELUM_DIMULAI,
    SEDANG_BERJALAN,
    DI_JEDA,
    SELESAI,
    HADIAH_DITERIMA
}

class DetailAktivitasActivity : AppCompatActivity() {

    // Komponen UI
    private lateinit var tvAngkaTarget: TextView
    private lateinit var btnMulai: MaterialButton

    // State FSM Saat Ini
    private var currentState = TantanganOlahragaState.BELUM_DIMULAI

    // Variabel Timer Dinamis
    private var timer: CountDownTimer? = null
    private var waktuAwalMillis: Long = 0
    private var sisaWaktuMillis: Long = 0

    // Variabel Data
    private var rewardPoin: Int = 0
    private var rewardExp: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_aktivitas)

        findViewById<MaterialCardView>(R.id.btn_back).setOnClickListener { finish() }

        val tvNamaAktivitas = findViewById<TextView>(R.id.tv_nama_aktivitas)
        tvAngkaTarget = findViewById(R.id.tv_angka_target)
        btnMulai = findViewById(R.id.btn_action_toggle)
        val btnPause = findViewById<MaterialCardView>(R.id.btn_pause_card)
        val ivIlustrasi = findViewById<ImageView>(R.id.iv_ilustrasi_aktivitas)

        // 1. TERIMA DATA DARI HALAMAN SEBELUMNYA
        val namaMisi = intent.getStringExtra("NAMA_MISI") ?: "Tantangan"
        val targetAngka = intent.getIntExtra("TARGET_ANGKA", 0)
        rewardPoin = intent.getIntExtra("REWARD_POIN", 0)
        rewardExp = intent.getIntExtra("REWARD_EXP", 0)
        val fileGif = intent.getIntExtra("EXTRA_GIF_FILE", 0)

        // 2. SET WAKTU DINAMIS & TAMPILAN
        waktuAwalMillis = targetAngka * 1000L // 1 Target = 1 Detik
        tvNamaAktivitas.text = "$namaMisi $targetAngka kali"

        if (fileGif != 0) {
            Glide.with(this)
                .load(fileGif)
                .centerCrop()
                .into(ivIlustrasi)
        }

        // 3. KONTROL TOMBOL UI (Pemicu FSM)
        btnMulai.setOnClickListener {
            if (currentState == TantanganOlahragaState.BELUM_DIMULAI) {
                // Trigger: Mulai -> Masuk State SEDANG_BERJALAN
                ubahState(TantanganOlahragaState.SEDANG_BERJALAN)
            }
        }

        btnPause.setOnClickListener {
            if (currentState == TantanganOlahragaState.SEDANG_BERJALAN) {
                // Trigger: Pause -> Masuk State DI_JEDA
                ubahState(TantanganOlahragaState.DI_JEDA)
            }
        }

        // 4. MULAI FSM (Langkah Awal)
        ubahState(TantanganOlahragaState.BELUM_DIMULAI)
    }

    // ========================================================
    // 2. IMPLEMENTASI TRANSISI FSM (FUNGSI UTAMA KONTROLER)
    // ========================================================
    private fun ubahState(newState: TantanganOlahragaState) {
        currentState = newState

        when (newState) {
            TantanganOlahragaState.BELUM_DIMULAI -> {
                // RESET ALUR: Kembalikan waktu ke awal & nyalakan tombol
                sisaWaktuMillis = waktuAwalMillis
                tvAngkaTarget.text = (waktuAwalMillis / 1000).toString()

                btnMulai.text = "Mulai"
                btnMulai.isEnabled = true
            }
            TantanganOlahragaState.SEDANG_BERJALAN -> {
                // Timer berjalan, matikan tombol Mulai
                btnMulai.text = "Sedang Beraksi..."
                btnMulai.isEnabled = false
                mulaiTimer()
            }
            TantanganOlahragaState.DI_JEDA -> {
                // Hentikan timer sementara dan tampilkan pop-up
                jedaTimer()
                tampilkanDialogJeda()
            }
            TantanganOlahragaState.SELESAI -> {
                // Target tercapai! Langsung transisi ke klaim hadiah
                jedaTimer()
                ubahState(TantanganOlahragaState.HADIAH_DITERIMA)
            }
            TantanganOlahragaState.HADIAH_DITERIMA -> {
                // Pindah ke layar Congratulations
                val intent = Intent(this@DetailAktivitasActivity, CongratulationsActivity::class.java).apply {
                    putExtra("HASIL_POIN", rewardPoin)
                    putExtra("HASIL_EXP", rewardExp)
                }
                startActivity(intent)
                finish()
            }
        }
    }

    // ========================================================
    // LOGIKA TIMER
    // ========================================================
    private fun mulaiTimer() {
        timer = object : CountDownTimer(sisaWaktuMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                sisaWaktuMillis = millisUntilFinished
                tvAngkaTarget.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                tvAngkaTarget.text = "0"
                // Trigger: Waktu Habis (Sukses) -> Masuk State SELESAI
                ubahState(TantanganOlahragaState.SELESAI)
            }
        }.start()
    }

    private fun jedaTimer() {
        timer?.cancel()
    }

    // ========================================================
    // LOGIKA DIALOG (TRANSISI FSM JEDA)
    // ========================================================
    private fun tampilkanDialogJeda() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_jeda, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<MaterialButton>(R.id.btn_lanjutkan).setOnClickListener {
            dialog.dismiss()
            // Trigger: Lanjutkan -> Masuk State SEDANG_BERJALAN
            ubahState(TantanganOlahragaState.SEDANG_BERJALAN)
        }
        dialogView.findViewById<MaterialButton>(R.id.btn_mulai_ulang).setOnClickListener {
            dialog.dismiss()
            // Trigger: Mulai Ulang -> Masuk State BELUM_DIMULAI
            ubahState(TantanganOlahragaState.BELUM_DIMULAI)
        }
        dialogView.findViewById<MaterialButton>(R.id.btn_keluar).setOnClickListener {
            dialog.dismiss()
            finish() // Mengakhiri mesin FSM (State -> [*])
        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        jedaTimer() // Pastikan timer mati saat keluar untuk mencegah memory leak
    }
}