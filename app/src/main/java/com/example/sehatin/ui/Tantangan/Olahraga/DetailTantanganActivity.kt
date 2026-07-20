package com.example.sehatin.ui.Tantangan.Olahraga

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sehatin.Data.Model.UserPreference // IMPORT USER PREFERENCE
import com.example.sehatin.R
import com.google.android.material.card.MaterialCardView

class DetailTantanganActivity : AppCompatActivity() {

    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tantangan)

        // =======================================================
        // INISIALISASI SOUNDPOOL
        // =======================================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        clickSoundId = soundPool?.load(this, R.raw.tombol_klik_sehatin, 1) ?: 0

        // Tombol Kembali
        findViewById<MaterialCardView>(R.id.btn_back).setOnClickListener {
            mainkanSuaraKlik()
            finish()
        }

        // =======================================================
        // AMBIL IDENTITAS USER SAAT INI
        // =======================================================
        val userPref = UserPreference(this)
        val userKey = userPref.getName() ?: "guest_user"

        // =======================================================
        // AMBIL DATA PROGRESS BERDASARKAN AKUN YANG LOGIN
        // =======================================================
        val prefs = getSharedPreferences("OlahragaProgressPrefs_$userKey", Context.MODE_PRIVATE)

        val isPushUpDone = prefs.getBoolean("Lakukan Push Up", false)
        val isSitUpDone = prefs.getBoolean("Lakukan Sit Up", false)
        val isPlankDone = prefs.getBoolean("Lakukan Plank", false)
        val isSquatDone = prefs.getBoolean("Lakukan Squat", false)
        val isLungesDone = prefs.getBoolean("Lakukan Lunges", false)
        val isBicycleDone = prefs.getBoolean("Bicycle Crunch", false)

        // Hubungkan Element View XML secara Eksplisit
        val btnPushUp = findViewById<MaterialCardView>(R.id.btn_tantangan_pushup)
        val ivPushUp = findViewById<ImageView>(R.id.iv_status_pushup)

        val btnSitUp = findViewById<MaterialCardView>(R.id.btn_tantangan_situp)
        val ivSitUp = findViewById<ImageView>(R.id.iv_status_situp)

        val btnPlank = findViewById<MaterialCardView>(R.id.btn_tantangan_plank)
        val ivPlank = findViewById<ImageView>(R.id.iv_status_plank)

        val btnSquat = findViewById<MaterialCardView>(R.id.btn_tantangan_squat)
        val ivSquat = findViewById<ImageView>(R.id.iv_status_squat)

        val btnLunges = findViewById<MaterialCardView>(R.id.btn_tantangan_lunges)
        val ivLunges = findViewById<ImageView>(R.id.iv_status_lunges)

        val btnBicycle = findViewById<MaterialCardView>(R.id.btn_tantangan_bicycle_crunch)
        val ivBicycle = findViewById<ImageView>(R.id.iv_status_bicycle)

        val btnLegRaise = findViewById<MaterialCardView>(R.id.btn_tantangan_leg_raise)
        val ivLegRaise = findViewById<ImageView>(R.id.iv_status_leg_raise)

        // ==========================================
        // MISI 1: PUSH UP (Selalu Terbuka)
        // ==========================================
        btnPushUp.alpha = 1.0f
        ivPushUp.setImageResource(android.R.drawable.ic_media_play)
        btnPushUp.setOnClickListener {
            mainkanSuaraKlik()
            bukaPreviewTantangan("Lakukan Push Up", 15, 25, 30, R.raw.push_up_illustration)
        }

        // ==========================================
        // MISI 2: SIT UP (Syarat: Push Up Harus Selesai)
        // ==========================================
        if (!isPushUpDone) {
            btnSitUp.alpha = 0.5f
            ivSitUp.setImageResource(android.R.drawable.ic_lock_lock)
            btnSitUp.setOnClickListener {
                mainkanSuaraKlik()
                Toast.makeText(this, "Terkunci! Selesaikan Push Up terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        } else {
            btnSitUp.alpha = 1.0f
            ivSitUp.setImageResource(android.R.drawable.ic_media_play)
            btnSitUp.setOnClickListener {
                mainkanSuaraKlik()
                bukaPreviewTantangan("Lakukan Sit Up", 10, 15, 20, R.raw.sit_up_illustration)
            }
        }

        // ==========================================
        // MISI 3: PLANK (Syarat: Sit Up Harus Selesai)
        // ==========================================
        if (!isSitUpDone) {
            btnPlank.alpha = 0.5f
            ivPlank.setImageResource(android.R.drawable.ic_lock_lock)
            btnPlank.setOnClickListener {
                mainkanSuaraKlik()
                Toast.makeText(this, "Terkunci! Selesaikan Sit Up terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        } else {
            btnPlank.alpha = 1.0f
            ivPlank.setImageResource(android.R.drawable.ic_media_play)
            btnPlank.setOnClickListener {
                mainkanSuaraKlik()
                bukaPreviewTantangan("Lakukan Plank", 45, 30, 35, R.raw.plank_illustration)
            }
        }

        // ==========================================
        // MISI 4: SQUAT (Syarat: Plank Harus Selesai)
        // ==========================================
        if (!isPlankDone) {
            btnSquat.alpha = 0.5f
            ivSquat.setImageResource(android.R.drawable.ic_lock_lock)
            btnSquat.setOnClickListener {
                mainkanSuaraKlik()
                Toast.makeText(this, "Terkunci! Selesaikan Plank terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        } else {
            btnSquat.alpha = 1.0f
            ivSquat.setImageResource(android.R.drawable.ic_media_play)
            btnSquat.setOnClickListener {
                mainkanSuaraKlik()
                bukaPreviewTantangan("Lakukan Squat", 20, 25, 30, R.raw.squat_illustration)
            }
        }

        // ==========================================
        // MISI 5: LUNGES (Syarat: Squat Harus Selesai)
        // ==========================================
        if (!isSquatDone) {
            btnLunges.alpha = 0.5f
            ivLunges.setImageResource(android.R.drawable.ic_lock_lock)
            btnLunges.setOnClickListener {
                mainkanSuaraKlik()
                Toast.makeText(this, "Terkunci! Selesaikan Squat terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        } else {
            btnLunges.alpha = 1.0f
            ivLunges.setImageResource(android.R.drawable.ic_media_play)
            btnLunges.setOnClickListener {
                mainkanSuaraKlik()
                bukaPreviewTantangan("Lakukan Lunges", 15, 20, 25, R.raw.lunges_illustration)
            }
        }

        // ==========================================
        // MISI 6: BICYCLE CRUNCH (Syarat: Lunges Harus Selesai)
        // ==========================================
        if (!isLungesDone) {
            btnBicycle.alpha = 0.5f
            ivBicycle.setImageResource(android.R.drawable.ic_lock_lock)
            btnBicycle.setOnClickListener {
                mainkanSuaraKlik()
                Toast.makeText(this, "Terkunci! Selesaikan Lunges terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        } else {
            btnBicycle.alpha = 1.0f
            ivBicycle.setImageResource(android.R.drawable.ic_media_play)
            btnBicycle.setOnClickListener {
                mainkanSuaraKlik()
                bukaPreviewTantangan("Bicycle Crunch", 20, 30, 35, R.raw.bicycle_crunch_illustration)
            }
        }

        // ==========================================
        // MISI 7: LEG RAISE (Syarat: Bicycle Harus Selesai)
        // ==========================================
        if (!isBicycleDone) {
            btnLegRaise.alpha = 0.5f
            ivLegRaise.setImageResource(android.R.drawable.ic_lock_lock)
            btnLegRaise.setOnClickListener {
                mainkanSuaraKlik()
                Toast.makeText(this, "Terkunci! Selesaikan Bicycle Crunch terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        } else {
            btnLegRaise.alpha = 1.0f
            ivLegRaise.setImageResource(android.R.drawable.ic_media_play)
            btnLegRaise.setOnClickListener {
                mainkanSuaraKlik()
                bukaPreviewTantangan("Lakukan Leg Raise", 15, 20, 25, R.raw.leg_raise_illustration)
            }
        }
    }

    private fun bukaPreviewTantangan(namaMisi: String, target: Int, poin: Int, exp: Int, gifFile: Int) {
        val intent = Intent(this, PreviewTantanganActivity::class.java).apply {
            putExtra("NAMA_MISI", namaMisi)
            putExtra("TARGET_ANGKA", target)
            putExtra("REWARD_POIN", poin)
            putExtra("REWARD_EXP", exp)
            putExtra("EXTRA_GIF_FILE", gifFile)
        }
        startActivity(intent)
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