package com.example.sehatin.ui.Tantangan.Makanan

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.sehatin.Data.Model.UserPreference
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCongratulationsMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tangkap Poin & EXP dari halaman Kuis sebelumnya
        val expDiterima = intent.getIntExtra("HASIL_EXP", 0)
        val poinDiterima = intent.getIntExtra("HASIL_POIN", 0)

        // 1. SIMPAN KE BANK PUSAT TANTANGAN
        val userPref = UserPreference(this)
        val userKey = userPref.getName() ?: "guest_user"

        val prefTantangan = TantanganPreferences.getInstance(applicationContext.dataStoreTantangan)
        val factory = TantanganViewModelFactory(TantanganRepository(prefTantangan))
        viewModel = ViewModelProvider(this, factory)[TantanganViewModel::class.java]

        viewModel.tambahExp(userKey, expDiterima)
        viewModel.tambahPoin(userKey, poinDiterima)

        // ========================================================
        // 2. SENSOR PENCAPAIAN: Kuis Makanan (+1), Akumulasi Poin & EXP
        // (Ini adalah bagian yang tersambung ke Fragment Pencapaian)
        // ========================================================
        val prefPencapaian = PencapaianPreferences.getInstance(applicationContext.dataStorePencapaian)
        val factoryPencapaian = PencapaianViewModelFactory(PencapaianRepository(prefPencapaian))
        val viewModelPencapaian = ViewModelProvider(this, factoryPencapaian)[PencapaianViewModel::class.java]

        lifecycleScope.launch {
            // Ambil data progres saat ini dari brankas Pencapaian
            val stateSekarang = prefPencapaian.getPencapaianProgress().first()

            // Tambah Lencana "Si Paling Paham Nutrisi" (+1)
            viewModelPencapaian.updateProgress(prefPencapaian.MAKANAN_KEY, stateSekarang.makanan + 1)

            // Tambah Lencana "Sultan Poin" & "Level Up" sesuai hadiah yang didapat
            viewModelPencapaian.updateProgress(prefPencapaian.POIN_KEY, stateSekarang.poin + poinDiterima)
            viewModelPencapaian.updateProgress(prefPencapaian.EXP_KEY, stateSekarang.exp + expDiterima)
        }
        // ========================================================

        // Siapkan Animasi Peti
        chestAnimation = binding.ivChestRewardMakanan.drawable as AnimationDrawable
        mulaiAnimasiDetakPeti()
        binding.btnKlaimKembaliMakanan.isEnabled = false

        // LOGIKA SAAT PETI DITEKAN
        binding.ivChestRewardMakanan.setOnClickListener {
            if (!isChestOpened) {
                isChestOpened = true
                pulseAnimator?.cancel() // Hentikan efek detak
                binding.ivChestRewardMakanan.scaleX = 1f // Kembalikan ukuran normal
                binding.ivChestRewardMakanan.scaleY = 1f

                // Sembunyikan instruksi tap
                binding.tvTapInstructionMakanan.animate().alpha(0f).setDuration(200).start()

                // Mulai memutar sequence gambar peti terbuka
                chestAnimation.start()

                // Tunggu sampai animasi gambar selesai, lalu munculkan kartu hadiah
                Handler(Looper.getMainLooper()).postDelayed({
                    tampilkanHadiahBouncy(poinDiterima, expDiterima)
                }, 800)
            }
        }

        // TOMBOL KEMBALI KE DASHBOARD
        binding.btnKlaimKembaliMakanan.setOnClickListener {
            binding.loadingOverlayMakanan.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, com.example.sehatin.Main.MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }, 800)
        }
    }

    // ==========================================
    // FUNGSI ANIMASI KUSTOM
    // ==========================================

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
        // 1. Munculkan Judul & Subjudul
        binding.tvCongratsTitleMakanan.animate().alpha(1f).setDuration(500).start()
        binding.tvCongratsSubtitleMakanan.animate().alpha(1f).setStartDelay(200).setDuration(500).start()

        // 2. Kartu Hadiah melompat naik (Overshoot)
        binding.cardRewardPanelMakanan.animate()
            .translationY(0f)
            .alpha(1f)
            .setInterpolator(OvershootInterpolator(1.2f)) // Efek membal
            .setDuration(800)
            .withEndAction {
                // Setelah kartu muncul, mulai hitung angkanya berputar
                animasiAngkaBergulir(binding.tvRewardExpMakanan, expTarget, "+", " EXP")
                animasiAngkaBergulir(binding.tvRewardPoinMakanan, poinTarget, "+", " Poin")
            }
            .start()

        // 3. Tombol Klaim membesar dan muncul
        binding.btnKlaimKembaliMakanan.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(1000) // Tunggu kartu selesai animasi
            .setInterpolator(OvershootInterpolator(1.5f))
            .setDuration(600)
            .withEndAction {
                binding.btnKlaimKembaliMakanan.isEnabled = true
            }
            .start()
    }

    private fun animasiAngkaBergulir(textView: android.widget.TextView, target: Int, prefix: String, suffix: String) {
        val animator = ValueAnimator.ofInt(0, target)
        animator.duration = 1500 // Lama angka berputar (1.5 detik)
        animator.addUpdateListener { animation ->
            val nilaiSekarang = animation.animatedValue.toString()
            textView.text = "$prefix$nilaiSekarang$suffix"
        }
        animator.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        pulseAnimator?.cancel() // Amankan dari memory leak
    }
}