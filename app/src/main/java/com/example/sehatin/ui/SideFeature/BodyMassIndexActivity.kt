package com.example.sehatin.ui.SideFeature

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.R
import com.example.sehatin.databinding.ActivityBodyMassIndexBinding
import com.example.sehatin.ui.Pencapaian.PencapaianPreferences
import com.example.sehatin.ui.Pencapaian.PencapaianRepository
import com.example.sehatin.ui.Pencapaian.PencapaianViewModel
import com.example.sehatin.ui.Pencapaian.PencapaianViewModelFactory
import com.example.sehatin.ui.Pencapaian.dataStorePencapaian
import kotlin.math.pow

class BodyMassIndexActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBodyMassIndexBinding

    private var genderPilihan = "Laki-laki"
    private var standarPilihan = "Asia"

    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBodyMassIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // =======================================================
        // INISIALISASI SOUNDPOOL
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

        setupTombolKembali()
        setupCounterBeratDanTinggi()
        setupPemilihanGender()
        setupPemilihanStandar()
        setupTombolHitung()
    }

    private fun mainkanSuaraKlik() {
        soundPool?.play(clickSoundId, 1f, 1f, 0, 0, 1f)
    }

    private fun setupTombolKembali() {
        binding.btnBack.setOnClickListener {
            mainkanSuaraKlik()
            finish()
        }
    }

    private fun setupCounterBeratDanTinggi() {
        // --- LOGIKA BERAT BADAN ---
        binding.btnPlusBerat.setOnClickListener {
            mainkanSuaraKlik()
            val currentBerat = binding.etValBerat.text.toString().toIntOrNull() ?: 40
            if (currentBerat < 250) {
                binding.etValBerat.setText((currentBerat + 1).toString())
            }
        }

        binding.btnMinBeratKiri.setOnClickListener {
            mainkanSuaraKlik()
            val currentBerat = binding.etValBerat.text.toString().toIntOrNull() ?: 40
            if (currentBerat > 20) {
                binding.etValBerat.setText((currentBerat - 1).toString())
            }
        }

        // --- LOGIKA TINGGI BADAN ---
        binding.btnPlusTinggi.setOnClickListener {
            mainkanSuaraKlik()
            val currentTinggi = binding.etValTinggi.text.toString().toIntOrNull() ?: 170
            if (currentTinggi < 250) {
                binding.etValTinggi.setText((currentTinggi + 1).toString())
            }
        }

        binding.btnMinTinggi.setOnClickListener {
            mainkanSuaraKlik()
            val currentTinggi = binding.etValTinggi.text.toString().toIntOrNull() ?: 170
            if (currentTinggi > 50) {
                binding.etValTinggi.setText((currentTinggi - 1).toString())
            }
        }
    }

    private fun setupPemilihanGender() {
        pilihGenderLakiLaki()

        binding.cardGenderMale.setOnClickListener {
            mainkanSuaraKlik()
            genderPilihan = "Laki-laki"
            pilihGenderLakiLaki()
        }

        binding.cardGenderFemale.setOnClickListener {
            mainkanSuaraKlik()
            genderPilihan = "Perempuan"
            pilihGenderPerempuan()
        }
    }

    private fun pilihGenderLakiLaki() {
        binding.cardGenderMale.strokeColor = ContextCompat.getColor(this, R.color.warnaUtama)
        binding.cardGenderFemale.strokeColor = ContextCompat.getColor(this, R.color.black)
    }

    private fun pilihGenderPerempuan() {
        binding.cardGenderFemale.strokeColor = ContextCompat.getColor(this, R.color.warnaUtama)
        binding.cardGenderMale.strokeColor = ContextCompat.getColor(this, R.color.black)
    }

    private fun setupPemilihanStandar() {
        pilihStandarAsia()

        binding.cardStandarAsia.setOnClickListener {
            mainkanSuaraKlik()
            standarPilihan = "Asia"
            pilihStandarAsia()
        }

        binding.cardStandarGlobal.setOnClickListener {
            mainkanSuaraKlik()
            standarPilihan = "Western"
            pilihStandarGlobal()
        }
    }

    private fun pilihStandarAsia() {
        binding.cardStandarAsia.strokeColor = ContextCompat.getColor(this, R.color.warnaUtama)
        binding.cardStandarGlobal.strokeColor = ContextCompat.getColor(this, R.color.black)
    }

    private fun pilihStandarGlobal() {
        binding.cardStandarGlobal.strokeColor = ContextCompat.getColor(this, R.color.warnaUtama)
        binding.cardStandarAsia.strokeColor = ContextCompat.getColor(this, R.color.black)
    }

    private fun setupTombolHitung() {
        binding.btnHitungBmi.setOnClickListener {
            mainkanSuaraKlik()

            val nama = binding.etNama.text.toString().trim()
            val umurString = binding.etUmur.text.toString().trim()

            val beratBadan = binding.etValBerat.text.toString().toIntOrNull() ?: 0
            val tinggiBadan = binding.etValTinggi.text.toString().toIntOrNull() ?: 0

            // Validasi Input Kosong atau 0
            if (nama.isEmpty()) {
                binding.etNama.error = "Nama tidak boleh kosong"
                binding.etNama.requestFocus()
                return@setOnClickListener
            }
            if (umurString.isEmpty()) {
                binding.etUmur.error = "Umur tidak boleh kosong"
                binding.etUmur.requestFocus()
                return@setOnClickListener
            }
            if (beratBadan <= 0) {
                binding.etValBerat.error = "Isi berat badan"
                return@setOnClickListener
            }
            if (tinggiBadan <= 0) {
                binding.etValTinggi.error = "Isi tinggi badan"
                return@setOnClickListener
            }

            // Perhitungan BMI
            val tinggiDalamMeter = tinggiBadan / 100.0
            val bmiScore = beratBadan / tinggiDalamMeter.pow(2.0)

            // ========================================================
            // SENSOR PENCAPAIAN: Lencana BMI (PERBAIKAN)
            // ========================================================
            // 1. Tulis catatan di SharedPreferences agar PencapaianFragment berhenti me-reset nilainya
            val sharedPrefs = getSharedPreferences("BMI_Prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putBoolean("hasCheckedBMI", true).apply()

            // 2. Simpan nilai ke DataStore Pencapaian
            val prefPencapaian = PencapaianPreferences.getInstance(applicationContext.dataStorePencapaian)
            val factoryPencapaian = PencapaianViewModelFactory(PencapaianRepository(prefPencapaian))
            val viewModelPencapaian = ViewModelProvider(this, factoryPencapaian)[PencapaianViewModel::class.java]

            // Ubah progress BMI menjadi 1 (Maksimal)
            viewModelPencapaian.updateProgress(prefPencapaian.BMI_KEY, 1)

            // ========================================================

            // Kirim Data ke Halaman Hasil
            val intent = Intent(this, HasilBodyMassIndexActivity::class.java).apply {
                putExtra("EXTRA_NAMA", nama)
                putExtra("EXTRA_UMUR", umurString)
                putExtra("EXTRA_TINGGI", tinggiBadan)
                putExtra("EXTRA_BERAT", beratBadan)
                putExtra("EXTRA_GENDER", genderPilihan)
                putExtra("EXTRA_STANDAR", standarPilihan)
                putExtra("EXTRA_BMI_SCORE", bmiScore.toFloat())
            }
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool?.release()
        soundPool = null
    }
}