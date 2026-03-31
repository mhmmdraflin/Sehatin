package com.example.sehatin.ui.Tantangan.Olahraga

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sehatin.R
import com.google.android.material.card.MaterialCardView

class DetailTantanganActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tantangan)

        findViewById<MaterialCardView>(R.id.btn_back).setOnClickListener { finish() }

        // ==========================================
        // Misi 1: Push Up
        // ==========================================
        findViewById<MaterialCardView>(R.id.btn_tantangan_pushup).setOnClickListener {
            val intent = Intent(this, PreviewTantanganActivity::class.java).apply {
                putExtra("NAMA_MISI", "Lakukan Push Up")
                putExtra("TARGET_ANGKA", 15)
                putExtra("REWARD_POIN", 25)
                putExtra("REWARD_EXP", 30)
                putExtra("EXTRA_GIF_FILE", R.raw.push_up_illustration)
            }
            startActivity(intent)
        }

        // ==========================================
        // Misi 2: Sit Up
        // ==========================================
        findViewById<MaterialCardView>(R.id.btn_tantangan_situp).setOnClickListener {
            val intent = Intent(this, PreviewTantanganActivity::class.java).apply {
                putExtra("NAMA_MISI", "Lakukan Sit Up")
                putExtra("TARGET_ANGKA", 10)
                putExtra("REWARD_POIN", 15)
                putExtra("REWARD_EXP", 20)
                putExtra("EXTRA_GIF_FILE", R.raw.sit_up_illustration)
            }
            startActivity(intent)
        }

        // ==========================================
        // Misi 3: Plank
        // ==========================================
        findViewById<MaterialCardView>(R.id.btn_tantangan_plank).setOnClickListener {
            val intent = Intent(this, PreviewTantanganActivity::class.java).apply {
                putExtra("NAMA_MISI", "Lakukan Plank")
                putExtra("TARGET_ANGKA", 45) // 45 Detik
                putExtra("REWARD_POIN", 30)
                putExtra("REWARD_EXP", 35)
                putExtra("EXTRA_GIF_FILE", R.raw.plank_illustration)
            }
            startActivity(intent)
        }

        // ==========================================
        // Misi 4: Squat
        // ==========================================
        findViewById<MaterialCardView>(R.id.btn_tantangan_squat).setOnClickListener {
            val intent = Intent(this, PreviewTantanganActivity::class.java).apply {
                putExtra("NAMA_MISI", "Lakukan Squat")
                putExtra("TARGET_ANGKA", 20)
                putExtra("REWARD_POIN", 25)
                putExtra("REWARD_EXP", 30)
                putExtra("EXTRA_GIF_FILE", R.raw.squat_illustration)
            }
            startActivity(intent)
        }

        // ==========================================
        // Misi 5: Lunges
        // ==========================================
        findViewById<MaterialCardView>(R.id.btn_tantangan_lunges).setOnClickListener {
            val intent = Intent(this, PreviewTantanganActivity::class.java).apply {
                putExtra("NAMA_MISI", "Lakukan Lunges")
                putExtra("TARGET_ANGKA", 15)
                putExtra("REWARD_POIN", 20)
                putExtra("REWARD_EXP", 25)
                putExtra("EXTRA_GIF_FILE", R.raw.lunges_illustration)
            }
            startActivity(intent)
        }

        // ==========================================
        // Misi 6: Bicycle Crunch
        // ==========================================
        findViewById<MaterialCardView>(R.id.btn_tantangan_bicycle_crunch).setOnClickListener {
            val intent = Intent(this, PreviewTantanganActivity::class.java).apply {
                putExtra("NAMA_MISI", "Bicycle Crunch")
                putExtra("TARGET_ANGKA", 20)
                putExtra("REWARD_POIN", 30)
                putExtra("REWARD_EXP", 35)
                putExtra("EXTRA_GIF_FILE", R.raw.bicycle_crunch_illustration)
            }
            startActivity(intent)
        }

        // ==========================================
        // Misi 7: Leg Raise
        // ==========================================
        findViewById<MaterialCardView>(R.id.btn_tantangan_leg_raise).setOnClickListener {
            val intent = Intent(this, PreviewTantanganActivity::class.java).apply {
                putExtra("NAMA_MISI", "Lakukan Leg Raise")
                putExtra("TARGET_ANGKA", 15)
                putExtra("REWARD_POIN", 20)
                putExtra("REWARD_EXP", 25)
                putExtra("EXTRA_GIF_FILE", R.raw.leg_raise_illustration)
            }
            startActivity(intent)
        }
    }
}