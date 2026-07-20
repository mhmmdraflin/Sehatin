package com.example.sehatin.ui.Tantangan.Makanan

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.databinding.ActivityDetailTantanganMakananBinding
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailTantanganMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTantanganMakananBinding
    private lateinit var viewModel: MakananViewModel
    private var completedMissions = listOf<Int>()
    private lateinit var userKey: String

    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    // Variabel Status Gembok
    private var isPemulaSelesai = false
    private var isMenengahSelesai = false
    private var isLanjutanSelesai = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTantanganMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // =======================================================
        // INISIALISASI AUDIO
        // =======================================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder().setMaxStreams(2).setAudioAttributes(audioAttributes).build()
        clickSoundId = soundPool?.load(this, R.raw.tombol_klik_sehatin, 1) ?: 0

        binding.btnBackMenuMakanan.setOnClickListener {
            mainkanSuaraKlik()
            finish()
        }

        val userPref = UserPreference(this)
        userKey = userPref.getName() ?: "guest_user"

        val prefMakanan = MakananPreferences.getInstance(applicationContext.dataStoreMakanan)
        val factory = MakananViewModelFactory(MakananRepository(prefMakanan))
        viewModel = ViewModelProvider(this, factory)[MakananViewModel::class.java]

        // =======================================================
        // OBSERVER PROGRESS & SISTEM GEMBOK
        // =======================================================
        viewModel.getCompletedMissions(userKey).observe(this) { missions ->
            completedMissions = missions

            // Cek Syarat Kelulusan
            isPemulaSelesai = completedMissions.contains(1) // Pemula selesai jika Misi 1 tuntas
            isMenengahSelesai = completedMissions.contains(2) // Menengah selesai jika Misi 2 tuntas
            isLanjutanSelesai = completedMissions.contains(3) && completedMissions.contains(4)

            // Perbarui Visual Tombol (Redup/Abu-abu & Gembok)
            updateVisualGembok()
        }

        // Siapkan logika klik kuis
        setupTombolKuis()
    }

    // ========================================================
    // FUNGSI UNTUK MERUBAH VISUAL (ABU-ABU & IKON GEMBOK)
    // ========================================================
    private fun updateVisualGembok() {
        // Misi 1 (Pemula) Selalu Terbuka
        aturStatusVisual(binding.btnMisi1, binding.ivStatusMisi1, false, completedMissions.contains(1))

        // Misi 2 (Menengah) Tergantung Pemula
        aturStatusVisual(binding.btnMisi2, binding.ivStatusMisi2, !isPemulaSelesai, completedMissions.contains(2))

        // Misi 3 & 4 (Lanjutan) Tergantung Menengah
        aturStatusVisual(binding.btnMisi3, binding.ivStatusMisi3, !isMenengahSelesai, completedMissions.contains(3))
        aturStatusVisual(binding.btnMisi4, binding.ivStatusMisi4, !isMenengahSelesai, completedMissions.contains(4))

        // Misi 5 & 6 (Master) Tergantung Lanjutan
        aturStatusVisual(binding.btnMisi5, binding.ivStatusMisi5, !isLanjutanSelesai, completedMissions.contains(5))
        aturStatusVisual(binding.btnMisi6, binding.ivStatusMisi6, !isLanjutanSelesai, completedMissions.contains(6))
    }

    private fun aturStatusVisual(card: MaterialCardView, icon: ImageView, isLocked: Boolean, isSelesai: Boolean) {
        if (isLocked) {
            // Jika Terkunci: Warna abu-abu redup dan ikon gembok
            card.alpha = 0.4f
            icon.setImageResource(android.R.drawable.ic_lock_lock)
        } else {
            // Jika Terbuka: Warna terang dan ikon play
            card.alpha = 1.0f
            icon.setImageResource(android.R.drawable.ic_media_play)

            // [Opsional] Jika misi sudah pernah diselesaikan, bisa diredupkan sedikit agar user tahu
            if (isSelesai) {
                card.alpha = 0.7f
            }
        }
    }

    // ========================================================
    // LOGIKA KLIK & PENGECEKAN SYARAT MASUK
    // ========================================================
    private fun setupTombolKuis() {
        // --- TINGKAT PEMULA ---
        binding.btnMisi1.setOnClickListener {
            mainkanSuaraKlik()
            if (!cekStatusDanKesempatan(1)) return@setOnClickListener
            jalankanKuis(1, "Pemula: Sarapan Sehat", 15, 10,
                arrayListOf("Mengapa sarapan pagi sangat penting?", "Karbohidrat kompleks yang baik untuk sarapan adalah...", "Apa fungsi protein saat sarapan?", "Minuman terbaik saat bangun tidur?", "Melewatkan sarapan dapat menyebabkan..."),
                arrayListOf("A. Agar mengantuk", "A. Roti tawar putih", "A. Membangun otot & tahan lapar", "A. Kopi manis", "A. Lebih mudah fokus"),
                arrayListOf("B. Memberi energi harian", "B. Oatmeal", "B. Menambah lemak", "B. Air putih", "B. Metabolisme melambat"),
                arrayListOf("C. Menurunkan berat badan", "C. Mie instan", "C. Membuat keropos tulang", "C. Jus kemasan", "C. Tidur nyenyak"),
                arrayListOf("B", "B", "A", "B", "B")
            )
        }

        // --- TINGKAT MENENGAH ---
        binding.btnMisi2.setOnClickListener {
            mainkanSuaraKlik()
            if (!isPemulaSelesai) {
                Toast.makeText(this, "Terkunci! Selesaikan Tingkat Pemula terlebih dahulu.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!cekStatusDanKesempatan(2)) return@setOnClickListener
            jalankanKuis(2, "Menengah: Keajaiban Air Putih", 15, 10,
                arrayListOf("Berapa liter anjuran minum air putih sehari?", "Tanda utama tubuh kekurangan cairan adalah...", "Kapan waktu terbaik minum air putih?", "Manfaat utama air putih bagi ginjal?", "Minuman pengganti cairan tubuh alami terbaik adalah..."),
                arrayListOf("A. 1 Liter", "A. Urin berwarna pekat", "A. Saat makan saja", "A. Membantu menyaring racun", "A. Air kelapa murni"),
                arrayListOf("B. 2 Liter", "B. Sering berkeringat", "B. Pagi setelah bangun tidur", "B. Membuat ginjal istirahat", "B. Minuman bersoda"),
                arrayListOf("C. 5 Liter", "C. Rambut rontok", "C. Malam sebelum tidur", "C. Menghancurkan lemak", "C. Minuman berenergi buatan"),
                arrayListOf("B", "A", "B", "A", "A")
            )
        }

        // --- TINGKAT LANJUTAN ---
        binding.btnMisi3.setOnClickListener {
            mainkanSuaraKlik()
            if (!isMenengahSelesai) {
                Toast.makeText(this, "Terkunci! Selesaikan Tingkat Menengah terlebih dahulu.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!cekStatusDanKesempatan(3)) return@setOnClickListener
            jalankanKuis(3, "Lanjutan: Karbohidrat & Lemak", 25, 20,
                arrayListOf("Karbohidrat yang paling lambat dicerna tubuh?", "Lemak baik (HDL) banyak ditemukan secara alami pada?", "Kelebihan karbohidrat yang tidak dibakar akan disimpan tubuh sebagai?", "Apa bahaya utama dari lemak trans (Trans Fat)?", "Makanan tinggi serat (fiber) sangat membantu dalam hal?"),
                arrayListOf("A. Nasi putih", "A. Gorengan", "A. Massa otot", "A. Meningkatkan risiko penyakit jantung", "A. Menaikkan gula darah dengan cepat"),
                arrayListOf("B. Gula pasir cair", "B. Buah alpukat", "B. Cadangan lemak", "B. Membuat tubuh kebal penyakit", "B. Melancarkan sistem pencernaan"),
                arrayListOf("C. Nasi merah / Gandum", "C. Mentega margarin", "C. Kepadatan tulang", "C. Menurunkan kolesterol", "C. Menyebabkan rasa kantuk berlebih"),
                arrayListOf("C", "B", "B", "A", "B")
            )
        }

        binding.btnMisi4.setOnClickListener {
            mainkanSuaraKlik()
            if (!isMenengahSelesai) {
                Toast.makeText(this, "Terkunci! Selesaikan Tingkat Menengah terlebih dahulu.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!cekStatusDanKesempatan(4)) return@setOnClickListener
            jalankanKuis(4, "Lanjutan: Protein & Otot", 25, 20,
                arrayListOf("Sumber protein nabati terbaik di bawah ini adalah?", "Kapan waktu yang sangat dianjurkan untuk mengonsumsi protein bagi olahragawan?", "Fungsi utama protein bagi tubuh manusia adalah?", "Bagian telur manakah yang paling tinggi kandungan protein murninya?", "Berapa rata-rata kandungan protein dalam 1 butir telur ukuran sedang?"),
                arrayListOf("A. Daging sapi lada hitam", "A. Sebelum tidur malam saja", "A. Memberikan energi instan", "A. Putih telur", "A. 2 gram"),
                arrayListOf("B. Tempe dan Tahu", "B. Setelah sesi latihan fisik", "B. Memperbaiki sel dan serat otot", "B. Kuning telur", "B. 6 gram"),
                arrayListOf("C. Dada ayam rebus", "C. Saat bangun tidur saja", "C. Melarutkan vitamin dalam darah", "C. Cangkang telur", "C. 15 gram"),
                arrayListOf("B", "B", "B", "A", "B")
            )
        }

        // --- TINGKAT MASTER ---
        binding.btnMisi5.setOnClickListener {
            mainkanSuaraKlik()
            if (!isLanjutanSelesai) {
                Toast.makeText(this, "Terkunci! Selesaikan Tingkat Lanjutan terlebih dahulu.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!cekStatusDanKesempatan(5)) return@setOnClickListener
            jalankanKuis(5, "Master: Metabolisme & Kalori", 40, 30,
                arrayListOf("Apa yang dimaksud dengan BMR?", "Defisit kalori adalah kondisi dimana...", "Zat gizi makro manakah yang membakar kalori paling banyak saat dicerna?", "Faktor apa yang TIDAK mempengaruhi tingkat BMR seseorang?", "1 gram lemak menyumbang kalori sebesar?"),
                arrayListOf("A. Kalori yang terbakar hanya saat olahraga", "A. Kalori masuk < Kalori keluar", "A. Karbohidrat", "A. Usia dan Jenis Kelamin", "A. 4 Kalori"),
                arrayListOf("B. Kalori minimal tubuh untuk hidup", "B. Kalori masuk > Kalori keluar", "B. Lemak", "B. Jumlah massa otot", "B. 7 Kalori"),
                arrayListOf("C. Total kalori makanan hari ini", "C. Kalori masuk = Kalori keluar", "C. Protein", "C. Jam tidur tadi malam", "C. 9 Kalori"),
                arrayListOf("B", "A", "C", "C", "C")
            )
        }

        binding.btnMisi6.setOnClickListener {
            mainkanSuaraKlik()
            if (!isLanjutanSelesai) {
                Toast.makeText(this, "Terkunci! Selesaikan Tingkat Lanjutan terlebih dahulu.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!cekStatusDanKesempatan(6)) return@setOnClickListener
            jalankanKuis(6, "Master: Mitos vs Fakta Gizi", 50, 40,
                arrayListOf("Makan di malam hari pasti membuat gemuk. Ini adalah?", "Apakah diet Keto cocok untuk semua orang?", "Apakah air perasan jeruk nipis hangat bisa melarutkan lemak?", "Gula aren lebih sehat dari gula pasir, sehingga boleh dikonsumsi tanpa batas?", "Mengkonsumsi vitamin C dosis tinggi (1000mg) akan kebal dari flu?"),
                arrayListOf("A. Fakta mutlak", "A. Benar, diet terbaik", "A. Ya, asam melarutkan lemak", "A. Mitos, jumlah kalorinya mirip", "A. Mitos, sisa dibuang tubuh"),
                arrayListOf("B. Mitos, bergantung total kalori", "B. Salah, bergantung gaya hidup", "B. Tidak, itu hanya mitos", "B. Fakta, 100% alami", "B. Fakta, perisai absolut"),
                arrayListOf("C. Fakta, setelah jam 7 malam", "C. Benar, terbukti medis", "C. Ya, jika diminum panas", "C. Fakta, tak naikkan gula darah", "C. Fakta, asal dibarengi olahraga"),
                arrayListOf("B", "B", "B", "A", "A")
            )
        }
    }

    // ========================================================
    // FUNGSI PENDUKUNG (AUDIO & LIMIT)
    // ========================================================
    private fun mainkanSuaraKlik() {
        val sharedPrefs = getSharedPreferences("AudioSettings", Context.MODE_PRIVATE)
        val volumeSfxInt = sharedPrefs.getInt("VOLUME_SFX", 100)
        soundPool?.play(clickSoundId, volumeSfxInt / 100f, volumeSfxInt / 100f, 0, 0, 1f)
    }

    private fun cekStatusDanKesempatan(idMisi: Int): Boolean {
        val prefs = getSharedPreferences("KuisAttemptPrefs_$userKey", Context.MODE_PRIVATE)
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val attemptKey = "attempt_misi_${idMisi}_$todayDate"
        val percobaanHariIni = prefs.getInt(attemptKey, 0)

        if (percobaanHariIni >= 3) {
            Toast.makeText(this, "Kesempatan habis! Kerjakan besok lagi.", Toast.LENGTH_LONG).show()
            return false
        }
        prefs.edit().putInt(attemptKey, percobaanHariIni + 1).apply()
        return true
    }

    private fun jalankanKuis(id: Int, nama: String, poin: Int, exp: Int, soal: ArrayList<String>, a: ArrayList<String>, b: ArrayList<String>, c: ArrayList<String>, kunci: ArrayList<String>) {
        val intent = Intent(this, DetailAktivitasMakananActivity::class.java).apply {
            putExtra("ID_MISI", id)
            putExtra("NAMA_MISI", nama)
            putExtra("REWARD_POIN", poin)
            putExtra("REWARD_EXP", exp)
            putStringArrayListExtra("LIST_PERTANYAAN", soal)
            putStringArrayListExtra("LIST_A", a)
            putStringArrayListExtra("LIST_B", b)
            putStringArrayListExtra("LIST_C", c)
            putStringArrayListExtra("LIST_KUNCI", kunci)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool?.release()
    }
}