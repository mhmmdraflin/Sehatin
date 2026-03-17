package com.example.sehatin.ui.SideFeature

import android.content.Intent
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

    private var genderPilihan = "Laki-laki" // Default jenis kelamin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBodyMassIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTombolKembali()
        setupCounterBeratDanTinggi()
        setupPemilihanGender()
        setupTombolHitung()
    }

    private fun setupTombolKembali() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupCounterBeratDanTinggi() {
        // --- LOGIKA BERAT BADAN ---
        binding.btnPlusBerat.setOnClickListener {
            val currentBerat = binding.etValBerat.text.toString().toIntOrNull() ?: 40
            if (currentBerat < 250) {
                binding.etValBerat.setText((currentBerat + 1).toString())
            }
        }

        binding.btnMinBeratKiri.setOnClickListener {
            val currentBerat = binding.etValBerat.text.toString().toIntOrNull() ?: 40
            if (currentBerat > 20) {
                binding.etValBerat.setText((currentBerat - 1).toString())
            }
        }

        // --- LOGIKA TINGGI BADAN ---
        binding.btnPlusTinggi.setOnClickListener {
            val currentTinggi = binding.etValTinggi.text.toString().toIntOrNull() ?: 170
            if (currentTinggi < 250) {
                binding.etValTinggi.setText((currentTinggi + 1).toString())
            }
        }

        binding.btnMinTinggi.setOnClickListener {
            val currentTinggi = binding.etValTinggi.text.toString().toIntOrNull() ?: 170
            if (currentTinggi > 50) {
                binding.etValTinggi.setText((currentTinggi - 1).toString())
            }
        }
    }

    private fun setupPemilihanGender() {
        pilihGenderLakiLaki()

        binding.cardGenderMale.setOnClickListener {
            genderPilihan = "Laki-laki"
            pilihGenderLakiLaki()
        }

        binding.cardGenderFemale.setOnClickListener {
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

    private fun setupTombolHitung() {
        binding.btnHitungBmi.setOnClickListener {
            val nama = binding.etNama.text.toString().trim()
            val umurString = binding.etUmur.text.toString().trim()

            // Ambil berat dan tinggi langsung dari inputan user
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
            // SENSOR PENCAPAIAN: Lencana BMI
            // ========================================================
            val prefPencapaian = PencapaianPreferences.getInstance(applicationContext.dataStorePencapaian)
            val factoryPencapaian = PencapaianViewModelFactory(PencapaianRepository(prefPencapaian))
            val viewModelPencapaian = ViewModelProvider(this, factoryPencapaian)[PencapaianViewModel::class.java]

            // Ubah progress BMI menjadi 1 (Maksimal)
            viewModelPencapaian.updateProgress(prefPencapaian.BMI_KEY, 1)

            // Kirim Data ke Halaman Hasil
            val intent = Intent(this, HasilBodyMassIndexActivity::class.java).apply {
                putExtra("EXTRA_NAMA", nama)
                putExtra("EXTRA_UMUR", umurString)
                putExtra("EXTRA_TINGGI", tinggiBadan)
                putExtra("EXTRA_BERAT", beratBadan)
                putExtra("EXTRA_GENDER", genderPilihan)
                putExtra("EXTRA_BMI_SCORE", bmiScore.toFloat())
            }
            startActivity(intent)
        }
    }
}