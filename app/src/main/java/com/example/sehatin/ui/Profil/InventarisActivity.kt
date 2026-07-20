package com.example.sehatin.ui.Profil

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.Utils.CharacterImageUtils
import com.example.sehatin.databinding.ActivityInventarisBinding
import com.google.android.material.button.MaterialButton

class InventarisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventarisBinding
    private lateinit var viewModelProfil: ProfilViewModel

    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    // ID Item yang sedang di-klik (Preview)
    private var tempBgId = 1
    private var tempCharId = 1

    // ID Item yang memang sedang dipakai (Equipped) di database
    private var savedBgId = 1
    private var savedCharId = 1

    // Status Kepemilikan Barang
    private var ownsBgGym = false
    private var ownsBgTaman = false
    private var ownsSkinElite = false
    private var ownsSkinSpecial = false

    private var userGender = "L"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventarisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ==========================================
        // INISIALISASI SOUNDPOOL
        // ==========================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder().setMaxStreams(2).setAudioAttributes(audioAttributes).build()
        clickSoundId = soundPool?.load(this, R.raw.tombol_klik_sehatin, 1) ?: 0

        // ==========================================
        // AMBIL IDENTITAS AKUN & GENDER
        // ==========================================
        val userPref = UserPreference(this)
        userGender = userPref.getUserBody().gender
        val userKey = userPref.getName() ?: "guest_user"

        // Cek Kepemilikan Skin secara lokal
        val shopPrefs = getSharedPreferences("ShopPrefs_$userKey", Context.MODE_PRIVATE)
        ownsSkinElite = shopPrefs.getBoolean("hasSkinElite", false)
        ownsSkinSpecial = shopPrefs.getBoolean("hasSkinSpecial", false)

        binding.btnBack.setOnClickListener {
            mainkanSuaraKlik()
            finish()
        }

        // ==========================================
        // TOMBOL NAVIGASI KE TOKO
        // ==========================================
        binding.btnKeTokoBg.setOnClickListener {
            mainkanSuaraKlik()
            startActivity(Intent(this, TukarPoinActivity::class.java))
        }

        binding.btnKeTokoChar.setOnClickListener {
            mainkanSuaraKlik()
            startActivity(Intent(this, TukarPoinActivity::class.java))
        }

        // ==========================================
        // INISIALISASI VIEWMODEL PROFIL
        // ==========================================
        val prefProfil = ProfilPreferences.getInstance(applicationContext.dataStoreProfil)
        val factory = ProfilViewModelFactory(ProfilRepository(prefProfil))
        viewModelProfil = ViewModelProvider(this, factory)[ProfilViewModel::class.java]

        viewModelProfil.getProfilData().observe(this) { data ->
            savedBgId = data.backgroundId
            savedCharId = data.characterId
            ownsBgGym = data.hasBgGym
            ownsBgTaman = data.hasBgTaman // Asumsi hasBgTaman sudah ada di ProfilData

            // 1. Sembunyikan item jika belum dibeli
            binding.itemBg2.visibility = if (ownsBgGym) View.VISIBLE else View.GONE
            binding.itemBg3.visibility = if (ownsBgTaman) View.VISIBLE else View.GONE
            binding.itemChar2.visibility = if (ownsSkinElite) View.VISIBLE else View.GONE
            binding.itemChar3.visibility = if (ownsSkinSpecial) View.VISIBLE else View.GONE

            // 2. Set Ikon Thumbnail Sesuai Gender & Kepemilikan
            binding.ivThumbBg1.setImageResource(CharacterImageUtils.getBackgroundImageRes(userGender, 1))
            binding.ivThumbChar1.setImageResource(CharacterImageUtils.getCharacterImageRes(userGender, "Normal (Ideal)", 1))

            if (ownsBgGym) binding.ivThumbBg2.setImageResource(CharacterImageUtils.getBackgroundImageRes(userGender, 2))
            if (ownsBgTaman) binding.ivThumbBg3.setImageResource(CharacterImageUtils.getBackgroundImageRes(userGender, 3))

            if (ownsSkinElite) binding.ivThumbChar2.setImageResource(CharacterImageUtils.getCharacterImageRes(userGender, "Normal (Ideal)", 2))
            if (ownsSkinSpecial) binding.ivThumbChar3.setImageResource(CharacterImageUtils.getCharacterImageRes(userGender, "Normal (Ideal)", 3))

            // 3. Tampilkan Preview Default
            updatePreviewBg(savedBgId)
            updatePreviewChar(savedCharId)
        }

        // ==========================================
        // LOGIKA KLIK ITEM UNTUK PREVIEW
        // ==========================================
        binding.itemBg1.setOnClickListener {
            mainkanSuaraKlik()
            updatePreviewBg(1)
        }

        binding.itemBg2.setOnClickListener {
            mainkanSuaraKlik()
            updatePreviewBg(2)
        }

        binding.itemBg3.setOnClickListener {
            mainkanSuaraKlik()
            updatePreviewBg(3)
        }

        binding.itemChar1.setOnClickListener {
            mainkanSuaraKlik()
            updatePreviewChar(1)
        }

        binding.itemChar2.setOnClickListener {
            mainkanSuaraKlik()
            updatePreviewChar(2)
        }

        binding.itemChar3.setOnClickListener {
            mainkanSuaraKlik()
            updatePreviewChar(3)
        }

        binding.btnSimpanPreview.setOnClickListener {
            mainkanSuaraKlik()
            if (tempBgId == savedBgId && tempCharId == savedCharId) {
                Toast.makeText(this, "Anda sudah menggunakan kombinasi ini", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            tampilkanKonfirmasiSimpan()
        }
    }

    // ==========================================
    // FUNGSI UPDATE PREVIEW VISUAL & CEKLIS
    // ==========================================
    private fun updatePreviewBg(bgId: Int) {
        tempBgId = bgId

        // Reset Bingkai & Ceklis Default
        binding.itemBg1.strokeColor = Color.parseColor("#E0E0E0")
        binding.itemBg2.strokeColor = Color.parseColor("#E0E0E0")
        binding.itemBg3.strokeColor = Color.parseColor("#E0E0E0")

        binding.cardCeklisBg1.visibility = View.GONE
        binding.cardCeklisBg2.visibility = View.GONE
        binding.cardCeklisBg3.visibility = View.GONE

        binding.ivPreviewBg.setImageResource(CharacterImageUtils.getBackgroundImageRes(userGender, bgId))

        // Nyalakan Ceklis & Border Biru pada Item yang Aktif
        when (bgId) {
            1 -> {
                binding.itemBg1.strokeColor = Color.parseColor("#33A1E0")
                binding.cardCeklisBg1.visibility = View.VISIBLE
            }
            2 -> {
                binding.itemBg2.strokeColor = Color.parseColor("#33A1E0")
                binding.cardCeklisBg2.visibility = View.VISIBLE
            }
            3 -> {
                binding.itemBg3.strokeColor = Color.parseColor("#33A1E0")
                binding.cardCeklisBg3.visibility = View.VISIBLE
            }
        }
    }

    private fun updatePreviewChar(charId: Int) {
        tempCharId = charId

        // Reset Bingkai & Ceklis Default Karakter
        binding.itemChar1.strokeColor = Color.parseColor("#E0E0E0")
        binding.itemChar2.strokeColor = Color.parseColor("#E0E0E0")
        binding.itemChar3.strokeColor = Color.parseColor("#E0E0E0")

        binding.cardCeklisChar1.visibility = View.GONE
        binding.cardCeklisChar2.visibility = View.GONE
        binding.cardCeklisChar3.visibility = View.GONE

        binding.ivPreviewChar.setImageResource(CharacterImageUtils.getCharacterImageRes(userGender, "Normal (Ideal)", charId))

        // Nyalakan Ceklis & Border Biru pada Item yang Aktif
        when (charId) {
            1 -> {
                binding.itemChar1.strokeColor = Color.parseColor("#33A1E0")
                binding.cardCeklisChar1.visibility = View.VISIBLE
            }
            2 -> {
                binding.itemChar2.strokeColor = Color.parseColor("#33A1E0")
                binding.cardCeklisChar2.visibility = View.VISIBLE
            }
            3 -> {
                binding.itemChar3.strokeColor = Color.parseColor("#33A1E0")
                binding.cardCeklisChar3.visibility = View.VISIBLE
            }
        }
    }

    // ==========================================
    // FUNGSI 3 TAHAP: KONFIRMASI -> LOADING -> SUKSES
    // ==========================================
    private fun tampilkanKonfirmasiSimpan() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_konfirmasi_inventaris, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnBatal = dialogView.findViewById<MaterialButton>(R.id.btn_dialog_batal)
        val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btn_dialog_simpan)

        btnBatal.setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
        }

        btnSimpan.setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
            tampilkanLoadingOverlay()
        }

        dialog.show()
    }

    private fun tampilkanLoadingOverlay() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_loading_gamified, null)
        val loadingDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            loadingDialog.dismiss()
            viewModelProfil.saveEquippedBg(tempBgId)
            viewModelProfil.saveEquippedChar(tempCharId)
            tampilkanSukses()
        }, 1500)
    }

    private fun tampilkanSukses() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_sukses_gamified, null)
        val suksesDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        suksesDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnKeren = dialogView.findViewById<MaterialButton>(R.id.btn_dialog_sukses)

        btnKeren.setOnClickListener {
            mainkanSuaraKlik()
            suksesDialog.dismiss()
            finish()
        }

        suksesDialog.show()
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