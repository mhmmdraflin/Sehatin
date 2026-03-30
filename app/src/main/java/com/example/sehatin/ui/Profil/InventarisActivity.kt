package com.example.sehatin.ui.Profil

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.Utils.CharacterImageUtils // IMPORT OTAK TERPADU
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class InventarisActivity : AppCompatActivity() {

    private lateinit var viewModelProfil: ProfilViewModel

    // ID Item yang sedang di-klik (Preview)
    private var tempBgId = 1
    private var tempCharId = 1

    // ID Item yang memang sedang dipakai (Equipped) di database
    private var savedBgId = 1
    private var savedCharId = 1

    // Status Kepemilikan dari Toko
    private var ownsBgGym = false

    // Komponen UI
    private lateinit var ivPreviewBg: ImageView
    private lateinit var ivPreviewChar: ImageView
    private lateinit var itemBg1: MaterialCardView
    private lateinit var itemBg2: MaterialCardView
    private lateinit var itemChar1: MaterialCardView
    private lateinit var itemChar2: MaterialCardView

    // Variabel Gender User (Dinamis dari Akun)
    private var userGender = "L"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventaris)

        // AMBIL GENDER DARI DATABASE USER
        val userPref = UserPreference(this)
        userGender = userPref.getUserBody().gender

        // 1. HUBUNGKAN ID XML UTAMA
        ivPreviewBg = findViewById(R.id.iv_preview_bg)
        ivPreviewChar = findViewById(R.id.iv_preview_char)
        val btnBack = findViewById<MaterialCardView>(R.id.btn_back)
        val btnSimpan = findViewById<MaterialCardView>(R.id.btn_simpan_preview)

        itemBg1 = findViewById(R.id.item_bg_1)
        itemBg2 = findViewById(R.id.item_bg_2)
        val btnKeTokoBg = findViewById<MaterialCardView>(R.id.btn_ke_toko_bg)

        itemChar1 = findViewById(R.id.item_char_1)
        itemChar2 = findViewById(R.id.item_char_2)
        val btnKeTokoChar = findViewById<MaterialCardView>(R.id.btn_ke_toko_char)

        btnBack.setOnClickListener { finish() }

        // ==========================================
        // SET IKON KOLEKSI BERDASARKAN GENDER SECARA DINAMIS
        // ==========================================
        val ivIconBg1 = itemBg1.getChildAt(0) as? ImageView
        val ivIconBg2 = itemBg2.getChildAt(0) as? ImageView
        val ivIconChar1 = itemChar1.getChildAt(0) as? ImageView
        val ivIconChar2 = itemChar2.getChildAt(0) as? ImageView

        ivIconBg1?.setImageResource(CharacterImageUtils.getBackgroundImageRes(userGender, 1))
        ivIconBg2?.setImageResource(CharacterImageUtils.getBackgroundImageRes(userGender, 2))

        ivIconChar1?.setImageResource(CharacterImageUtils.getCharacterImageRes(userGender, "Normal (Ideal)", 1))
        ivIconChar2?.setImageResource(CharacterImageUtils.getCharacterImageRes(userGender, "Normal (Ideal)", 2))

        // ==========================================
        // TOMBOL JALAN PINTAS KE TOKO (TUKAR POIN)
        // ==========================================
        btnKeTokoBg.setOnClickListener {
            val intent = Intent(this, TukarPoinActivity::class.java)
            startActivity(intent)
        }

        btnKeTokoChar.setOnClickListener {
            val intent = Intent(this, TukarPoinActivity::class.java)
            startActivity(intent)
        }

        // 2. INISIALISASI VIEWMODEL PROFIL
        val prefProfil = ProfilPreferences.getInstance(applicationContext.dataStoreProfil)
        val factory = ProfilViewModelFactory(ProfilRepository(prefProfil))
        viewModelProfil = ViewModelProvider(this, factory)[ProfilViewModel::class.java]

        // 3. PANTAU DATA KEPEMILIKAN & ITEM TERPAKAI
        viewModelProfil.getProfilData().observe(this) { data ->
            savedBgId = data.backgroundId
            savedCharId = data.characterId
            ownsBgGym = data.hasBgGym

            // Set Preview awal sesuai yang sedang dipakai
            updatePreviewBg(savedBgId)
            updatePreviewChar(savedCharId)

            // Efek Visual Barang Belum Dibeli (Digelapkan/Transparan)
            if (!ownsBgGym) {
                itemBg2.alpha = 0.4f
            } else {
                itemBg2.alpha = 1.0f
            }
        }

        // 4. LOGIKA KLIK ITEM UNTUK PREVIEW
        itemBg1.setOnClickListener { updatePreviewBg(1) }
        itemBg2.setOnClickListener {
            if (ownsBgGym) {
                updatePreviewBg(2)
            } else {
                Toast.makeText(this, "Latar ini belum dibeli. Silakan ke Toko!", Toast.LENGTH_SHORT).show()
            }
        }

        itemChar1.setOnClickListener { updatePreviewChar(1) }
        itemChar2.setOnClickListener { updatePreviewChar(2) }

        // 5. LOGIKA KLIK TOMBOL SIMPAN PREVIEW
        btnSimpan.setOnClickListener {
            // Cek apakah user sebenarnya mengubah sesuatu
            if (tempBgId == savedBgId && tempCharId == savedCharId) {
                Toast.makeText(this, "Anda sudah menggunakan kombinasi ini", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mulai Alur 3 Tahap
            tampilkanKonfirmasiSimpan()
        }
    }

    // ==========================================
    // FUNGSI UPDATE PREVIEW VISUAL
    // ==========================================
    private fun updatePreviewBg(bgId: Int) {
        tempBgId = bgId
        // Reset warna bingkai pilihan ke abu-abu
        itemBg1.strokeColor = Color.parseColor("#E0E0E0")
        itemBg2.strokeColor = Color.parseColor("#E0E0E0")

        // Memanggil Utils Terpadu agar sesuai dengan Gender
        ivPreviewBg.setImageResource(CharacterImageUtils.getBackgroundImageRes(userGender, bgId))

        if (bgId == 1) {
            itemBg1.strokeColor = Color.parseColor("#33A1E0") // Warna biru aktif
        } else if (bgId == 2) {
            itemBg2.strokeColor = Color.parseColor("#33A1E0")
        }
    }

    private fun updatePreviewChar(charId: Int) {
        tempCharId = charId
        // Reset warna bingkai pilihan ke abu-abu
        itemChar1.strokeColor = Color.parseColor("#E0E0E0")
        itemChar2.strokeColor = Color.parseColor("#E0E0E0")

        // Memanggil Utils Terpadu agar sesuai dengan Gender
        ivPreviewChar.setImageResource(CharacterImageUtils.getCharacterImageRes(userGender, "Normal (Ideal)", charId))

        if (charId == 1) {
            itemChar1.strokeColor = Color.parseColor("#33A1E0")
        } else if (charId == 2) {
            itemChar2.strokeColor = Color.parseColor("#33A1E0")
        }
    }

    // ==========================================
    // FUNGSI 3 TAHAP: KONFIRMASI -> LOADING -> SUKSES
    // ==========================================

    // Tahap 1: Konfirmasi (Pop-up Custom)
    private fun tampilkanKonfirmasiSimpan() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_konfirmasi_inventaris, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // WAJIB: Background transparan agar lengkungan (radius) terlihat
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnBatal = dialogView.findViewById<MaterialButton>(R.id.btn_dialog_batal)
        val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btn_dialog_simpan)

        btnBatal.setOnClickListener {
            dialog.dismiss()
        }

        btnSimpan.setOnClickListener {
            dialog.dismiss()
            tampilkanLoadingOverlay() // Lanjut ke Tahap 2
        }

        dialog.show()
    }

    // Tahap 2: Loading Animasi (Pop-up Custom Gamified)
    private fun tampilkanLoadingOverlay() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_loading_gamified, null)
        val loadingDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Tidak bisa ditutup dengan asal klik luar
            .create()

        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.show()

        // Simulasi Loading selama 1.5 detik
        Handler(Looper.getMainLooper()).postDelayed({
            loadingDialog.dismiss()

            // Simpan data permanen ke ViewModel (Database Lokal)
            viewModelProfil.saveEquippedBg(tempBgId)
            viewModelProfil.saveEquippedChar(tempCharId)

            tampilkanSukses() // Lanjut ke Tahap 3
        }, 1500)
    }

    // Tahap 3: Pemberitahuan Sukses (Pop-up Custom Gamified)
    private fun tampilkanSukses() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_sukses_gamified, null)
        val suksesDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        suksesDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnKeren = dialogView.findViewById<MaterialButton>(R.id.btn_dialog_sukses)

        btnKeren.setOnClickListener {
            suksesDialog.dismiss()

            // Opsional: Tutup halaman Inventaris agar otomatis kembali melihat Dashboard/Profil yang sudah berubah
            finish()
        }

        suksesDialog.show()
    }
}