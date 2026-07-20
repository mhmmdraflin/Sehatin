package com.example.sehatin.ui.Tantangan

import android.content.Intent
import android.media.AudioAttributes // IMPORT AUDIO ATTRIBUTES
import android.media.SoundPool       // IMPORT SOUNDPOOL
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.ui.Tantangan.Olahraga.DetailTantanganActivity
// [TAMBAHAN]: Import halaman tantangan makanan
import com.example.sehatin.ui.Tantangan.Makanan.DetailTantanganMakananActivity
import com.google.android.material.card.MaterialCardView

class TantanganFragment : Fragment() {

    private lateinit var viewModel: TantanganViewModel

    // VARIABEL SOUNDPOOL (Untuk Suara Zero-Delay)
    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tantangan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // =======================================================
        // INISIALISASI SOUNDPOOL
        // =======================================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2) // Maksimal putar 2 suara bersamaan
            .setAudioAttributes(audioAttributes)
            .build()

        // Load suara ke memori sejak awal Fragment dibuka
        clickSoundId = soundPool?.load(requireContext(), R.raw.tombol_klik_sehatin, 1) ?: 0
        // =======================================================

        // ==========================================
        // TENTUKAN IDENTITAS USER AKTIF (Menggunakan getName)
        // ==========================================
        val userPref = UserPreference(requireContext())
        val userKey = userPref.getName() ?: "guest_user"

        val pref = TantanganPreferences.getInstance(requireContext().dataStoreTantangan)
        val factory = TantanganViewModelFactory(TantanganRepository(pref))
        viewModel = ViewModelProvider(this, factory)[TantanganViewModel::class.java]

        val tvTotalPoin = view.findViewById<TextView>(R.id.tv_total_poin_header)
        val btnPeriksaOlahraga = view.findViewById<MaterialCardView>(R.id.btn_periksa)

        // [TAMBAHAN]: Hubungkan tombol periksa makanan ke ID di XML
        val btnPeriksaMakanan = view.findViewById<MaterialCardView>(R.id.btn_periksa_makanan)

        // BACA TOTAL POIN BERDASARKAN AKUN
        viewModel.getTotalPoin(userKey).observe(viewLifecycleOwner) { totalPoin ->
            tvTotalPoin.text = "$totalPoin Poin"
        }

        // ==========================================
        // FUNGSI KLIK TOMBOL OLAHRAGA
        // ==========================================
        btnPeriksaOlahraga.setOnClickListener {
            mainkanSuaraKlik() // MEMAINKAN SUARA KLIK
            val intent = Intent(requireContext(), DetailTantanganActivity::class.java)
            startActivity(intent)
        }

        // ==========================================
        // FUNGSI KLIK TOMBOL MAKANAN
        // ==========================================
        btnPeriksaMakanan.setOnClickListener {
            mainkanSuaraKlik() // MEMAINKAN SUARA KLIK
            val intent = Intent(requireContext(), DetailTantanganMakananActivity::class.java)
            startActivity(intent)
        }
    }

    // ========================================================
    // FUNGSI UNTUK MEMAINKAN SUARA KLIK (ZERO-DELAY)
    // ========================================================
    private fun mainkanSuaraKlik() {
        soundPool?.play(clickSoundId, 1f, 1f, 0, 0, 1f)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Bersihkan memori SoundPool agar tidak membebani HP
        soundPool?.release()
        soundPool = null
    }
}