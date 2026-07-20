package com.example.sehatin.ui.Tantangan.Olahraga

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes // IMPORT SOUNDPOOL
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.sehatin.R
import com.example.sehatin.Utils.BackgroundMusicManager // IMPORT MESIN BGM
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

    // Komponen UI Utama
    private lateinit var tvAngkaTarget: TextView
    private lateinit var overlayGetReady: ConstraintLayout
    private lateinit var tvAngkaCountdown: TextView
    private lateinit var ivIlustrasi: ImageView

    // State FSM Saat Ini
    private var currentState = TantanganOlahragaState.BELUM_DIMULAI

    // Variabel Timer Dinamis
    private var timer: CountDownTimer? = null
    private var persiapanTimer: CountDownTimer? = null
    private var waktuAwalMillis: Long = 0
    private var sisaWaktuMillis: Long = 0

    // Variabel Data
    private var rewardPoin: Int = 0
    private var rewardExp: Int = 0
    private var namaMisi: String = "Tantangan"
    private var fileGif: Int = 0

    // VARIABEL SOUNDPOOL (Untuk Suara Zero-Delay)
    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_aktivitas)

        // =======================================================
        // A. INISIALISASI SOUNDPOOL
        // =======================================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        clickSoundId = soundPool?.load(this, R.raw.tombol_klik_sehatin, 1) ?: 0

        // =======================================================
        // B. SIAPKAN MUSIK LATAR TANTANGAN (BGM_TANTANGAN)
        // =======================================================
        // Menghentikan musik utama reguler dan menggantinya dengan musik latihan khusus olahraga
        BackgroundMusicManager.stopMusic()
        BackgroundMusicManager.initialize(application, R.raw.bgm_tantangan)

        // Bind UI Utama
        tvAngkaTarget = findViewById(R.id.tv_angka_target)
        val tvNamaAktivitas = findViewById<TextView>(R.id.tv_nama_aktivitas)
        val btnPause = findViewById<MaterialCardView>(R.id.btn_pause_card)
        ivIlustrasi = findViewById(R.id.iv_ilustrasi_aktivitas)
        val btnBack = findViewById<MaterialCardView>(R.id.btn_back)

        // Bind UI Overlay Get Ready
        overlayGetReady = findViewById(R.id.overlay_get_ready)
        tvAngkaCountdown = findViewById(R.id.tv_angka_countdown)

        btnBack.setOnClickListener {
            mainkanSuaraKlik()
            kembaliKeLayarUtama()
        }

        // 1. TERIMA DATA DARI HALAMAN SEBELUMNYA
        namaMisi = intent.getStringExtra("NAMA_MISI") ?: "Tantangan"
        val targetAngka = intent.getIntExtra("TARGET_ANGKA", 0)
        rewardPoin = intent.getIntExtra("REWARD_POIN", 0)
        rewardExp = intent.getIntExtra("REWARD_EXP", 0)
        fileGif = intent.getIntExtra("EXTRA_GIF_FILE", 0)

        // 2. SET WAKTU DINAMIS & TAMPILAN
        waktuAwalMillis = targetAngka * 1000L
        tvNamaAktivitas.text = "$namaMisi $targetAngka kali"

        // 3. KONTROL TOMBOL UI (Pemicu FSM Jeda)
        btnPause.setOnClickListener {
            mainkanSuaraKlik()
            if (currentState == TantanganOlahragaState.SEDANG_BERJALAN) {
                // Trigger: Pause -> Masuk State DI_JEDA
                ubahState(TantanganOlahragaState.DI_JEDA)
            }
        }

        // 4. MULAI FSM SECARA OTOMATIS
        ubahState(TantanganOlahragaState.BELUM_DIMULAI)
    }

    // ========================================================
    // 2. IMPLEMENTASI TRANSISI FSM (KONTROL AUDIO DINAMIS)
    // ========================================================
    private fun ubahState(newState: TantanganOlahragaState) {
        currentState = newState

        when (newState) {
            TantanganOlahragaState.BELUM_DIMULAI -> {
                sisaWaktuMillis = waktuAwalMillis
                tvAngkaTarget.text = (waktuAwalMillis / 1000).toString()

                // GAMBAR DIAM: Gunakan asBitmap() agar GIF tidak bergerak saat persiapan
                if (fileGif != 0) {
                    Glide.with(this).asBitmap().load(fileGif).centerCrop().into(ivIlustrasi)
                }

                // MATIKAN MUSIK SEMENTARA SAAT PERSIAPAN TIMER ABA-ABA
                BackgroundMusicManager.pauseMusic()

                mulaiGetReadyOverlay()
            }
            TantanganOlahragaState.SEDANG_BERJALAN -> {
                overlayGetReady.visibility = View.GONE

                // ANIMASI BERGERAK: Gunakan asGif() saat olahraga dimulai
                if (fileGif != 0) {
                    Glide.with(this).asGif().load(fileGif).centerCrop().into(ivIlustrasi)
                }

                // NYALAKAN MUSIK LATAR TANTANGAN SAAT OLAHRAGA BERJALAN
                BackgroundMusicManager.resume()

                mulaiTimerOlahraga()
            }
            TantanganOlahragaState.DI_JEDA -> {
                berhentikanSemuaTimer()

                // GAMBAR DIAM: Bekukan GIF saat user menekan Pause
                if (fileGif != 0) {
                    Glide.with(this).asBitmap().load(fileGif).centerCrop().into(ivIlustrasi)
                }

                // MATIKAN MUSIK LATAR SAAT DIJEDA
                BackgroundMusicManager.pauseMusic()

                tampilkanDialogJeda()
            }
            TantanganOlahragaState.SELESAI -> {
                berhentikanSemuaTimer()

                // GAMBAR DIAM: Bekukan GIF saat waktu habis
                if (fileGif != 0) {
                    Glide.with(this).asBitmap().load(fileGif).centerCrop().into(ivIlustrasi)
                }

                // HENTIKAN TOTAL AUDIO BGM TANTANGAN KARENA TANTANGAN BERHASIL DIJAWAB
                BackgroundMusicManager.stopMusic()

                ubahState(TantanganOlahragaState.HADIAH_DITERIMA)
            }
            TantanganOlahragaState.HADIAH_DITERIMA -> {
                // INISIALISASI ULANG MUSIK UTAMA SEBELUM BERPINDAH LAYAR
                BackgroundMusicManager.initialize(application, R.raw.steps_in_sunlight)

                val intent = Intent(this@DetailAktivitasActivity, CongratulationsActivity::class.java).apply {
                    putExtra("HASIL_POIN", rewardPoin)
                    putExtra("HASIL_EXP", rewardExp)
                    putExtra("HASIL_NAMA_MISI", namaMisi)
                }
                startActivity(intent)
                finish()
            }
        }
    }

    // ========================================================
    // LOGIKA FUNGSI PENDUKUNG UI DAN TIMER
    // ========================================================
    private fun mainkanSuaraKlik() {
        val sharedPrefs = getSharedPreferences("AudioSettings", Context.MODE_PRIVATE)
        val volumeSfxInt = sharedPrefs.getInt("VOLUME_SFX", 100)
        val volumeSfxFloat = volumeSfxInt / 100f
        soundPool?.play(clickSoundId, volumeSfxFloat, volumeSfxFloat, 0, 0, 1f)
    }

    private fun mulaiGetReadyOverlay() {
        overlayGetReady.visibility = View.VISIBLE
        tvAngkaCountdown.text = "3"

        persiapanTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val detik = (millisUntilFinished / 1000) + 1
                tvAngkaCountdown.text = detik.toString()
            }

            override fun onFinish() {
                tvAngkaCountdown.text = "GO!"
                Handler(Looper.getMainLooper()).postDelayed({
                    ubahState(TantanganOlahragaState.SEDANG_BERJALAN)
                }, 500)
            }
        }.start()
    }

    private fun mulaiTimerOlahraga() {
        timer = object : CountDownTimer(sisaWaktuMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                sisaWaktuMillis = millisUntilFinished
                tvAngkaTarget.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                tvAngkaTarget.text = "0"
                ubahState(TantanganOlahragaState.SELESAI)
            }
        }.start()
    }

    private fun berhentikanSemuaTimer() {
        timer?.cancel()
        persiapanTimer?.cancel()
    }

    private fun tampilkanDialogJeda() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_jeda, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<MaterialButton>(R.id.btn_lanjutkan).setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
            mulaiGetReadyOverlay()
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_mulai_ulang).setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
            ubahState(TantanganOlahragaState.BELUM_DIMULAI)
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_keluar).setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
            kembaliKeLayarUtama()
        }
        dialog.show()
    }

    private fun kembaliKeLayarUtama() {
        BackgroundMusicManager.stopMusic()
        BackgroundMusicManager.initialize(application, R.raw.steps_in_sunlight)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        berhentikanSemuaTimer()
        soundPool?.release()
        soundPool = null
    }
}