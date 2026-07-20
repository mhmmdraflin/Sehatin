package com.example.sehatin.ui.SideFeature

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes // IMPORT AUDIO ATTRIBUTES
import android.media.SoundPool       // IMPORT SOUNDPOOL
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sehatin.R
import com.example.sehatin.databinding.ActivityHasilBodyMassIndexBinding
import com.google.android.material.button.MaterialButton

class HasilBodyMassIndexActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHasilBodyMassIndexBinding
    private lateinit var kategoriBMI: String
    private lateinit var standarPilihanData: String

    // VARIABEL SOUNDPOOL (Untuk Suara Zero-Delay)
    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHasilBodyMassIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // =======================================================
        // INISIALISASI SOUNDPOOL
        // =======================================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2) // Maksimal putar 2 suara bersamaan jika diklik cepat
            .setAudioAttributes(audioAttributes)
            .build()

        // Load suara ke memori sejak awal Activity dibuka
        clickSoundId = soundPool?.load(this, R.raw.tombol_klik_sehatin, 1) ?: 0
        // =======================================================

        // Menangkap pilihan standar dari halaman sebelumnya
        standarPilihanData = intent.getStringExtra("EXTRA_STANDAR") ?: "Asia"

        tampilkanDataHasil()
        setupTombolKembali()

        // Logika Klik untuk Memunculkan Pop-up Tabel BMI
        binding.btnLihatTabelBmi.setOnClickListener {
            mainkanSuaraKlik() // MEMAINKAN SUARA KLIK
            tampilkanDialogTabelBMI(standarPilihanData)
        }
    }

    // ========================================================
    // FUNGSI UNTUK MEMAINKAN SUARA KLIK (ZERO-DELAY)
    // ========================================================
    private fun mainkanSuaraKlik() {
        soundPool?.play(clickSoundId, 1f, 1f, 0, 0, 1f)
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

        // =============================================================
        // PEMISAHAN LOGIKA KLASIFIKASI BMI (ASIA vs WESTERN)
        // =============================================================

        if (standarPilihanData == "Asia") {
            // SET LOGO DAN TOMBOL UNTUK ASIA
            binding.ivLogoWho.setImageResource(R.drawable.logo_who_asia2)
            binding.btnLihatTabelBmi.text = "Lihat Tabel Klasifikasi BMI Asia"

            when {
                bmiScore < 18.5f -> {
                    kategoriBMI = "Kurus"
                    range = "Skor BMI: < 18,5 (Standar Asia)"
                    deskripsi = "Tubuhmu membutuhkan lebih banyak nutrisi. Tingkatkan asupan makanan bergizi yang tinggi protein dan karbohidrat kompleks."
                    imageRes = if (gender == "Laki-laki") R.drawable.character_boy_lebih_kurus else R.drawable.character_girl_lebih_kurus
                }
                bmiScore < 23.0f -> {
                    kategoriBMI = "Normal (Ideal)"
                    range = "Skor BMI: 18,5 - 22,9 (Standar Asia)"
                    deskripsi = "Luar biasa! Berat badanmu berada dalam rentang yang sangat sehat dan ideal. Kesehatan optimal dan risiko penyakit metabolik rendah."
                    imageRes = if (gender == "Laki-laki") R.drawable.character_ideal else R.drawable.character_girl_ideal
                }
                bmiScore <= 24.9f -> {
                    kategoriBMI = "Gemuk"
                    range = "Skor BMI: 23,0 - 24,9 (Standar Asia)"
                    deskripsi = "Berat badanmu sedikit di atas ideal. Ini waktu yang tepat untuk mulai melakukan defisit kalori ringan. Risiko masalah jantung awal mulai meningkat."
                    imageRes = if (gender == "Laki-laki") R.drawable.character_boy_gemuk else R.drawable.character_girl_gemuk
                }
                bmiScore <= 29.9f -> {
                    kategoriBMI = "Obesitas"
                    range = "Skor BMI: 25,0 - 29,9 (Standar Asia)"
                    deskripsi = "Perhatian! Kamu berada di kategori obesitas I. Sangat disarankan untuk memulai program defisit kalori yang disiplin untuk menghindari risiko diabetes."
                    imageRes = if (gender == "Laki-laki") R.drawable.character_boy_obesitas else R.drawable.character_girl_obesitas
                }
                else -> {
                    kategoriBMI = "Obesitas"
                    range = "Skor BMI: >= 30,0 (Standar Asia)"
                    deskripsi = "Sangat Berisiko! Kamu berada di kategori obesitas II. Risiko komplikasi metabolik sangat serius. Segera mulai gaya hidup sehat dan konsultasi gizi."
                    imageRes = if (gender == "Laki-laki") R.drawable.character_boy_obesitas else R.drawable.character_girl_obesitas
                }
            }
        } else {
            // SET LOGO DAN TOMBOL UNTUK WESTERN (INTERNASIONAL)
            binding.ivLogoWho.setImageResource(R.drawable.logo_who_internasional)
            binding.btnLihatTabelBmi.text = "Lihat Tabel Klasifikasi BMI Internasional"

            when {
                bmiScore < 18.5f -> {
                    kategoriBMI = "Kurus"
                    range = "Skor BMI: < 18,5 (Standar Internasional)"
                    deskripsi = "Tubuhmu membutuhkan lebih banyak nutrisi. Tingkatkan asupan makanan bergizi yang tinggi protein."
                    imageRes = if (gender == "Laki-laki") R.drawable.character_boy_lebih_kurus else R.drawable.character_girl_lebih_kurus
                }
                bmiScore < 25.0f -> {
                    kategoriBMI = "Normal (Ideal)"
                    range = "Skor BMI: 18,5 - 24,9 (Standar Internasional)"
                    deskripsi = "Luar biasa! Berat badanmu berada dalam rentang yang sangat sehat dan ideal menurut standar Internasional."
                    imageRes = if (gender == "Laki-laki") R.drawable.character_ideal else R.drawable.character_girl_ideal
                }
                bmiScore < 30.0f -> {
                    kategoriBMI = "Gemuk (Overweight)"
                    range = "Skor BMI: 25,0 - 29,9 (Standar Internasional)"
                    deskripsi = "Berat badanmu di atas ideal. Ini waktu yang tepat untuk mulai melakukan defisit kalori ringan untuk kembali ke kondisi optimal."
                    imageRes = if (gender == "Laki-laki") R.drawable.character_boy_gemuk else R.drawable.character_girl_gemuk
                }
                bmiScore < 35.0f -> {
                    kategoriBMI = "Obesitas Kelas I"
                    range = "Skor BMI: 30,0 - 34,9 (Standar Internasional)"
                    deskripsi = "Perhatian! Kamu berada di kategori obesitas. Sangat disarankan untuk memulai program defisit kalori yang disiplin."
                    imageRes = if (gender == "Laki-laki") R.drawable.character_boy_obesitas else R.drawable.character_girl_obesitas
                }
                else -> {
                    kategoriBMI = "Obesitas Ekstrem"
                    range = "Skor BMI: >= 35,0 (Standar Internasional)"
                    deskripsi = "Sangat Berisiko! Risiko komplikasi metabolik sangat serius. Segera mulai gaya hidup sehat dan konsultasi gizi."
                    imageRes = if (gender == "Laki-laki") R.drawable.character_boy_obesitas else R.drawable.character_girl_obesitas
                }
            }
        }

        binding.tvKategoriBmi.text = kategoriBMI
        binding.tvRangeBmi.text = range
        binding.tvDeskripsiBmi.text = deskripsi

        try {
            binding.ivSilhouette.setImageResource(imageRes)
        } catch (e: Exception) {
            binding.ivSilhouette.setImageResource(if (gender == "Laki-laki") R.drawable.ic_character_boy else R.drawable.ic_character_girl)
        }
    }

    // =============================================================
    // FUNGSI UNTUK MEMANGGIL POP-UP TABEL SESUAI STANDAR PILIHAN
    // =============================================================
    private fun tampilkanDialogTabelBMI(standar: String) {
        // Arahkan ke layout pop-up yang sesuai
        val layoutRes = if (standar == "Asia") {
            R.layout.dialog_tabel_bmi
        } else {
            R.layout.dialog_tabel_bmi_western // Akan memanggil layout XML baru
        }

        val dialogView = layoutInflater.inflate(layoutRes, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnTutup = dialogView.findViewById<MaterialButton>(R.id.btn_tutup_dialog)
        btnTutup?.setOnClickListener {
            mainkanSuaraKlik() // MEMAINKAN SUARA KLIK SAAT POP-UP DITUTUP
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupTombolKembali() {
        binding.btnBack.setOnClickListener {
            mainkanSuaraKlik() // MEMAINKAN SUARA KLIK
            finish()
        }

        binding.btnKembaliDashboard.text = "Kembali ke Dashboard"

        binding.btnKembaliDashboard.setOnClickListener {
            mainkanSuaraKlik() // MEMAINKAN SUARA KLIK
            val intent = Intent(this, com.example.sehatin.Main.MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Bersihkan memori SoundPool agar tidak membebani HP
        soundPool?.release()
        soundPool = null
    }
}