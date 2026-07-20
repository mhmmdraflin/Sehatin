package com.example.sehatin.ui.SideFeature.Olahraga

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes // IMPORT AUDIO ATTRIBUTES
import android.media.SoundPool       // IMPORT SOUNDPOOL
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.graphics.drawable.Animatable
import com.bumptech.glide.Glide
import com.example.sehatin.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class SesiOlahragaActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var ivKarakter: ImageView
    private var timerUtama: CountDownTimer? = null
    private var sisaWaktuMillis: Long = 0
    private var durasiAwalDetik: Int = 0
    private var isPaused: Boolean = false

    // Variabel Penampung Data
    private var idGerakan: Int = 0
    private var kaloriDidapat: Int = 0
    private var expDidapat: Int = 0

    // VARIABEL SOUNDPOOL (Untuk Suara Zero-Delay)
    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sesi_olahraga)

        // =======================================================
        // INISIALISASI SOUNDPOOL
        // =======================================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3) // Maksimal putar 3 suara bersamaan jika diklik cepat
            .setAudioAttributes(audioAttributes)
            .build()

        // Load suara ke memori sejak awal Activity dibuka
        clickSoundId = soundPool?.load(this, R.raw.tombol_klik_sehatin, 1) ?: 0
        // =======================================================

        tvTimer = findViewById(R.id.tv_timer_angka)
        val tvStatusSesi = findViewById<TextView>(R.id.tv_status_sesi)
        ivKarakter = findViewById(R.id.iv_karakter_olahraga)
        val btnPause = findViewById<MaterialCardView>(R.id.btn_pause_card)

        // MENERIMA DATA DARI INTENT
        idGerakan = intent.getIntExtra("EXTRA_ID_GERAKAN", 0)
        val namaGerakan = intent.getStringExtra("EXTRA_NAMA_GERAKAN") ?: "Olahraga"
        durasiAwalDetik = intent.getIntExtra("EXTRA_DURASI", 30)

        kaloriDidapat = intent.getIntExtra("EXTRA_KALORI", 50)
        expDidapat = intent.getIntExtra("EXTRA_EXP_DIDAPAT", 20)
        val fileGif = intent.getIntExtra("EXTRA_GIF_FILE", 0)

        tvStatusSesi.text = "Lakukan $namaGerakan!"
        sisaWaktuMillis = durasiAwalDetik * 1000L
        updateTeksTimer()

        // Memuat Gambar Animasi GIF menggunakan Glide
        if (fileGif != 0) {
            Glide.with(this)
                .asGif()
                .load(fileGif)
                .into(ivKarakter)
        }

        // JALANKAN POP-UP ABA-ABA 3 DETIK PERTAMA KALI SEBELUM OLAHRAGA DIMULAI
        tampilkanPopUpCountdown {
            mulaiTimerUtama()
        }

        btnPause.setOnClickListener {
            mainkanSuaraKlik() // MEMAINKAN SUARA KLIK
            if (!isPaused) {
                jedaTimerUtama()
                tampilkanDialogPause()
            }
        }
    }

    // ========================================================
    // FUNGSI UNTUK MEMAINKAN SUARA KLIK (ZERO-DELAY)
    // ========================================================
    private fun mainkanSuaraKlik() {
        soundPool?.play(clickSoundId, 1f, 1f, 0, 0, 1f)
    }

    // ========================================================
    // POP-UP DIALOG COUNTDOWN DENGAN KONTROL ANIMASI BREAK/FREEZE
    // ========================================================
    private fun tampilkanPopUpCountdown(onFinishAction: () -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_countdown, null)
        val tvAngka = dialogView.findViewById<TextView>(R.id.tv_angka_countdown)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        // Berhentikan (Freeze) gerakan animasi GIF selama pop-up aba-aba muncul
        (ivKarakter.drawable as? Animatable)?.stop()

        object : CountDownTimer(3999, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val detik = millisUntilFinished / 1000
                if (detik > 0) {
                    tvAngka.text = detik.toString()
                }
            }

            override fun onFinish() {
                dialog.dismiss()
                // Jalankan kembali (Play) gerakan animasi GIF setelah aba-aba selesai
                (ivKarakter.drawable as? Animatable)?.start()
                // Jalankan timer utama sesi olahraga
                onFinishAction.invoke()
            }
        }.start()
    }

    private fun mulaiTimerUtama() {
        timerUtama = object : CountDownTimer(sisaWaktuMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                sisaWaktuMillis = millisUntilFinished
                updateTeksTimer()
            }

            override fun onFinish() {
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

    private fun jedaTimerUtama() {
        timerUtama?.cancel()
        isPaused = true
        // Berhentikan animasi GIF saat menu pause utama terbuka
        (ivKarakter.drawable as? Animatable)?.stop()
    }

    private fun updateTeksTimer() {
        tvTimer.text = (sisaWaktuMillis / 1000).toString()
    }

    private fun tampilkanDialogPause() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_pause_olahraga, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<MaterialButton>(R.id.btn_lanjutkan).setOnClickListener {
            mainkanSuaraKlik() // MEMAINKAN SUARA KLIK
            dialog.dismiss()
            // Saat dilanjutkan, panggil pop-up aba-aba 3 detik dulu baru jalankan timer utama
            tampilkanPopUpCountdown {
                mulaiTimerUtama()
            }
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_mulai_ulang).setOnClickListener {
            mainkanSuaraKlik() // MEMAINKAN SUARA KLIK
            dialog.dismiss()
            sisaWaktuMillis = durasiAwalDetik * 1000L
            updateTeksTimer()
            // Saat mulai ulang dari awal, jalankan aba-aba 3 detik terlebih dahulu
            tampilkanPopUpCountdown {
                mulaiTimerUtama()
            }
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_keluar).setOnClickListener {
            mainkanSuaraKlik() // MEMAINKAN SUARA KLIK
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerUtama?.cancel()
        // Bersihkan memori SoundPool
        soundPool?.release()
        soundPool = null
    }
}