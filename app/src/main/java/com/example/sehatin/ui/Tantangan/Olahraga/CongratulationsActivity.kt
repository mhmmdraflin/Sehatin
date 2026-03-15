package com.example.sehatin.ui.Tantangan.Olahraga

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.ui.Tantangan.TantanganPreferences
import com.example.sehatin.ui.Tantangan.TantanganRepository
import com.example.sehatin.ui.Tantangan.TantanganViewModel
import com.example.sehatin.ui.Tantangan.TantanganViewModelFactory
import com.example.sehatin.ui.Tantangan.dataStoreTantangan
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class CongratulationsActivity : AppCompatActivity() {

    private lateinit var viewModel: TantanganViewModel
    private var isChestOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congratulations)

        val ivChest = findViewById<ImageView>(R.id.iv_chest_reward)
        val tvInstruksi = findViewById<TextView>(R.id.tv_tap_instruction)
        val cardReward = findViewById<MaterialCardView>(R.id.card_reward_panel)
        val btnKlaim = findViewById<MaterialButton>(R.id.btn_klaim_kembali)
        val tvExp = findViewById<TextView>(R.id.tv_reward_exp)
        val tvPoin = findViewById<TextView>(R.id.tv_reward_poin)

        val expDiterima = intent.getIntExtra("HASIL_EXP", 0)
        val poinDiterima = intent.getIntExtra("HASIL_POIN", 0)

        tvExp.text = "+$expDiterima EXP"
        tvPoin.text = "+$poinDiterima Poin"

        // ==========================================
        // 1. AMBIL IDENTITAS USER (Menggunakan getName)
        // ==========================================
        val userPref = UserPreference(this)
        val userKey = userPref.getName() ?: "guest_user"

        // 2. INISIALISASI VIEWMODEL
        val prefTantangan = TantanganPreferences.getInstance(applicationContext.dataStoreTantangan)
        val factory = TantanganViewModelFactory(TantanganRepository(prefTantangan))
        viewModel = ViewModelProvider(this, factory)[TantanganViewModel::class.java]

        // 3. SIMPAN LANGSUNG BERDASARKAN USER KEY
        viewModel.tambahExp(userKey, expDiterima)
        viewModel.tambahPoin(userKey, poinDiterima)

        btnKlaim.isEnabled = false

        ivChest.setOnClickListener {
            if (!isChestOpened) {
                isChestOpened = true
                tvInstruksi.visibility = View.GONE

                val chestAnimation = ivChest.drawable as? AnimationDrawable
                if (chestAnimation != null) {
                    chestAnimation.stop()
                    chestAnimation.start()
                }

                cardReward.animate().alpha(1f).setDuration(500).start()
                btnKlaim.animate().alpha(1f).setDuration(500).withEndAction {
                    btnKlaim.isEnabled = true
                }.start()
            }
        }

        btnKlaim.setOnClickListener {
            val intent = Intent(this, com.example.sehatin.Main.MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}