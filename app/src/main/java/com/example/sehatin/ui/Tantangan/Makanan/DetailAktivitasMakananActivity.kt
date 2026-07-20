package com.example.sehatin.ui.Tantangan.Makanan

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.Utils.BackgroundMusicManager
import com.example.sehatin.databinding.ActivityDetailAktivitasMakananBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

// ==========================================
// DEKLARASI ALGORITMA FINITE STATE MACHINE (FSM)
// ==========================================
enum class TantanganState {
    BELUM_DIMULAI,
    SEDANG_BERJALAN,
    DI_JEDA,
    SELESAI,
    GAGAL,
    HADIAH_DITERIMA
}

class DetailAktivitasMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailAktivitasMakananBinding

    // State FSM Saat Ini
    private var currentState = TantanganState.BELUM_DIMULAI

    // Variabel Timer
    private var timer: CountDownTimer? = null
    private var persiapanTimer: CountDownTimer? = null // Timer khusus untuk "Get Ready"
    private var sisaWaktuMillis: Long = 0

    // Variabel Hadiah Maksimal
    private var idMisi: Int = 0
    private var rewardPoinMax: Int = 0
    private var rewardExpMax: Int = 0

    // Penampung Data Kuis
    private var daftarPertanyaan = ArrayList<String>()
    private var daftarJawabA = ArrayList<String>()
    private var daftarJawabB = ArrayList<String>()
    private var daftarJawabC = ArrayList<String>()
    private var daftarKunci = ArrayList<String>()

    // Pelacak Status Kuis
    private var soalSekarangIndex = 0
    private var sudahMenjawab = false
    private var jumlahBenar = 0

    // VARIABEL SOUNDPOOL (Untuk Suara Klik Zero-Delay)
    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAktivitasMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // =======================================================
        // A. INISIALISASI SOUNDPOOL
        // =======================================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        clickSoundId = soundPool?.load(this, R.raw.tombol_klik_sehatin, 1) ?: 0

        // =======================================================
        // B. SIAPKAN MUSIK LATAR TANTANGAN
        // =======================================================
        BackgroundMusicManager.stopMusic()
        BackgroundMusicManager.initialize(application, R.raw.bgm_tantangan)

        // 1. TANGKAP ARRAY DARI INTENT
        idMisi = intent.getIntExtra("ID_MISI", 0)
        binding.tvNamaTantanganMakanan.text = intent.getStringExtra("NAMA_MISI") ?: "Tantangan Nutrisi"

        rewardPoinMax = intent.getIntExtra("REWARD_POIN", 0)
        rewardExpMax = intent.getIntExtra("REWARD_EXP", 0)

        binding.tvPoinMakanan.text = "$rewardPoinMax Poin | $rewardExpMax EXP"

        daftarPertanyaan = intent.getStringArrayListExtra("LIST_PERTANYAAN") ?: ArrayList()
        daftarJawabA = intent.getStringArrayListExtra("LIST_A") ?: ArrayList()
        daftarJawabB = intent.getStringArrayListExtra("LIST_B") ?: ArrayList()
        daftarJawabC = intent.getStringArrayListExtra("LIST_C") ?: ArrayList()
        daftarKunci = intent.getStringArrayListExtra("LIST_KUNCI") ?: ArrayList()

        // 2. KONTROL TOMBOL UI (Pemicu Transisi FSM)
        binding.btnBackMakanan.setOnClickListener {
            mainkanSuaraKlik()
            kembaliKeLayarUtama()
        }

        binding.btnPauseMakanan.setOnClickListener {
            mainkanSuaraKlik()
            if (currentState == TantanganState.SEDANG_BERJALAN) {
                ubahState(TantanganState.DI_JEDA)
            }
        }

        binding.btnJawabanA.setOnClickListener { cekJawaban("A", binding.btnJawabanA) }
        binding.btnJawabanB.setOnClickListener { cekJawaban("B", binding.btnJawabanB) }
        binding.btnJawabanC.setOnClickListener { cekJawaban("C", binding.btnJawabanC) }

        // Tombol ini HANYA muncul di akhir kuis untuk menyelesaikan
        binding.btnActionSelesaiMakanan.setOnClickListener {
            mainkanSuaraKlik()
            if (currentState == TantanganState.SELESAI) {
                if (jumlahBenar > 0) {
                    ubahState(TantanganState.HADIAH_DITERIMA)
                } else {
                    ubahState(TantanganState.GAGAL)
                }
            }
        }

        // 3. MULAI FSM
        ubahState(TantanganState.BELUM_DIMULAI)
    }

    // ========================================================
    // IMPLEMENTASI TRANSISI FSM & AUDIO
    // ========================================================
    private fun ubahState(newState: TantanganState) {
        currentState = newState

        when (newState) {
            TantanganState.BELUM_DIMULAI -> {
                soalSekarangIndex = 0
                jumlahBenar = 0
                sisaWaktuMillis = daftarPertanyaan.size * 15000L // 15 Detik per soal
                BackgroundMusicManager.pauseMusic() // Matikan musik saat aba-aba
                mulaiGetReadyOverlay() // Panggil hitung mundur persiapan
            }
            TantanganState.SEDANG_BERJALAN -> {
                binding.overlayGetReady.visibility = View.GONE
                BackgroundMusicManager.resume() // Nyalakan BGM saat kuis mulai
                tampilkanSoal(soalSekarangIndex)
                mulaiTimer()
            }
            TantanganState.DI_JEDA -> {
                jedaSemuaTimer()
                BackgroundMusicManager.pauseMusic()
                tampilkanDialogJeda()
            }
            TantanganState.SELESAI -> {
                jedaSemuaTimer()
                BackgroundMusicManager.stopMusic()
                binding.btnActionSelesaiMakanan.text = "Selesaikan Tantangan"
                binding.btnActionSelesaiMakanan.visibility = View.VISIBLE
            }
            TantanganState.GAGAL -> {
                jedaSemuaTimer()
                BackgroundMusicManager.stopMusic()
                tampilkanDialogGagal()
            }
            TantanganState.HADIAH_DITERIMA -> {
                lanjutKeCongratulations()
            }
        }
    }

    // ========================================================
    // LOGIKA TIMER & KUIS
    // ========================================================
    private fun mulaiGetReadyOverlay() {
        binding.overlayGetReady.visibility = View.VISIBLE
        binding.tvAngkaCountdown.text = "3"

        persiapanTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val detik = (millisUntilFinished / 1000) + 1
                binding.tvAngkaCountdown.text = detik.toString()
            }

            override fun onFinish() {
                binding.tvAngkaCountdown.text = "GO!"
                Handler(Looper.getMainLooper()).postDelayed({
                    ubahState(TantanganState.SEDANG_BERJALAN)
                }, 500)
            }
        }.start()
    }

    private fun mulaiTimer() {
        timer = object : CountDownTimer(sisaWaktuMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                sisaWaktuMillis = millisUntilFinished
                val menit = (millisUntilFinished / 1000) / 60
                val detik = (millisUntilFinished / 1000) % 60
                binding.tvTimerKuis.text = String.format("%02d:%02d", menit, detik)
            }

            override fun onFinish() {
                binding.tvTimerKuis.text = "00:00"
                ubahState(TantanganState.GAGAL)
            }
        }.start()
    }

    private fun jedaSemuaTimer() {
        timer?.cancel()
        persiapanTimer?.cancel()
    }

    private fun tampilkanSoal(index: Int) {
        binding.tvPertanyaanKuis.text = "Soal ${index + 1}/${daftarPertanyaan.size}\n${daftarPertanyaan[index]}"
        binding.tvJawabanA.text = daftarJawabA[index]
        binding.tvJawabanB.text = daftarJawabB[index]
        binding.tvJawabanC.text = daftarJawabC[index]

        sudahMenjawab = false
        binding.btnActionSelesaiMakanan.visibility = View.GONE
        resetWarnaTombol()
    }

    private fun resetWarnaTombol() {
        val abuAbu = Color.parseColor("#9E9E9E")
        val abuMuda = Color.parseColor("#F5F5F5")
        binding.btnJawabanA.setStrokeColor(abuAbu); binding.btnJawabanA.setCardBackgroundColor(abuMuda)
        binding.btnJawabanB.setStrokeColor(abuAbu); binding.btnJawabanB.setCardBackgroundColor(abuMuda)
        binding.btnJawabanC.setStrokeColor(abuAbu); binding.btnJawabanC.setCardBackgroundColor(abuMuda)
    }

    private fun cekJawaban(pilihan: String, btnPilihan: MaterialCardView) {
        if (sudahMenjawab || currentState != TantanganState.SEDANG_BERJALAN) return

        mainkanSuaraKlik()
        sudahMenjawab = true
        val kunciJawaban = daftarKunci[soalSekarangIndex]

        if (pilihan == kunciJawaban) {
            jumlahBenar++
            btnPilihan.setStrokeColor(Color.parseColor("#4CAF50"))
            btnPilihan.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
        } else {
            btnPilihan.setStrokeColor(Color.parseColor("#F44336"))
            btnPilihan.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isDestroyed && currentState == TantanganState.SEDANG_BERJALAN) {
                if (soalSekarangIndex == daftarPertanyaan.size - 1) {
                    ubahState(TantanganState.SELESAI)
                } else {
                    soalSekarangIndex++
                    tampilkanSoal(soalSekarangIndex)
                }
            }
        }, 1000)
    }

    // ========================================================
    // LOGIKA DIALOG & FUNGSI PENDUKUNG LAINNYA
    // ========================================================
    private fun mainkanSuaraKlik() {
        val sharedPrefs = getSharedPreferences("AudioSettings", Context.MODE_PRIVATE)
        val volumeSfxInt = sharedPrefs.getInt("VOLUME_SFX", 100)
        val volumeSfxFloat = volumeSfxInt / 100f
        soundPool?.play(clickSoundId, volumeSfxFloat, volumeSfxFloat, 0, 0, 1f)
    }

    private fun kembaliKeLayarUtama() {
        BackgroundMusicManager.stopMusic()
        BackgroundMusicManager.initialize(application, R.raw.steps_in_sunlight)
        finish()
    }

    private fun tampilkanDialogJeda() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_jeda, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<MaterialButton>(R.id.btn_lanjutkan).setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
            mulaiGetReadyOverlay() // Memunculkan aba-aba sebelum lanjut
        }
        dialogView.findViewById<MaterialButton>(R.id.btn_mulai_ulang).setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
            ubahState(TantanganState.BELUM_DIMULAI)
        }
        dialogView.findViewById<MaterialButton>(R.id.btn_keluar).setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
            kembaliKeLayarUtama()
        }
        dialog.show()
    }

    private fun tampilkanDialogGagal() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_jeda, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnLanjut = dialogView.findViewById<MaterialButton>(R.id.btn_lanjutkan)
        btnLanjut.visibility = View.GONE

        val btnUlang = dialogView.findViewById<MaterialButton>(R.id.btn_mulai_ulang)

        if (jumlahBenar == 0 && soalSekarangIndex == daftarPertanyaan.size - 1) {
            btnUlang.text = "Skor 0! Coba Lagi"
        } else {
            btnUlang.text = "Waktu Habis! Coba Lagi"
        }

        btnUlang.setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
            ubahState(TantanganState.BELUM_DIMULAI)
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_keluar).setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
            kembaliKeLayarUtama()
        }
        dialog.show()
    }

    private fun lanjutKeCongratulations() {
        val persentaseBenar = jumlahBenar.toFloat() / daftarPertanyaan.size.toFloat()
        val finalPoin = (rewardPoinMax * persentaseBenar).toInt()
        val finalExp = (rewardExpMax * persentaseBenar).toInt()

        if (idMisi != 0) {
            val userPref = UserPreference(this)
            val userKey = userPref.getName() ?: "guest_user"

            val prefMakanan = MakananPreferences.getInstance(applicationContext.dataStoreMakanan)
            val factory = MakananViewModelFactory(MakananRepository(prefMakanan))
            val viewModelMakanan = ViewModelProvider(this, factory)[MakananViewModel::class.java]

            viewModelMakanan.simpanMisiSelesai(userKey, idMisi)
        }

        BackgroundMusicManager.initialize(application, R.raw.steps_in_sunlight)

        val intentLanjut = Intent(this, CongratulationsMakananActivity::class.java).apply {
            putExtra("HASIL_POIN", finalPoin)
            putExtra("HASIL_EXP", finalExp)
        }
        startActivity(intentLanjut)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        jedaSemuaTimer()
        soundPool?.release()
        soundPool = null
    }
}