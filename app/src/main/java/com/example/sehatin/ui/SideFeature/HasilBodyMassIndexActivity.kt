package com.example.sehatin.ui.SideFeature

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sehatin.R
import com.example.sehatin.databinding.ActivityHasilBodyMassIndexBinding

class HasilBodyMassIndexActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHasilBodyMassIndexBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHasilBodyMassIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tampilkanDataHasil()
        setupTombolKembali()
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun tampilkanDataHasil() {
        // 1. Menangkap Data dari Halaman Sebelumnya
        val nama = intent.getStringExtra("EXTRA_NAMA") ?: "Sobat"
        val umur = intent.getStringExtra("EXTRA_UMUR") ?: "0"
        val tinggi = intent.getIntExtra("EXTRA_TINGGI", 0)
        val berat = intent.getIntExtra("EXTRA_BERAT", 0)
        val gender = intent.getStringExtra("EXTRA_GENDER") ?: "Laki-laki"
        val bmiScore = intent.getFloatExtra("EXTRA_BMI_SCORE", 0f)

        // 2. Menampilkan Data Dasar ke Layar
        binding.tvHasilNama.text = nama
        binding.tvHasilGender.text = gender
        binding.tvHasilUmur.text = "$umur Tahun"
        binding.tvHasilTinggi.text = "$tinggi cm"
        binding.tvHasilBerat.text = "$berat kg"

        // Memformat Skor BMI menjadi 1 angka di belakang koma (contoh: 22.5)
        binding.tvHasilSkor.text = String.format("%.1f", bmiScore)

        // 3. Menentukan Kategori berdasarkan Tabel Gender
        val kategori: String
        val range: String
        val deskripsi: String
        val imageRes: Int

        if (gender == "Laki-laki") {
            // === LOGIKA TABEL BMI PRIA ===
            when {
                bmiScore < 17.0f -> {
                    kategori = "Kurus"
                    range = "Skor BMI: < 17"
                    deskripsi = "Tubuhmu membutuhkan lebih banyak nutrisi. Tingkatkan asupan makanan bergizi yang tinggi protein dan karbohidrat kompleks. Latihan angkat beban ringan juga sangat bagus untuk membangun massa otot, bukan sekadar menumpuk lemak!"
                    imageRes = R.drawable.character_boy_lebih_kurus
                }
                bmiScore < 23.0f -> { // Range 17 - 22.9
                    kategori = "Normal (Ideal)"
                    range = "Skor BMI: 17 - 23"
                    deskripsi = "Luar biasa! Berat badanmu berada dalam rentang yang sangat sehat dan ideal. Pertahankan pola makan bergizi seimbang dan rutinitas olahraga agar kebugaran dan bentuk tubuhmu tetap terjaga dengan baik."
                    imageRes = R.drawable.character_ideal
                }
                bmiScore <= 27.0f -> { // Range 23 - 27
                    kategori = "Gemuk"
                    range = "Skor BMI: 23 - 27"
                    deskripsi = "Berat badanmu sedikit di atas ideal. Ini waktu yang tepat untuk mulai melakukan defisit kalori ringan. Kurangi makanan manis atau gorengan, dan perbanyak latihan kardio serta angkat beban untuk membakar lemak berlebih."
                    imageRes = R.drawable.character_boy_gemuk
                }
                else -> { // > 27
                    kategori = "Obesitas"
                    range = "Skor BMI: > 27"
                    deskripsi = "Perhatian! Kamu berada di kategori obesitas yang berisiko bagi kesehatan. Sangat disarankan untuk memulai program defisit kalori yang disiplin, memperbanyak asupan serat alami, dan membangun kebiasaan olahraga yang konsisten."
                    imageRes = R.drawable.character_boy_obesitas
                }
            }
        } else {
            // === LOGIKA TABEL BMI WANITA ===
            when {
                bmiScore < 18.0f -> {
                    kategori = "Kurus"
                    range = "Skor BMI: < 18"
                    deskripsi = "Tubuhmu membutuhkan lebih banyak nutrisi. Tingkatkan asupan makanan bergizi yang tinggi protein dan karbohidrat kompleks. Latihan kekuatan juga sangat bagus untuk membangun otot agar tubuh lebih bugar!"
                    imageRes = R.drawable.character_girl_lebih_kurus
                }
                bmiScore < 25.0f -> { // Range 18 - 24.9
                    kategori = "Normal (Ideal)"
                    range = "Skor BMI: 18 - 25"
                    deskripsi = "Luar biasa! Berat badanmu berada dalam rentang yang sangat sehat dan ideal. Pertahankan pola makan bergizi seimbang dan rutinitas olahraga agar kebugaran dan bentuk tubuhmu tetap terjaga dengan baik."
                    imageRes = R.drawable.character_girl_ideal
                }
                bmiScore <= 27.0f -> { // Range 25 - 27
                    kategori = "Gemuk"
                    range = "Skor BMI: 25 - 27"
                    deskripsi = "Berat badanmu sedikit di atas ideal. Ini waktu yang tepat untuk mulai melakukan defisit kalori ringan. Kurangi makanan manis atau gorengan, dan perbanyak latihan kardio untuk membakar lemak berlebih."
                    imageRes = R.drawable.character_girl_gemuk
                }
                else -> { // > 27
                    kategori = "Obesitas"
                    range = "Skor BMI: > 27"
                    deskripsi = "Perhatian! Kamu berada di kategori obesitas yang berisiko bagi kesehatan. Sangat disarankan untuk memulai program defisit kalori yang disiplin, memperbanyak asupan serat alami, dan rutin berolahraga."
                    imageRes = R.drawable.character_girl_obesitas
                }
            }
        }

        // 4. Memasukkan Teks dan Gambar Hasil ke Layar
        binding.tvKategoriBmi.text = kategori
        binding.tvRangeBmi.text = range
        binding.tvDeskripsiBmi.text = deskripsi

        try {
            binding.ivSilhouette.setImageResource(imageRes)
        } catch (e: Exception) {
            // Fallback jika ada gambar yang belum terpasang di drawable
            binding.ivSilhouette.setImageResource(R.drawable.ic_character_boy)
        }
    }

    private fun setupTombolKembali() {
        binding.btnBack.setOnClickListener {
            finish() // Kembali ke kalkulator
        }

        binding.btnKembaliDashboard.setOnClickListener {
            val intent = Intent(this, com.example.sehatin.Main.MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}