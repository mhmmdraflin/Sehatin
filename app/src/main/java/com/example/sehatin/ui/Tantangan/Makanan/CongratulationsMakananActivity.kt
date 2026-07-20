package com.example.sehatin.ui.Tantangan.Makanan

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.AudioAttributes // IMPORT SOUNDPOOL
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.Utils.BackgroundMusicManager // IMPORT MESIN BGM
import com.example.sehatin.databinding.ActivityCongratulationsMakananBinding

// Import Mesin Utama Tantangan
import com.example.sehatin.ui.Tantangan.TantanganPreferences
import com.example.sehatin.ui.Tantangan.TantanganRepository
import com.example.sehatin.ui.Tantangan.TantanganViewModel
import com.example.sehatin.ui.Tantangan.TantanganViewModelFactory
import com.example.sehatin.ui.Tantangan.dataStoreTantangan

// Import Mesin Pencapaian (Sensor Papan Skor)
import com.example.sehatin.ui.Pencapaian.PencapaianPreferences
import com.example.sehatin.ui.Pencapaian.PencapaianRepository
import com.example.sehatin.ui.Pencapaian.PencapaianViewModel
import com.example.sehatin.ui.Pencapaian.PencapaianViewModelFactory
import com.example.sehatin.ui.Pencapaian.dataStorePencapaian
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CongratulationsMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCongratulationsMakananBinding
    private lateinit var viewModel: TantanganViewModel

    private var isChestOpened = false
    private var pulseAnimator: ValueAnimator? = null
    private lateinit var chestAnimation: AnimationDrawable

    // VARIABEL SOUNDPOOL (Untuk Efek Suara Berlapis)
    private var soundPool: SoundPool? = null
    private var soundOrchestralWin: Int = 0
    private var soundExpPoint: Int = 0
    private var clickSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCongratulationsMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // =======================================================
        // 1. MATIKAN MUSIK LATAR UTAMA UNTUK HALAMAN INI
        // =======================================================
        BackgroundMusicManager.stopMusic()

        // =======================================================
        // 2. INISIALISASI SOUNDPOOL
        // =======================================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3) // Izinkan 3 suara tumpang tindih secara bersamaan
            .setAudioAttributes(audioAttributes)
            .build()

        // Muat file suara dari res/raw secara aman
        soundOrchestralWin = soundPool?.load(this, R.raw.orchestral_win, 1) ?: 0
        soundExpPoint = soundPool?.load(this, R.raw.sound_effect_exp_point, 1) ?: 0
        clickSoundId = soundPool?.load(this, R.raw.tombol_klik_sehatin, 1) ?: 0
        // =======================================================

        // Tangkap Poin & EXP dari halaman Kuis sebelumnya
        val expDiterima = intent.getIntExtra("HASIL_EXP", 0)
        val poinDiterima = intent.getIntExtra("HASIL_POIN", 0)

        // Set teks awal menjadi 0 karena akan bergulir naik menggunakan ValueAnimator
        binding.tvRewardExpMakanan.text = "+0 EXP"
        binding.tvRewardPoinMakanan.text = "+0 Poin"

        // SIMPAN KE BANK PUSAT TANTANGAN
        val userPref = UserPreference(this)
        val userKey = userPref.getName() ?: "guest_user"

        val prefTantangan = TantanganPreferences.getInstance(applicationContext.dataStoreTantangan)
        val factory = TantanganViewModelFactory(TantanganRepository(prefTantangan))
        viewModel = ViewModelProvider(this, factory)[TantanganViewModel::class.java]

        viewModel.tambahExp(userKey, expDiterima)
        viewModel.tambahPoin(userKey, poinDiterima)

        // SENSOR PENCAPAIAN
        val prefPencapaian = PencapaianPreferences.getInstance(applicationContext.dataStorePencapaian)
        val factoryPencapaian = PencapaianViewModelFactory(PencapaianRepository(prefPencapaian))
        val viewModelPencapaian = ViewModelProvider(this, factoryPencapaian)[PencapaianViewModel::class.java]

        lifecycleScope.launch {
            val stateSekarang = prefPencapaian.getPencapaianProgress().first()
            viewModelPencapaian.updateProgress(prefPencapaian.MAKANAN_KEY, stateSekarang.makanan + 1)
            viewModelPencapaian.updateProgress(prefPencapaian.POIN_KEY, stateSekarang.poin + poinDiterima)
            viewModelPencapaian.updateProgress(prefPencapaian.EXP_KEY, stateSekarang.exp + expDiterima)
        }

        // Siapkan Animasi Peti Detak
        chestAnimation = binding.ivChestRewardMakanan.drawable as AnimationDrawable
        mulaiAnimasiDetakPeti()
        binding.btnKlaimKembaliMakanan.isEnabled = false

        // LOGIKA SAAT PETI DITEKAN
        binding.ivChestRewardMakanan.setOnClickListener {
            if (!isChestOpened) {
                isChestOpened = true
                pulseAnimator?.cancel()
                binding.ivChestRewardMakanan.scaleX = 1f
                binding.ivChestRewardMakanan.scaleY = 1f

                // 1. MAINKAN SUARA PETI EMAS TERBUKA (ORCHESTRAL WIN)
                mainkanSuara(soundOrchestralWin)

                binding.tvTapInstructionMakanan.animate().alpha(0f).setDuration(200).start()
                chestAnimation.start()

                // Tunggu sejenak sampai frame animasi peti terbuka pas, lalu tampilkan hadiah
                Handler(Looper.getMainLooper()).postDelayed({
                    tampilkanHadiahBouncy(poinDiterima, expDiterima)
                }, 800)
            }
        }

        // TOMBOL KEMBALI KE DASHBOARD & KIRIM INTENT UNTUK BUKA FRAGMENT PENCAPAIAN
        binding.btnKlaimKembaliMakanan.setOnClickListener {
            mainkanSuara(clickSoundId)
            binding.loadingOverlayMakanan.visibility = View.VISIBLE

            // HIDUPKAN KEMBALI MUSIK UTAMA REGULER SEBELUM PINDAH HALAMAN
            BackgroundMusicManager.initialize(application, R.raw.steps_in_sunlight)

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, com.example.sehatin.Main.MainActivity::class.java)

                // --- PENAMBAHAN KODE ---
                // Mengirim sinyal ke MainActivity agar membuka tab Pencapaian
                intent.putExtra("TAMPILKAN_FRAGMENT", "Pencapaian")
                // -----------------------

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }, 800)
        }
    }

    private fun mulaiAnimasiDetakPeti() {
        pulseAnimator = ValueAnimator.ofFloat(1f, 1.05f, 1f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animator ->
                val scale = animator.animatedValue as Float
                binding.ivChestRewardMakanan.scaleX = scale
                binding.ivChestRewardMakanan.scaleY = scale
            }
            start()
        }
    }

    private fun tampilkanHadiahBouncy(poinTarget: Int, expTarget: Int) {
        binding.tvCongratsTitleMakanan.animate().alpha(1f).setDuration(500).start()
        binding.tvCongratsSubtitleMakanan.animate().alpha(1f).setStartDelay(200).setDuration(500).start()

        binding.cardRewardPanelMakanan.animate()
            .translationY(0f)
            .alpha(1f)
            .setInterpolator(OvershootInterpolator(1.2f))
            .setDuration(800)
            .withEndAction {
                // =================================================================
                // 2. MAINKAN SUARA COUNTING & MULAI ANIMASI GULIR SECARA BERSAMAAN
                // =================================================================
                mainkanSuara(soundExpPoint)
                animasiAngkaBergulir(binding.tvRewardExpMakanan, expTarget, "+", " EXP")
                animasiAngkaBergulir(binding.tvRewardPoinMakanan, poinTarget, "+", " Poin")
            }
            .start()

        binding.btnKlaimKembaliMakanan.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(1000)
            .setInterpolator(OvershootInterpolator(1.5f))
            .setDuration(600)
            .withEndAction {
                binding.btnKlaimKembaliMakanan.isEnabled = true
            }
            .start()
    }

    private fun animasiAngkaBergulir(textView: android.widget.TextView, target: Int, prefix: String, suffix: String) {
        val animator = ValueAnimator.ofInt(0, target)
        animator.duration = 1500 // Durasi angka menggelinding naik (1.5 detik)
        animator.addUpdateListener { animation ->
            val nilaiSekarang = animation.animatedValue.toString()
            textView.text = "$prefix$nilaiSekarang$suffix"
        }
        animator.start()
    }

    // ========================================================
    // FUNGSI PEMANGGIL SOUND SFX DINAMIS
    // ========================================================
    private fun mainkanSuara(soundId: Int) {
        val sharedPrefs = getSharedPreferences("AudioSettings", Context.MODE_PRIVATE)
        val volumeSfxInt = sharedPrefs.getInt("VOLUME_SFX", 100)
        val volumeSfxFloat = volumeSfxInt / 100f

        if (soundId != 0) {
            soundPool?.play(soundId, volumeSfxFloat, volumeSfxFloat, 0, 0, 1f)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pulseAnimator?.cancel()
        soundPool?.release()
        soundPool = null
    }
}