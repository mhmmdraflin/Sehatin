package com.example.sehatin.ui.SideFeature.Olahraga

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

class SesiOlahragaActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private var timer: CountDownTimer? = null
    private var sisaWaktuMillis: Long = 0
    private var durasiAwalDetik: Int = 0
    private var isPaused: Boolean = false

    // Variabel Penampung Data
    private var idGerakan: Int = 0
    private var kaloriDidapat: Int = 0
    private var expDidapat: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sesi_olahraga)

        tvTimer = findViewById(R.id.tv_timer_angka)
        val tvStatusSesi = findViewById<TextView>(R.id.tv_status_sesi)
        val ivKarakter = findViewById<ImageView>(R.id.iv_karakter_olahraga)
        val btnPause = findViewById<MaterialCardView>(R.id.btn_pause_card)

        // MENERIMA DATA DARI INTENT (DENGAN NILAI DEFAULT AMAN)
        idGerakan = intent.getIntExtra("EXTRA_ID_GERAKAN", 0)
        val namaGerakan = intent.getStringExtra("EXTRA_NAMA_GERAKAN") ?: "Olahraga"
        durasiAwalDetik = intent.getIntExtra("EXTRA_DURASI", 30)

        // Safety Net: Jika data kosong, otomatis beri 50 Kalori dan 20 EXP
        kaloriDidapat = intent.getIntExtra("EXTRA_KALORI", 50)
        expDidapat = intent.getIntExtra("EXTRA_EXP_DIDAPAT", 20)
        val fileGif = intent.getIntExtra("EXTRA_GIF_FILE", 0)

        tvStatusSesi.text = "Lakukan $namaGerakan!"
        sisaWaktuMillis = durasiAwalDetik * 1000L
        updateTeksTimer()

        // Memuat Gambar Animasi GIF
        if (fileGif != 0) {
            Glide.with(this)
                .asGif()
                .load(fileGif)
                .into(ivKarakter)
        }

        mulaiTimer()

        btnPause.setOnClickListener {
            if (!isPaused) {
                jedaTimer()
                tampilkanDialogPause()
            }
        }
    }

    private fun mulaiTimer() {
        timer = object : CountDownTimer(sisaWaktuMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                sisaWaktuMillis = millisUntilFinished
                updateTeksTimer()
            }

            override fun onFinish() {
                // TIMER HABIS: Pindah ke Halaman Hasil dan Bawa Kalori & EXP
                val intent = Intent(this@SesiOlahragaActivity, HasilOlahragaActivity::class.java).apply {
                    putExtra("HASIL_ID_GERAKAN", idGerakan)
                    putExtra("HASIL_KALORI", kaloriDidapat)
                    putExtra("HASIL_WAKTU", durasiAwalDetik)
                    putExtra("HASIL_EXP", expDidapat)
                }
                startActivity(intent)
                finish()
            }
        }.start()
        isPaused = false
    }

    private fun jedaTimer() {
        timer?.cancel()
        isPaused = true
    }

    private fun updateTeksTimer() {
        tvTimer.text = (sisaWaktuMillis / 1000).toString()
    }

    private fun tampilkanDialogPause() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_pause_olahraga, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<MaterialButton>(R.id.btn_lanjutkan).setOnClickListener {
            dialog.dismiss()
            mulaiTimer()
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_mulai_ulang).setOnClickListener {
            dialog.dismiss()
            sisaWaktuMillis = durasiAwalDetik * 1000L
            updateTeksTimer()
            mulaiTimer()
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_keluar).setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}