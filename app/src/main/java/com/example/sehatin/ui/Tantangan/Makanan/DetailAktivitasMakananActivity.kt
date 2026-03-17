package com.example.sehatin.ui.Tantangan.Makanan

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAktivitasMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. TANGKAP ARRAY DARI INTENT
        idMisi = intent.getIntExtra("ID_MISI", 0)
        binding.tvNamaTantanganMakanan.text = intent.getStringExtra("NAMA_MISI") ?: "Tantangan Nutrisi"

        // Simpan nilai maksimal yang bisa didapat
        rewardPoinMax = intent.getIntExtra("REWARD_POIN", 0)
        rewardExpMax = intent.getIntExtra("REWARD_EXP", 0)

        binding.tvPoinMakanan.text = "Max: $rewardPoinMax Poin | $rewardExpMax EXP"

        daftarPertanyaan = intent.getStringArrayListExtra("LIST_PERTANYAAN") ?: ArrayList()
        daftarJawabA = intent.getStringArrayListExtra("LIST_A") ?: ArrayList()
        daftarJawabB = intent.getStringArrayListExtra("LIST_B") ?: ArrayList()
        daftarJawabC = intent.getStringArrayListExtra("LIST_C") ?: ArrayList()
        daftarKunci = intent.getStringArrayListExtra("LIST_KUNCI") ?: ArrayList()

        // 2. KONTROL TOMBOL UI (Pemicu Transisi FSM)
        binding.btnBackMakanan.setOnClickListener { finish() }

        binding.btnPauseMakanan.setOnClickListener {
            if (currentState == TantanganState.SEDANG_BERJALAN) {
                ubahState(TantanganState.DI_JEDA)
            }
        }

        binding.btnJawabanA.setOnClickListener { cekJawaban("A", binding.btnJawabanA) }
        binding.btnJawabanB.setOnClickListener { cekJawaban("B", binding.btnJawabanB) }
        binding.btnJawabanC.setOnClickListener { cekJawaban("C", binding.btnJawabanC) }

        // Tombol ini HANYA muncul di akhir kuis untuk menyelesaikan
        binding.btnActionSelesaiMakanan.setOnClickListener {
            if (currentState == TantanganState.SELESAI) {
                // PERCABANGAN FSM: Cek Skor Akhir!
                if (jumlahBenar > 0) {
                    ubahState(TantanganState.HADIAH_DITERIMA)
                } else {
                    ubahState(TantanganState.GAGAL) // Gagal karena skor 0
                }
            }
        }

        // 3. MULAI FSM
        ubahState(TantanganState.BELUM_DIMULAI)
    }

    // ========================================================
    // IMPLEMENTASI TRANSISI FSM
    // ========================================================
    private fun ubahState(newState: TantanganState) {
        currentState = newState

        when (newState) {
            TantanganState.BELUM_DIMULAI -> {
                soalSekarangIndex = 0
                jumlahBenar = 0 // Reset skor ke 0
                sisaWaktuMillis = daftarPertanyaan.size * 15000L // 15 Detik per soal
                ubahState(TantanganState.SEDANG_BERJALAN)
            }
            TantanganState.SEDANG_BERJALAN -> {
                tampilkanSoal(soalSekarangIndex)
                mulaiTimer()
            }
            TantanganState.DI_JEDA -> {
                jedaTimer()
                tampilkanDialogJeda()
            }
            TantanganState.SELESAI -> {
                jedaTimer()
                binding.btnActionSelesaiMakanan.text = "Selesaikan Tantangan"
                binding.btnActionSelesaiMakanan.visibility = View.VISIBLE
            }
            TantanganState.GAGAL -> {
                jedaTimer()
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
                ubahState(TantanganState.GAGAL) // Gagal karena waktu habis
            }
        }.start()
    }

    private fun jedaTimer() {
        timer?.cancel()
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
        // Blokir jika user sudah menekan salah satu jawaban
        if (sudahMenjawab || currentState != TantanganState.SEDANG_BERJALAN) return

        sudahMenjawab = true // Kunci tombol
        val kunciJawaban = daftarKunci[soalSekarangIndex]

        // WARNAI JAWABAN USER
        if (pilihan == kunciJawaban) {
            jumlahBenar++
            btnPilihan.setStrokeColor(Color.parseColor("#4CAF50"))
            btnPilihan.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
        } else {
            btnPilihan.setStrokeColor(Color.parseColor("#F44336"))
            btnPilihan.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
        }

        // ==========================================
        // AUTO NEXT MENGGUNAKAN HANDLER (Jeda 1 Detik)
        // ==========================================
        Handler(Looper.getMainLooper()).postDelayed({
            // Pastikan Activity belum ditutup dan kuis masih dalam state Berjalan
            if (!isDestroyed && currentState == TantanganState.SEDANG_BERJALAN) {
                if (soalSekarangIndex == daftarPertanyaan.size - 1) {
                    ubahState(TantanganState.SELESAI) // Soal habis
                } else {
                    soalSekarangIndex++
                    tampilkanSoal(soalSekarangIndex) // Pindah otomatis
                }
            }
        }, 1000)
    }

    // ========================================================
    // LOGIKA DIALOG (TRANSISI FSM)
    // ========================================================
    private fun tampilkanDialogJeda() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_jeda, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<MaterialButton>(R.id.btn_lanjutkan).setOnClickListener {
            dialog.dismiss()
            ubahState(TantanganState.SEDANG_BERJALAN)
        }
        dialogView.findViewById<MaterialButton>(R.id.btn_mulai_ulang).setOnClickListener {
            dialog.dismiss()
            ubahState(TantanganState.BELUM_DIMULAI)
        }
        dialogView.findViewById<MaterialButton>(R.id.btn_keluar).setOnClickListener {
            dialog.dismiss()
            finish()
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

        // CEK ALASAN GAGAL: Waktu habis ATAU Skor 0?
        if (jumlahBenar == 0 && soalSekarangIndex == daftarPertanyaan.size - 1) {
            btnUlang.text = "Skor 0! Coba Lagi"
        } else {
            btnUlang.text = "Waktu Habis! Coba Lagi"
        }

        btnUlang.setOnClickListener {
            dialog.dismiss()
            ubahState(TantanganState.BELUM_DIMULAI)
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_keluar).setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    private fun lanjutKeCongratulations() {
        // PERHITUNGAN SKOR DINAMIS
        val persentaseBenar = jumlahBenar.toFloat() / daftarPertanyaan.size.toFloat()
        val finalPoin = (rewardPoinMax * persentaseBenar).toInt()
        val finalExp = (rewardExpMax * persentaseBenar).toInt()

        // Simpan progress (Misi dianggap tuntas) ke MakananPreferences
        if (idMisi != 0) {
            val userPref = UserPreference(this)
            val userKey = userPref.getName() ?: "guest_user"

            val prefMakanan = MakananPreferences.getInstance(applicationContext.dataStoreMakanan)
            val factory = MakananViewModelFactory(MakananRepository(prefMakanan))
            val viewModelMakanan = ViewModelProvider(this, factory)[MakananViewModel::class.java]

            viewModelMakanan.simpanMisiSelesai(userKey, idMisi)
        }

        // Kirim hasil akhir ke halaman Peti Hadiah
        val intentLanjut = Intent(this, CongratulationsMakananActivity::class.java).apply {
            putExtra("HASIL_POIN", finalPoin)
            putExtra("HASIL_EXP", finalExp)
        }
        startActivity(intentLanjut)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        jedaTimer() // Amankan dari memory leak
    }
}