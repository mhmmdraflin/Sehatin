package com.example.sehatin.ui.Tantangan.Olahraga

import android.animation.ValueAnimator // IMPORT BARU UNTUK ANIMASI ANGKA
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.Utils.BackgroundMusicManager
import com.example.sehatin.ui.Tantangan.TantanganPreferences
import com.example.sehatin.ui.Tantangan.TantanganRepository
import com.example.sehatin.ui.Tantangan.TantanganViewModel
import com.example.sehatin.ui.Tantangan.TantanganViewModelFactory
import com.example.sehatin.ui.Tantangan.dataStoreTantangan
import com.example.sehatin.ui.Pencapaian.PencapaianPreferences
import com.example.sehatin.ui.Pencapaian.PencapaianRepository
import com.example.sehatin.ui.Pencapaian.PencapaianViewModel
import com.example.sehatin.ui.Pencapaian.PencapaianViewModelFactory
import com.example.sehatin.ui.Pencapaian.dataStorePencapaian
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// =======================================================
// IMPORT BARU: Mesin Olahraga (Untuk menyimpan Kalori)
// =======================================================
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaPreferences
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaRepository
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaViewModel
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaViewModelFactory
import com.example.sehatin.ui.SideFeature.Olahraga.dataStore

class CongratulationsActivity : AppCompatActivity() {

    private lateinit var viewModel: TantanganViewModel
    private var isChestOpened = false

    // VARIABEL SOUNDPOOL (Untuk Efek Suara Berlapis)
    private var soundPool: SoundPool? = null
    private var soundOrchestralWin: Int = 0
    private var soundExpPoint: Int = 0
    private var clickSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congratulations)

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
            .setMaxStreams(3) // Izinkan 3 suara tumpang tindih
            .setAudioAttributes(audioAttributes)
            .build()

        soundOrchestralWin = soundPool?.load(this, R.raw.orchestral_win, 1) ?: 0
        soundExpPoint = soundPool?.load(this, R.raw.sound_effect_exp_point, 1) ?: 0
        clickSoundId = soundPool?.load(this, R.raw.tombol_klik_sehatin, 1) ?: 0
        // =======================================================

        val ivChest = findViewById<ImageView>(R.id.iv_chest_reward)
        val tvInstruksi = findViewById<TextView>(R.id.tv_tap_instruction)
        val cardReward = findViewById<MaterialCardView>(R.id.card_reward_panel)
        val btnKlaim = findViewById<MaterialButton>(R.id.btn_klaim_kembali)
        val tvExp = findViewById<TextView>(R.id.tv_reward_exp)
        val tvPoin = findViewById<TextView>(R.id.tv_reward_poin)

        val expDiterima = intent.getIntExtra("HASIL_EXP", 0)
        val poinDiterima = intent.getIntExtra("HASIL_POIN", 0)
        val kaloriTerbakar = intent.getIntExtra("HASIL_KALORI", 50)
        val namaMisiOlahraga = intent.getStringExtra("HASIL_NAMA_MISI") ?: ""

        // SIMPAN STATUS SELESAI KE MEMORI AGAR GEMBOK TERBUKA
        // SIMPAN STATUS SELESAI KE MEMORI AGAR GEMBOK TERBUKA KHUSUS AKUN INI
        if (namaMisiOlahraga.isNotEmpty()) {
            val userPref = UserPreference(this)
            val userKey = userPref.getName() ?: "guest_user"

            val progressPrefs = getSharedPreferences("OlahragaProgressPrefs_$userKey", Context.MODE_PRIVATE)
            progressPrefs.edit().putBoolean(namaMisiOlahraga, true).apply()
        }

        // Set teks awal menjadi 0, karena akan dianimasikan ke angka target
        tvExp.text = "+0 EXP"
        tvPoin.text = "+0 Poin"

        // ==========================================
        // AMBIL IDENTITAS USER & SIMPAN REWARD
        // ==========================================
        val userPref = UserPreference(this)
        val userKey = userPref.getName() ?: "guest_user"

        val prefTantangan = TantanganPreferences.getInstance(applicationContext.dataStoreTantangan)
        val factory = TantanganViewModelFactory(TantanganRepository(prefTantangan))
        viewModel = ViewModelProvider(this, factory)[TantanganViewModel::class.java]

        viewModel.tambahExp(userKey, expDiterima)
        viewModel.tambahPoin(userKey, poinDiterima)

        val prefOlahraga = OlahragaPreferences.getInstance(applicationContext.dataStore)
        val factoryOlahraga = OlahragaViewModelFactory(OlahragaRepository(prefOlahraga))
        val olahragaViewModel = ViewModelProvider(this, factoryOlahraga)[OlahragaViewModel::class.java]

        olahragaViewModel.tambahKaloriDanExp(kaloriTerbakar, 0)

        val prefPencapaian = PencapaianPreferences.getInstance(applicationContext.dataStorePencapaian)
        val factoryPencapaian = PencapaianViewModelFactory(PencapaianRepository(prefPencapaian))
        val viewModelPencapaian = ViewModelProvider(this, factoryPencapaian)[PencapaianViewModel::class.java]

        lifecycleScope.launch {
            val stateSekarang = prefPencapaian.getPencapaianProgress().first()

            if (namaMisiOlahraga.contains("Push Up", ignoreCase = true)) {
                viewModelPencapaian.updateProgress(prefPencapaian.PUSHUP_KEY, 1)
            } else if (namaMisiOlahraga.contains("Plank", ignoreCase = true)) {
                viewModelPencapaian.updateProgress(prefPencapaian.PLANK_KEY, stateSekarang.plank + 1)
            }

            viewModelPencapaian.updateProgress(prefPencapaian.POIN_KEY, stateSekarang.poin + poinDiterima)
            viewModelPencapaian.updateProgress(prefPencapaian.EXP_KEY, stateSekarang.exp + expDiterima)
        }

        btnKlaim.isEnabled = false

        // ========================================================
        // LOGIKA ANIMASI & SUARA PETI HADIAH
        // ========================================================
        ivChest.setOnClickListener {
            if (!isChestOpened) {
                isChestOpened = true
                tvInstruksi.visibility = View.GONE

                // 1. MAINKAN SUARA PETI DIBUKA (ORCHESTRAL WIN)
                mainkanSuara(soundOrchestralWin)

                val chestAnimation = ivChest.drawable as? AnimationDrawable
                if (chestAnimation != null) {
                    chestAnimation.stop()
                    chestAnimation.start()
                }

                // 2. MUNCULKAN KARTU POIN DAN EXP
                cardReward.animate().alpha(1f).setDuration(500).start()

                // 3. MULAI ANIMASI ANGKA & SUARA POIN SECARA BERSAMAAN SETELAH JEDA
                Handler(Looper.getMainLooper()).postDelayed({
                    mainkanSuara(soundExpPoint)

                    // Memanggil fungsi animasi untuk menghitung dari 0 ke angka target
                    mulaiAnimasiAngka(tvExp, expDiterima, "EXP")
                    mulaiAnimasiAngka(tvPoin, poinDiterima, "Poin")

                }, 400) // Mundur 400ms agar pas saat kartu muncul ke layar

                btnKlaim.animate().alpha(1f).setDuration(500).withEndAction {
                    btnKlaim.isEnabled = true
                }.start()
            }
        }

        // ========================================================
        // KLAIM BUTTON & REDIRECT KE FRAGMENT TANTANGAN
        // ========================================================
        btnKlaim.setOnClickListener {
            mainkanSuara(clickSoundId)

            // HIDUPKAN KEMBALI MUSIK UTAMA SEBELUM PINDAH HALAMAN
            BackgroundMusicManager.initialize(application, R.raw.steps_in_sunlight)

            val intent = Intent(this, com.example.sehatin.Main.MainActivity::class.java)

            // --- PENAMBAHAN KODE ---
            // Mengirim sinyal ke MainActivity agar membuka tab Tantangan
            intent.putExtra("TAMPILKAN_FRAGMENT", "Tantangan")
            // -----------------------

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    // ========================================================
    // FUNGSI ANIMASI COUNTING ANGKA
    // ========================================================
    private fun mulaiAnimasiAngka(textView: TextView, targetValue: Int, suffix: String) {
        val animator = ValueAnimator.ofInt(0, targetValue)
        animator.duration = 1500 // Durasi efek counting (1.5 Detik)

        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            textView.text = "+$animatedValue $suffix"
        }

        animator.start()
    }

    // ========================================================
    // FUNGSI PEMANGGIL SUARA DINAMIS
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
        soundPool?.release()
        soundPool = null
    }
}