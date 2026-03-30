package com.example.sehatin.ui.SideFeature

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.databinding.ActivityHasilBodyMassIndexBinding

class HasilBodyMassIndexActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHasilBodyMassIndexBinding

    // Variabel penampung untuk disimpan ke Profil nanti
    private lateinit var kategoriBMI: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHasilBodyMassIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tampilkanDataHasil()
        setupTombolKembali()
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun tampilkanDataHasil() {
        val nama = intent.getStringExtra("EXTRA_NAMA") ?: "Sobat"
        val umur = intent.getStringExtra("EXTRA_UMUR") ?: "0"
        val tinggi = intent.getIntExtra("EXTRA_TINGGI", 0)
        val berat = intent.getIntExtra("EXTRA_BERAT", 0)
        val gender = intent.getStringExtra("EXTRA_GENDER") ?: "Laki-laki"
        val bmiScore = intent.getFloatExtra("EXTRA_BMI_SCORE", 0f)

        binding.tvHasilNama.text = nama
        binding.tvHasilGender.text = gender
        binding.tvHasilUmur.text = "$umur Tahun"
        binding.tvHasilTinggi.text = "$tinggi cm"
        binding.tvHasilBerat.text = "$berat kg"
        binding.tvHasilSkor.text = String.format("%.1f", bmiScore)

        val range: String
        val deskripsi: String
        val imageRes: Int

        if (gender == "Laki-laki") {
            when {
                bmiScore < 17.0f -> {
                    kategoriBMI = "Kurus"
                    range = "Skor BMI: < 17"
                    deskripsi = "Tubuhmu membutuhkan lebih banyak nutrisi. Tingkatkan asupan makanan bergizi yang tinggi protein dan karbohidrat kompleks."
                    imageRes = R.drawable.character_boy_lebih_kurus
                }
                bmiScore < 23.0f -> {
                    kategoriBMI = "Normal (Ideal)"
                    range = "Skor BMI: 17 - 23"
                    deskripsi = "Luar biasa! Berat badanmu berada dalam rentang yang sangat sehat dan ideal. Pertahankan rutinitasmu!"
                    imageRes = R.drawable.character_ideal
                }
                bmiScore <= 27.0f -> {
                    kategoriBMI = "Gemuk"
                    range = "Skor BMI: 23 - 27"
                    deskripsi = "Berat badanmu sedikit di atas ideal. Ini waktu yang tepat untuk mulai melakukan defisit kalori ringan."
                    imageRes = R.drawable.character_boy_gemuk
                }
                else -> {
                    kategoriBMI = "Obesitas"
                    range = "Skor BMI: > 27"
                    deskripsi = "Perhatian! Kamu berada di kategori obesitas. Sangat disarankan untuk memulai program defisit kalori yang disiplin."
                    imageRes = R.drawable.character_boy_obesitas
                }
            }
        } else {
            when {
                bmiScore < 18.0f -> {
                    kategoriBMI = "Kurus"
                    range = "Skor BMI: < 18"
                    deskripsi = "Tubuhmu membutuhkan lebih banyak nutrisi. Latihan kekuatan juga sangat bagus untuk membangun otot agar tubuh lebih bugar!"
                    imageRes = R.drawable.character_girl_lebih_kurus
                }
                bmiScore < 25.0f -> {
                    kategoriBMI = "Normal (Ideal)"
                    range = "Skor BMI: 18 - 25"
                    deskripsi = "Luar biasa! Berat badanmu berada dalam rentang yang sangat sehat dan ideal."
                    imageRes = R.drawable.character_girl_ideal
                }
                bmiScore <= 27.0f -> {
                    kategoriBMI = "Gemuk"
                    range = "Skor BMI: 25 - 27"
                    deskripsi = "Berat badanmu sedikit di atas ideal. Perbanyak latihan kardio untuk membakar lemak berlebih."
                    imageRes = R.drawable.character_girl_gemuk
                }
                else -> {
                    kategoriBMI = "Obesitas"
                    range = "Skor BMI: > 27"
                    deskripsi = "Perhatian! Kamu berada di kategori obesitas yang berisiko bagi kesehatan. Mulailah defisit kalori."
                    imageRes = R.drawable.character_girl_obesitas
                }
            }
        }

        binding.tvKategoriBmi.text = kategoriBMI
        binding.tvRangeBmi.text = range
        binding.tvDeskripsiBmi.text = deskripsi

        try {
            binding.ivSilhouette.setImageResource(imageRes)
        } catch (e: Exception) {
            binding.ivSilhouette.setImageResource(R.drawable.ic_character_boy)
        }
    }

    private fun setupTombolKembali() {
        // Tombol Back Kiri Atas (Hanya kembali ke kalkulator, tidak menyimpan ke profil)
        binding.btnBack.setOnClickListener { finish() }

        // Ubah Teks Tombol Bawah agar User Paham Fungsinya
        binding.btnKembaliDashboard.text = "Terapkan ke Profil & Dashboard"

        binding.btnKembaliDashboard.setOnClickListener {
            // 1. Ambil data dari Intent yang tadi dikirim
            val umur = intent.getStringExtra("EXTRA_UMUR") ?: "0"
            val tinggi = intent.getIntExtra("EXTRA_TINGGI", 0)
            val berat = intent.getIntExtra("EXTRA_BERAT", 0)
            val gender = intent.getStringExtra("EXTRA_GENDER") ?: "Laki-laki"

            // Konversi teks gender menjadi kode "L" atau "P" untuk database
            val kodeGender = if (gender == "Laki-laki") "L" else "P"

            // 2. SIMPAN KE DATABASE LOKAL (UserPreference)
            val userPref = UserPreference(this)
            userPref.setUserBody(umur, tinggi.toString(), berat.toString(), kodeGender)
            userPref.setKondisiTubuh(kategoriBMI) // Menyimpan "Kurus", "Gemuk", dll.

            Toast.makeText(this, "Profil fisik berhasil diperbarui!", Toast.LENGTH_SHORT).show()

            // 3. Kembali ke Dashboard Utama
            val intent = Intent(this, com.example.sehatin.Main.MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}