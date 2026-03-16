package com.example.sehatin.ui.Tantangan.Makanan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.databinding.ActivityDetailTantanganMakananBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailTantanganMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTantanganMakananBinding
    private lateinit var viewModel: MakananViewModel
    private var completedMissions = listOf<Int>()
    private lateinit var userKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTantanganMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackMenuMakanan.setOnClickListener { finish() }

        val userPref = UserPreference(this)
        userKey = userPref.getName() ?: "guest_user"

        val prefMakanan = MakananPreferences.getInstance(applicationContext.dataStoreMakanan)
        val factory = MakananViewModelFactory(MakananRepository(prefMakanan))
        viewModel = ViewModelProvider(this, factory)[MakananViewModel::class.java]

        // Cek dan redupkan misi yang sudah selesai (Tampilan Visual Saja)
        viewModel.getCompletedMissions(userKey).observe(this) { missions ->
            completedMissions = missions
            if (missions.contains(1)) binding.btnMisi1.alpha = 0.5f
            if (missions.contains(2)) binding.btnMisi2.alpha = 0.5f

            // if (missions.contains(3)) binding.btnMisi3.alpha = 0.5f
            // ... dst
        }

        // Jalankan Inisialisasi Misi
        setupMisi1()
        setupMisi2()
        // setupMisi3() ... dst
    }

    // ========================================================
    // LOGIKA PENGECEKAN STATUS & KESEMPATAN HARIAN
    // ========================================================
    private fun cekStatusDanKesempatan(idMisi: Int): Boolean {
        // 1. Cek Kesempatan (Maksimal 3 Kali Sehari) sebagai ATURAN UTAMA
        val prefs = getSharedPreferences("KuisAttemptPrefs_$userKey", Context.MODE_PRIVATE)
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Key format: "attempt_misi_1_2026-03-16"
        val attemptKey = "attempt_misi_${idMisi}_$todayDate"
        val percobaanHariIni = prefs.getInt(attemptKey, 0)

        // Jika sudah mencapai 3 kali main hari ini, langsung tolak!
        if (percobaanHariIni >= 3) {
            Toast.makeText(this, "Kesempatan habis! Kerjakan besok lagi.", Toast.LENGTH_LONG).show()
            return false // TIDAK BOLEH LANJUT
        }

        // Jika lolos (belum 3 kali), tambahkan hitungan percobaan (+1) ke brankas
        prefs.edit().putInt(attemptKey, percobaanHariIni + 1).apply()

        // 2. Berikan pesan informatif jika user memainkan ulang misi yang sudah tuntas
        if (completedMissions.contains(idMisi)) {
            val sisaPercobaan = 2 - percobaanHariIni // Karena jatahnya 3. (Jika baru main ke-1, sisa 2)
            Toast.makeText(this, "Misi diulang! Sisa kesempatan Anda hari ini: $sisaPercobaan", Toast.LENGTH_SHORT).show()
        }

        return true // SILAKAN LANJUT MAIN
    }

    // ========================================================
    // MISI 1-3: TINGKAT PEMULA (Masing-masing 5 Soal)
    // ========================================================
    private fun setupMisi1() {
        binding.btnMisi1.setOnClickListener {
            if (!cekStatusDanKesempatan(1)) return@setOnClickListener

            jalankanKuis(1, "Pemula: Sarapan Sehat", 15, 10,
                arrayListOf("Mengapa sarapan pagi sangat penting?", "Karbohidrat kompleks yang baik untuk sarapan adalah...", "Apa fungsi protein saat sarapan?", "Minuman terbaik saat bangun tidur?", "Melewatkan sarapan dapat menyebabkan..."),
                arrayListOf("A. Agar mengantuk", "A. Roti tawar putih", "A. Membangun otot & tahan lapar", "A. Kopi manis", "A. Lebih mudah fokus"),
                arrayListOf("B. Memberi energi harian", "B. Oatmeal", "B. Menambah lemak", "B. Air putih", "B. Metabolisme melambat"),
                arrayListOf("C. Menurunkan berat badan", "C. Mie instan", "C. Membuat keropos tulang", "C. Jus kemasan", "C. Tidur nyenyak"),
                arrayListOf("B", "B", "A", "B", "B")
            )
        }
    }

    private fun setupMisi2() {
        binding.btnMisi2.setOnClickListener {
            if (!cekStatusDanKesempatan(2)) return@setOnClickListener

            jalankanKuis(2, "Pemula: Keajaiban Air Putih", 15, 10,
                arrayListOf("Berapa liter anjuran minum air putih sehari?", "Tanda utama tubuh kekurangan cairan adalah...", "Kapan waktu terbaik minum air putih?", "Manfaat utama air putih bagi ginjal?", "Minuman pengganti cairan tubuh alami terbaik adalah..."),
                arrayListOf("A. 1 Liter", "A. Urin berwarna pekat", "A. Saat makan saja", "A. Membantu menyaring racun", "A. Air kelapa murni"),
                arrayListOf("B. 2 Liter", "B. Sering berkeringat", "B. Pagi setelah bangun tidur", "B. Membuat ginjal istirahat", "B. Minuman bersoda"),
                arrayListOf("C. 5 Liter", "C. Rambut rontok", "C. Malam sebelum tidur", "C. Menghancurkan lemak", "C. Minuman berenergi buatan"),
                arrayListOf("B", "A", "B", "A", "A")
            )
        }
    }

    // ========================================================
    // FUNGSI HELPER UNTUK MENGIRIM ARRAY KUIS
    // ========================================================
    private fun jalankanKuis(
        idMisi: Int, nama: String, poin: Int, exp: Int,
        soal: ArrayList<String>, opsiA: ArrayList<String>, opsiB: ArrayList<String>, opsiC: ArrayList<String>, kunci: ArrayList<String>
    ) {
        val intent = Intent(this, DetailAktivitasMakananActivity::class.java).apply {
            putExtra("ID_MISI", idMisi)
            putExtra("NAMA_MISI", nama)
            putExtra("REWARD_POIN", poin)
            putExtra("REWARD_EXP", exp)
            putStringArrayListExtra("LIST_PERTANYAAN", soal)
            putStringArrayListExtra("LIST_A", opsiA)
            putStringArrayListExtra("LIST_B", opsiB)
            putStringArrayListExtra("LIST_C", opsiC)
            putStringArrayListExtra("LIST_KUNCI", kunci)
        }
        startActivity(intent)
    }
}