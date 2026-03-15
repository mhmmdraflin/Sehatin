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

class DetailAktivitasActivity : AppCompatActivity() {

    private lateinit var tvAngkaTarget: TextView
    private var timer: CountDownTimer? = null
    private var sisaWaktu: Long = 15000 // Contoh: 15 detik untuk 15 hitungan
    private var isPaused: Boolean = false
    private var isStarted: Boolean = false

    private var rewardPoin: Int = 0
    private var rewardExp: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_aktivitas)

        findViewById<MaterialCardView>(R.id.btn_back).setOnClickListener { finish() }

        val tvNamaAktivitas = findViewById<TextView>(R.id.tv_nama_aktivitas)
        tvAngkaTarget = findViewById(R.id.tv_angka_target)
        val btnMulai = findViewById<MaterialButton>(R.id.btn_action_toggle)
        val btnPause = findViewById<MaterialCardView>(R.id.btn_pause_card)
        val ivIlustrasi = findViewById<ImageView>(R.id.iv_ilustrasi_aktivitas)

        // 1. TERIMA DATA DARI HALAMAN SEBELUMNYA
        val namaMisi = intent.getStringExtra("NAMA_MISI") ?: "Tantangan"
        val targetAngka = intent.getIntExtra("TARGET_ANGKA", 0)
        rewardPoin = intent.getIntExtra("REWARD_POIN", 0)
        rewardExp = intent.getIntExtra("REWARD_EXP", 0)

        // Tangkap file GIF
        val fileGif = intent.getIntExtra("EXTRA_GIF_FILE", 0)

        // 2. TAMPILKAN DATA KE LAYAR
        tvNamaAktivitas.text = "$namaMisi $targetAngka kali"
        tvAngkaTarget.text = targetAngka.toString()

        // 3. PUTAR GIF MENGGUNAKAN GLIDE
        if (fileGif != 0) {
            Glide.with(this)
                .load(fileGif)
                .centerCrop() // Agar gambar memenuhi area ImageView dengan rapi
                .into(ivIlustrasi)
        }

        // 4. LOGIKA TOMBOL MULAI & PAUSE
        btnMulai.setOnClickListener {
            if (!isStarted) {
                mulaiTimer()
                btnMulai.text = "Sedang Beraksi..."
                btnMulai.isEnabled = false
                isStarted = true
            }
        }

        btnPause.setOnClickListener {
            if (isStarted && !isPaused) {
                jedaTimer()
                tampilkanDialogJeda()
            }
        }
    }

    private fun mulaiTimer() {
        timer = object : CountDownTimer(sisaWaktu, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                sisaWaktu = millisUntilFinished
                tvAngkaTarget.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                // MISI SELESAI! PINDAH KE HALAMAN CONGRATULATIONS
                val intent = Intent(this@DetailAktivitasActivity, CongratulationsActivity::class.java).apply {
                    putExtra("HASIL_POIN", rewardPoin)
                    putExtra("HASIL_EXP", rewardExp)
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

    private fun tampilkanDialogJeda() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_jeda, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<MaterialButton>(R.id.btn_lanjutkan).setOnClickListener {
            dialog.dismiss()
            mulaiTimer()
        }
        dialogView.findViewById<MaterialButton>(R.id.btn_mulai_ulang).setOnClickListener {
            dialog.dismiss()
            sisaWaktu = 15000 // Kembalikan ke waktu awal
            tvAngkaTarget.text = "15"
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