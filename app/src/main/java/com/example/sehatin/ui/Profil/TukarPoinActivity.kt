package com.example.sehatin.ui.Profil

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.Utils.CharacterImageUtils
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

    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    private var currentPoinUser = 0
    private lateinit var userKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tukar_poin)

        // ==========================================
        // INISIALISASI SOUNDPOOL
        // ==========================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder().setMaxStreams(2).setAudioAttributes(audioAttributes).build()
        clickSoundId = soundPool?.load(this, R.raw.tombol_klik_sehatin, 1) ?: 0

        val tvUserPoin = findViewById<TextView>(R.id.tv_user_poin)
        val btnBack = findViewById<MaterialCardView>(R.id.btn_back)

        // Hubungkan Tombol Beli dari XML
        val btnBeliLatarElite = findViewById<MaterialButton>(R.id.btn_beli_bg_1)
        val btnBeliLatarSpecial = findViewById<MaterialButton>(R.id.btn_beli_bg_2)
        val btnBeliCharEpic = findViewById<MaterialButton>(R.id.btn_beli_char_1)
        val btnBeliCharRare = findViewById<MaterialButton>(R.id.btn_beli_char_2)

        btnBack.setOnClickListener {
            mainkanSuaraKlik()
            finish()
        }

        // ==========================================
        // 1. AMBIL IDENTITAS & GENDER USER
        // ==========================================
        val userPref = UserPreference(this)
        userKey = userPref.getName() ?: "guest_user"
        val userGender = userPref.getUserBody().gender

        // ==========================================
        // 2. SET GAMBAR ETALASE TOKO (DINAMIS SESUAI GENDER)
        // ==========================================
        val ivEtalaseBgElite = findViewById<ImageView>(R.id.iv_etalase_elite)
        val ivEtalaseBgSpecial = findViewById<ImageView>(R.id.iv_etalase_special)
        val ivEtalaseCharEpic = findViewById<ImageView>(R.id.iv_etalase_char_epic)
        val ivEtalaseCharRare = findViewById<ImageView>(R.id.iv_etalase_char_rare)

        ivEtalaseBgElite?.setImageResource(CharacterImageUtils.getBackgroundImageRes(userGender, 2))
        ivEtalaseBgSpecial?.setImageResource(CharacterImageUtils.getBackgroundImageRes(userGender, 3))

        ivEtalaseCharEpic?.setImageResource(CharacterImageUtils.getCharacterImageRes(userGender, "Normal (Ideal)", 2))
        ivEtalaseCharRare?.setImageResource(CharacterImageUtils.getCharacterImageRes(userGender, "Normal (Ideal)", 3))

        // ==========================================
        // 3. INISIALISASI VIEWMODEL & PREFERENCES
        // ==========================================
        val prefTantangan = TantanganPreferences.getInstance(applicationContext.dataStoreTantangan)
        viewModelTantangan = ViewModelProvider(this, TantanganViewModelFactory(TantanganRepository(prefTantangan)))[TantanganViewModel::class.java]

        val prefProfil = ProfilPreferences.getInstance(applicationContext.dataStoreProfil)
        viewModelProfil = ViewModelProvider(this, ProfilViewModelFactory(ProfilRepository(prefProfil)))[ProfilViewModel::class.java]

        val shopPrefs = getSharedPreferences("ShopPrefs_$userKey", Context.MODE_PRIVATE)
        val ownsSkinElite = shopPrefs.getBoolean("hasSkinElite", false)
        val ownsSkinSpecial = shopPrefs.getBoolean("hasSkinSpecial", false)

        // ==========================================
        // 4. PANTAU POIN & KEPEMILIKAN
        // ==========================================
        viewModelTantangan.getTotalPoin(userKey).observe(this) { currentPoin ->
            currentPoinUser = currentPoin
            tvUserPoin.text = "$currentPoinUser"
        }

        viewModelProfil.getProfilData().observe(this) { data ->
            if (data.hasBgGym) setTombolDimiliki(btnBeliLatarElite)
            if (data.hasBgTaman) setTombolDimiliki(btnBeliLatarSpecial)
        }

        if (ownsSkinElite) setTombolDimiliki(btnBeliCharEpic)
        if (ownsSkinSpecial) setTombolDimiliki(btnBeliCharRare)

        // ==========================================
        // 5. LOGIKA KLIK BELI (TUKAR POIN)
        // ==========================================

        // --- LATAR BELAKANG ---
        btnBeliLatarElite.setOnClickListener {
            mainkanSuaraKlik()
            tampilkanPopUpKonfirmasi("Latar Gym Retro", 120) {
                viewModelTantangan.tambahPoin(userKey, -120)
                viewModelProfil.buyBgGym()
                setTombolDimiliki(btnBeliLatarElite)
                tampilkanPopUpSukses("Latar Gym Retro")
            }
        }

        btnBeliLatarSpecial.setOnClickListener {
            mainkanSuaraKlik()
            tampilkanPopUpKonfirmasi("Latar Taman", 50) {
                viewModelTantangan.tambahPoin(userKey, -50)
                viewModelProfil.buyBgTaman()
                setTombolDimiliki(btnBeliLatarSpecial)
                tampilkanPopUpSukses("Latar Taman")
            }
        }

        // --- KARAKTER ---
        btnBeliCharEpic.setOnClickListener {
            mainkanSuaraKlik()
            tampilkanPopUpKonfirmasi("Setelan Elite", 150) {
                viewModelTantangan.tambahPoin(userKey, -150)
                shopPrefs.edit().putBoolean("hasSkinElite", true).apply()
                setTombolDimiliki(btnBeliCharEpic)
                tampilkanPopUpSukses("Setelan Elite")
            }
        }

        btnBeliCharRare.setOnClickListener {
            mainkanSuaraKlik()
            tampilkanPopUpKonfirmasi("Baju Olahraga", 80) {
                viewModelTantangan.tambahPoin(userKey, -80)
                shopPrefs.edit().putBoolean("hasSkinSpecial", true).apply()
                setTombolDimiliki(btnBeliCharRare)
                tampilkanPopUpSukses("Baju Olahraga")
            }
        }
    }

    // ========================================================
    // FUNGSI PENDUKUNG UI & AUDIO
    // ========================================================

    private fun setTombolDimiliki(btn: MaterialButton) {
        btn.text = "Dimiliki"
        btn.isEnabled = false
        btn.setBackgroundColor(Color.parseColor("#9E9E9E"))
        btn.setStrokeColorResource(android.R.color.transparent)
    }

    // ========================================================
    // POP-UP KUSTOM (XML)
    // ========================================================
    private fun tampilkanPopUpKonfirmasi(namaItem: String, harga: Int, onConfirm: () -> Unit) {
        if (currentPoinUser < harga) {
            Toast.makeText(this, "Maaf, Poin Anda tidak cukup \uD83D\uDE22", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_konfirmasi_tukar_poin, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Membuat background dialog menjadi transparan agar sudut melengkung CardView terlihat
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTanya = dialogView.findViewById<TextView>(R.id.tv_dialog_tanya)
        val tvSisa = dialogView.findViewById<TextView>(R.id.tv_dialog_sisa)
        val btnBatal = dialogView.findViewById<TextView>(R.id.btn_dialog_batal)
        val btnTukar = dialogView.findViewById<TextView>(R.id.btn_dialog_tukar)

        // Set teks secara dinamis sesuai barang yang diklik
        tvTanya.text = "Tukar $harga Poin dengan $namaItem?"
        tvSisa.text = "Sisa Poin Anda nanti: ${currentPoinUser - harga}"

        btnBatal.setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
        }

        btnTukar.setOnClickListener {
            mainkanSuaraKlik()
            onConfirm()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun tampilkanPopUpSukses(namaItem: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_sukses_tukar_poin, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvSelamat = dialogView.findViewById<TextView>(R.id.tv_dialog_selamat)
        val btnKeren = dialogView.findViewById<TextView>(R.id.btn_dialog_keren)

        // Set teks sukses secara dinamis
        tvSelamat.text = "Selamat! Kamu berhasil menukarkan poin dengan $namaItem."

        btnKeren.setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun mainkanSuaraKlik() {
        val sharedPrefs = getSharedPreferences("AudioSettings", Context.MODE_PRIVATE)
        val volumeSfxInt = sharedPrefs.getInt("VOLUME_SFX", 100)
        val volumeSfxFloat = volumeSfxInt / 100f
        soundPool?.play(clickSoundId, volumeSfxFloat, volumeSfxFloat, 0, 0, 1f)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool?.release()
        soundPool = null
    }
}