package com.example.sehatin.ui.Profil // Sesuaikan dengan nama package Anda jika berbeda

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sehatin.databinding.ActivityTukarPoinBinding
import java.text.NumberFormat
import java.util.Locale

class TukarPoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTukarPoinBinding

    // Simulasi Poin Pengguna saat ini
    private var poinUser = 1250

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi ViewBinding
        binding = ActivityTukarPoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Tampilkan jumlah poin awal di pojok kanan atas
        updatePoinUI()

        // 2. Logika Tombol Kembali (Back)
        binding.btnBack.setOnClickListener {
            finish() // Tutup halaman dan kembali ke Profile
        }

        // 3. Logika Beli Item 1 (Harga 500 Poin)
        binding.btnBeli1.setOnClickListener {
            beliItem(harga = 500, namaItem = "Latar Gym Retro")
        }

        // 4. Logika Beli Item 2 (Harga 300 Poin)
        binding.btnBeli2.setOnClickListener {
            beliItem(harga = 300, namaItem = "Latar Taman")
        }
    }

    // Fungsi Utama untuk memproses penukaran Poin
    private fun beliItem(harga: Int, namaItem: String) {
        if (poinUser >= harga) {
            // Jika poin cukup: Kurangi poin
            poinUser -= harga
            updatePoinUI()

            // Tampilkan pesan sukses
            Toast.makeText(this, "Berhasil menukar $namaItem!", Toast.LENGTH_SHORT).show()

            // TODO: Nanti di sini kita tambahkan kode untuk menyimpan item ke Inventaris

        } else {
            // Jika poin tidak cukup
            Toast.makeText(this, "Poin kamu tidak cukup untuk menukar item ini.", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk mengubah tampilan angka poin (Memberi koma ribuan)
    private fun updatePoinUI() {
        val formatPoin = NumberFormat.getNumberInstance(Locale.US).format(poinUser)
        binding.tvUserPoin.text = "$formatPoin Poin"
    }
}