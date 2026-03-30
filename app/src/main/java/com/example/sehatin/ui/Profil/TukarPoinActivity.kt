package com.example.sehatin.ui.Profil

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView // TAMBAHAN IMPORT
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.Utils.CharacterImageUtils // IMPORT OTAK TERPADU
import com.example.sehatin.ui.Tantangan.TantanganPreferences
import com.example.sehatin.ui.Tantangan.TantanganRepository
import com.example.sehatin.ui.Tantangan.TantanganViewModel
import com.example.sehatin.ui.Tantangan.TantanganViewModelFactory
import com.example.sehatin.ui.Tantangan.dataStoreTantangan
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class TukarPoinActivity : AppCompatActivity() {

    private lateinit var viewModelTantangan: TantanganViewModel
    private lateinit var viewModelProfil: ProfilViewModel

    private var currentPoinUser = 0
    private lateinit var userKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tukar_poin)

        val tvUserPoin = findViewById<TextView>(R.id.tv_user_poin)
        val btnBack = findViewById<MaterialCardView>(R.id.btn_back)

        // Penamaan variabel diubah menjadi Elite dan Special
        val btnBeliLatarElite = findViewById<MaterialButton>(R.id.btn_beli_1)
        val btnBeliLatarSpecial = findViewById<MaterialButton>(R.id.btn_beli_2)

        btnBack.setOnClickListener { finish() }

        // 1. AMBIL IDENTITAS & GENDER USER
        val userPref = UserPreference(this)
        userKey = userPref.getName() ?: "guest_user"
        val userGender = userPref.getUserBody().gender

        // ==========================================
        // SET GAMBAR ETALASE TOKO SESUAI GENDER (ELITE & SPECIAL)
        // ==========================================
        // Langsung memanggil ID dari ImageView yang sudah Anda tambahkan di XML
        val ivEtalaseElite = findViewById<ImageView>(R.id.iv_etalase_elite)
        val ivEtalaseSpecial = findViewById<ImageView>(R.id.iv_etalase_special)

        // Memanggil Otak Terpadu (Background ID 2 = Elite, ID 3 = Special)
        ivEtalaseElite?.setImageResource(CharacterImageUtils.getBackgroundImageRes(userGender, 2))
        ivEtalaseSpecial?.setImageResource(CharacterImageUtils.getBackgroundImageRes(userGender, 3))
        // ==========================================

        // 2. INISIALISASI BANK POIN (Tantangan) & INVENTARIS (Profil)
        val prefTantangan = TantanganPreferences.getInstance(applicationContext.dataStoreTantangan)
        viewModelTantangan = ViewModelProvider(this, TantanganViewModelFactory(TantanganRepository(prefTantangan)))[TantanganViewModel::class.java]

        val prefProfil = ProfilPreferences.getInstance(applicationContext.dataStoreProfil)
        viewModelProfil = ViewModelProvider(this, ProfilViewModelFactory(ProfilRepository(prefProfil)))[ProfilViewModel::class.java]

        // 3. PANTAU POIN & STATUS KEPEMILIKAN ITEM SECARA REAL-TIME
        viewModelTantangan.getTotalPoin(userKey).observe(this) { currentPoin ->
            currentPoinUser = currentPoin
            tvUserPoin.text = "$currentPoinUser Poin"
        }

        viewModelProfil.getProfilData().observe(this) { data ->
            // Menggunakan properti database yang sama, namun konteksnya kini Elite & Special
            if (data.hasBgGym) {
                setTombolDimiliki(btnBeliLatarElite)
            }
            if (data.hasBgTaman) {
                setTombolDimiliki(btnBeliLatarSpecial)
            }
        }

        // 4. LOGIKA KLIK TOMBOL BELI DENGAN POP-UP KONFIRMASI
        btnBeliLatarElite.setOnClickListener {
            tampilkanPopUpKonfirmasi("Latar Elite", 500) {
                viewModelTantangan.tambahPoin(userKey, -500)
                viewModelProfil.buyBgGym() // Backend tetap pakai fungsi aslinya agar aman
                Toast.makeText(this, "Berhasil menukar Latar Elite!", Toast.LENGTH_SHORT).show()
            }
        }

        btnBeliLatarSpecial.setOnClickListener {
            tampilkanPopUpKonfirmasi("Latar Special", 300) {
                viewModelTantangan.tambahPoin(userKey, -300)
                viewModelProfil.buyBgTaman() // Backend tetap pakai fungsi aslinya agar aman
                Toast.makeText(this, "Berhasil menukar Latar Special!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk mengubah tampilan tombol jika item sudah dibeli
    private fun setTombolDimiliki(btn: MaterialButton) {
        btn.text = "Dimiliki"
        btn.isEnabled = false // Tombol dimatikan
        btn.setBackgroundColor(Color.parseColor("#9E9E9E")) // Warna abu-abu
        btn.setStrokeColorResource(android.R.color.transparent)
    }

    // ==========================================
    // FUNGSI POP-UP KONFIRMASI PEMBELIAN
    // ==========================================
    private fun tampilkanPopUpKonfirmasi(namaItem: String, harga: Int, onConfirm: () -> Unit) {
        // Cek dulu poinnya, kalau kurang jangan munculkan pop-up konfirmasi
        if (currentPoinUser < harga) {
            Toast.makeText(this, "Maaf, Poin Anda tidak cukup \uD83D\uDE22", Toast.LENGTH_SHORT).show()
            return
        }

        // Buat Dialog Pop-Up
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Konfirmasi Penukaran")
        dialogBuilder.setMessage("Tukar $harga Poin dengan $namaItem?\n\nSisa Poin Anda nanti: ${currentPoinUser - harga}")

        dialogBuilder.setPositiveButton("Tukar") { dialog, _ ->
            onConfirm() // Jalankan fungsi potong poin & simpan item
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        // Mewarnai tombol Pop-Up agar lebih rapi (Opsional)
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4CAF50")) // Hijau
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F44336")) // Merah
    }
}