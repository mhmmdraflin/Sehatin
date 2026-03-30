package com.example.sehatin.ui.Dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.databinding.FragmentDashboardBinding
import com.example.sehatin.ui.SideFeature.BodyMassIndexActivity
import com.example.sehatin.ui.SideFeature.Informasi_MakananActivity
import com.example.sehatin.ui.SideFeature.JamMakan.Pengingat_Jam_MakanActivity
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaActivity
import com.example.sehatin.ui.SideFeature.JamMakan.NotifikasiRiwayat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Import Profil
import com.example.sehatin.ui.Profil.ProfilPreferences
import com.example.sehatin.ui.Profil.ProfilRepository
import com.example.sehatin.ui.Profil.ProfilViewModel
import com.example.sehatin.ui.Profil.ProfilViewModelFactory
import com.example.sehatin.ui.Profil.dataStoreProfil

// Import Tantangan
import com.example.sehatin.ui.Tantangan.TantanganPreferences
import com.example.sehatin.ui.Tantangan.TantanganRepository
import com.example.sehatin.ui.Tantangan.TantanganViewModel
import com.example.sehatin.ui.Tantangan.TantanganViewModelFactory
import com.example.sehatin.ui.Tantangan.dataStoreTantangan

// Import Olahraga
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaPreferences
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaRepository
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaViewModel
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaViewModelFactory
import com.example.sehatin.ui.SideFeature.Olahraga.dataStore

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // Penampung 2 Jenis Kalori
    private var kaloriHariIni = 0
    private var totalKaloriAkumulasi = 0
    private var simulasiKondisiTubuh = "Belum Dihitung"

    // VARIABEL BARU: Menyimpan ID Skin & Background yang sedang dipakai
    private var currentSkinId = 1
    private var currentBackgroundId = 1

    private val dashboardViewModel: DashboardViewModel by viewModels {
        val pref = UserPreference(requireContext())
        val repo = DashboardRepository(pref)
        DashboardViewModelFactory(repo)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTombolKategori()
        binding.dashboardEditprof.setOnClickListener { bukaRiwayatNotifikasi() }

        // ==========================================
        // PEMISAHAN KLIK AREA AGAR TIDAK BENTROK
        // ==========================================

        // 1. Jika teks merah KALORI diklik -> Buka Pop-up Kalori
        binding.tvKaloriTerbakar.setOnClickListener {
            tampilkanPopUpInfoKalori()
        }

        // 2. Jika angka BERAT BADAN diklik -> Buka Pop-up Update Berat
        binding.tvBeratVal.setOnClickListener {
            tampilkanDialogUpdateBerat()
        }

        val userPref = UserPreference(requireContext())
        val userKey = userPref.getName() ?: "guest_user"

        // 1. INTEGRASI EXP & POIN
        val prefTantangan = TantanganPreferences.getInstance(requireActivity().applicationContext.dataStoreTantangan)
        val factoryTantangan = TantanganViewModelFactory(TantanganRepository(prefTantangan))
        val tantanganViewModel = ViewModelProvider(this, factoryTantangan)[TantanganViewModel::class.java]

        tantanganViewModel.getTotalExp(userKey).observe(viewLifecycleOwner) { totalExp ->
            val MAX_EXP_PER_LEVEL = 100
            val levelSekarang = (totalExp / MAX_EXP_PER_LEVEL) + 1
            val progressSaatIni = totalExp % MAX_EXP_PER_LEVEL

            binding.tvLevelAngka.text = levelSekarang.toString()
            binding.pbExpLevel.setProgressCompat(progressSaatIni, true)
        }

        tantanganViewModel.getTotalPoin(userKey).observe(viewLifecycleOwner) { totalPoin ->
            binding.dashboardPoint.text = "$totalPoin Poin"
        }

        // ========================================================
        // 2. AMBIL 2 JENIS KALORI DARI DATABASE OLAHRAGA
        // ========================================================
        val prefOlahraga = OlahragaPreferences.getInstance(requireActivity().applicationContext.dataStore)
        val factoryOlahraga = OlahragaViewModelFactory(OlahragaRepository(prefOlahraga))
        val olahragaViewModel = ViewModelProvider(this, factoryOlahraga)[OlahragaViewModel::class.java]

        // Pantau Kalori Harian (Untuk teks merah)
        olahragaViewModel.getKaloriHarian().observe(viewLifecycleOwner) { kaloriHarian ->
            kaloriHariIni = kaloriHarian
            tampilkanDataProfil()
        }

        // Pantau Total Kalori Akumulasi (Untuk kurusin karakter)
        olahragaViewModel.getKaloriAkumulasi().observe(viewLifecycleOwner) { kaloriTotal ->
            totalKaloriAkumulasi = kaloriTotal
            tampilkanDataProfil()
        }

        // 3. INTEGRASI INVENTARIS SKIN
        val prefProfil = ProfilPreferences.getInstance(requireActivity().applicationContext.dataStoreProfil)
        val factoryProfil = ProfilViewModelFactory(ProfilRepository(prefProfil))
        val viewModelProfil = ViewModelProvider(this, factoryProfil)[ProfilViewModel::class.java]

        viewModelProfil.getProfilData().observe(viewLifecycleOwner) { data ->

            // SIMPAN ID SKIN & BACKGROUND YANG TERPILIH (Agar bisa dipakai di bagian bawah)
            currentSkinId = data.characterId
            currentBackgroundId = data.backgroundId

            tampilkanDataProfil()
        }
    }

    // ========================================================
    // TAMPILKAN CUSTOM DIALOG XML UNTUK INFO KALORI
    // ========================================================
    private fun tampilkanPopUpInfoKalori() {
        val beratTurun = totalKaloriAkumulasi / 100.0
        val formatBeratTurun = String.format("%.1f", beratTurun)

        val pesanHarian = if (kaloriHariIni > 0) {
            "Hari ini kamu sudah membakar $kaloriHariIni Kkal."
        } else {
            "Kamu belum membakar kalori hari ini. Ayo mulai bergerak!"
        }

        val pesanTotal = if (totalKaloriAkumulasi > 0) {
            "Total Usahamu: $totalKaloriAkumulasi Kkal terbakar sejauh ini.\n\nEfek Gamifikasi: Berat badan karaktermu telah menyusut sebanyak $formatBeratTurun Kg! Teruslah konsisten untuk mencapai tubuh ideal."
        } else {
            "Mulai selesaikan Tantangan atau Fitur Olahraga untuk membentuk tubuh karaktermu!"
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_info_kalori, null)

        val tvHarian = dialogView.findViewById<TextView>(R.id.tv_dialog_harian)
        val tvTotal = dialogView.findViewById<TextView>(R.id.tv_dialog_total)
        val btnTutup = dialogView.findViewById<Button>(R.id.btn_dialog_tutup)

        tvHarian.text = pesanHarian
        tvTotal.text = pesanTotal

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnTutup.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // ========================================================
    // TAMPILKAN CUSTOM DIALOG UNTUK UPDATE BERAT BADAN
    // ========================================================
    private fun tampilkanDialogUpdateBerat() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_berat, null)
        val etBeratAngka = dialogView.findViewById<EditText>(R.id.et_update_berat_angka)
        val btnBatal = dialogView.findViewById<Button>(R.id.btn_update_berat_batal)
        val btnSimpan = dialogView.findViewById<Button>(R.id.btn_update_berat_simpan)

        val fisikUser = dashboardViewModel.getUserBody()
        etBeratAngka.setText(fisikUser.berat)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnBatal.setOnClickListener { dialog.dismiss() }

        btnSimpan.setOnClickListener {
            val beratBaruStr = etBeratAngka.text.toString().trim()

            if (beratBaruStr.isEmpty()) {
                etBeratAngka.error = "Berat badan wajib diisi!"
                return@setOnClickListener
            }

            val beratDouble = beratBaruStr.toDoubleOrNull()
            if (beratDouble == null || beratDouble <= 10.0 || beratDouble >= 250.0) {
                etBeratAngka.error = "Masukkan angka yang logis (10-250 kg)"
                return@setOnClickListener
            }

            // 1. SIMPAN DATA BARAT BADAN BARU KE DATABASE
            dashboardViewModel.updateBeratBadan(beratBaruStr)

            // 2. TUTUP DIALOG POP-UP
            dialog.dismiss()

            // 3. REFRESH TAMPILAN SECARA INSTAN
            tampilkanDataProfil()

            // 4. MUNCULKAN PESAN SUKSES
            Toast.makeText(requireContext(), "Berat badan berhasil diupdate ke $beratBaruStr kg!", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        cekBadgeNotifikasi()
    }

    private fun cekBadgeNotifikasi() {
        val prefs = requireContext().getSharedPreferences("NotifikasiPrefs", Context.MODE_PRIVATE)
        val jsonLama = prefs.getString("LIST_NOTIFIKASI", null)

        if (jsonLama != null && jsonLama != "[]") {
            binding.badgeNotifikasi.visibility = View.VISIBLE
        } else {
            binding.badgeNotifikasi.visibility = View.GONE
        }
    }

    private fun bukaRiwayatNotifikasi() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_pusat_notifikasi, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val rvNotif = dialogView.findViewById<RecyclerView>(R.id.rv_notifikasi)
        val tvKosong = dialogView.findViewById<TextView>(R.id.tv_notif_kosong)
        val btnHapusSemua = dialogView.findViewById<Button>(R.id.btn_hapus_semua_notif)
        val btnTutup = dialogView.findViewById<Button>(R.id.btn_tutup_notif)

        val prefs = requireContext().getSharedPreferences("NotifikasiPrefs", Context.MODE_PRIVATE)
        val jsonLama = prefs.getString("LIST_NOTIFIKASI", null)

        var daftarRiwayat = mutableListOf<NotifikasiRiwayat>()
        if (jsonLama != null && jsonLama != "[]") {
            val type = object : TypeToken<MutableList<NotifikasiRiwayat>>() {}.type
            daftarRiwayat = Gson().fromJson(jsonLama, type)
        }

        val adapterNotif = NotifikasiAdapter(daftarRiwayat)
        rvNotif.layoutManager = LinearLayoutManager(requireContext())
        rvNotif.adapter = adapterNotif

        if (daftarRiwayat.isEmpty()) {
            rvNotif.visibility = View.GONE
            tvKosong.visibility = View.VISIBLE
            btnHapusSemua.isEnabled = false
        } else {
            rvNotif.visibility = View.VISIBLE
            tvKosong.visibility = View.GONE
            btnHapusSemua.isEnabled = true
        }

        btnTutup.setOnClickListener { dialog.dismiss() }

        btnHapusSemua.setOnClickListener {
            prefs.edit().clear().apply()
            adapterNotif.updateData(emptyList())
            rvNotif.visibility = View.GONE
            tvKosong.visibility = View.VISIBLE
            btnHapusSemua.isEnabled = false
            cekBadgeNotifikasi()
        }

        dialog.show()
    }

    private fun setupTombolKategori() {
        binding.btnBmiCard.setOnClickListener {
            startActivity(Intent(requireContext(), BodyMassIndexActivity::class.java))
        }
        binding.btnPengingatMakanCard.setOnClickListener {
            startActivity(Intent(requireContext(), Pengingat_Jam_MakanActivity::class.java))
        }
        binding.btnInfoMakananCard.setOnClickListener {
            startActivity(Intent(requireContext(), Informasi_MakananActivity::class.java))
        }
        binding.btnOlahragaCard.setOnClickListener {
            startActivity(Intent(requireContext(), OlahragaActivity::class.java))
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun tampilkanDataProfil() {
        val nama = dashboardViewModel.getName() ?: "Sobat Sehatin"
        val fisikUser = dashboardViewModel.getUserBody()
        val kondisiAwal = dashboardViewModel.getKondisiTubuh()

        binding.dashboardUsername.text = nama
        binding.tvUmurVal.text = "${fisikUser.umur} Tahun"
        binding.tvTinggiVal.text = "${fisikUser.tinggi} cm"

        // ========================================================
        // 1. LOGIKA GAMIFIKASI: MENGURANGI BERAT BADAN
        // ========================================================
        val beratAwal = fisikUser.berat.toDoubleOrNull() ?: 0.0
        val tinggiAwal = fisikUser.tinggi.toDoubleOrNull() ?: 0.0
        val gender = fisikUser.gender

        val kgTurun = totalKaloriAkumulasi / 100.0 // 100 Kkal = 1 Kg turun (Simulasi)
        var beratSimulasi = beratAwal - kgTurun

        if (beratSimulasi < 30.0 && beratAwal > 0) {
            beratSimulasi = 30.0
        }

        if (beratAwal > 0) {
            binding.tvBeratVal.text = "${String.format("%.1f", beratSimulasi)} kg"
        } else {
            binding.tvBeratVal.text = "0 kg"
        }

        // ========================================================
        // 2. HITUNG ULANG KATEGORI BMI UNTUK MENGUBAH KARAKTER
        // ========================================================
        simulasiKondisiTubuh = kondisiAwal

        if (beratAwal > 0 && tinggiAwal > 0) {
            val tinggiMeter = tinggiAwal / 100.0
            val bmiScore = beratSimulasi / (tinggiMeter * tinggiMeter)

            if (gender == "L") {
                simulasiKondisiTubuh = when {
                    bmiScore < 17.0 -> "Kurus"
                    bmiScore < 23.0 -> "Normal (Ideal)"
                    bmiScore <= 27.0 -> "Gemuk"
                    else -> "Obesitas"
                }
            } else {
                simulasiKondisiTubuh = when {
                    bmiScore < 18.0 -> "Kurus"
                    bmiScore < 25.0 -> "Normal (Ideal)"
                    bmiScore <= 27.0 -> "Gemuk"
                    else -> "Obesitas"
                }
            }
        }

        binding.kondisiTubuh.text = simulasiKondisiTubuh

        // ========================================================
        // 3. TAMPILKAN TEKS MERAH KALORI
        // ========================================================
        binding.tvKaloriTerbakar.visibility = View.VISIBLE
        binding.tvKaloriTerbakar.text = "🔥 $kaloriHariIni Kkal Keluar Hari Ini"

        // ========================================================
        // 4. MENGUBAH WUJUD KARAKTER & BACKGROUND (UPDATE: DUKUNGAN SKIN & GENDER)
        // ========================================================
        if (gender == "L") {
            binding.dashProfilePict.setImageResource(R.drawable.profile_boy)

            // SET BACKGROUND LAKI-LAKI (Sesuai ID Background yang dipilih)
            val bgRes = when (currentBackgroundId) {
                2 -> R.drawable.background_laki_perempuan_skin_elite // ID 2: Elite Background
                3 -> R.drawable.background_lakilaki_skin_special     // ID 3: Special Background Laki-laki
                else -> R.drawable.bg_dashboard_character            // ID 1: Basic Background Laki-laki
            }
            binding.bgIconCharacter.setImageResource(bgRes)

            // NESTED WHEN: Cek Skin ID dulu, baru cek BMI
            val imageRes = when (currentSkinId) {
                1 -> { // Skin BASIC Laki-laki
                    when (simulasiKondisiTubuh) {
                        "Kurus" -> R.drawable.character_boy_lebih_kurus
                        "Normal (Ideal)" -> R.drawable.character_ideal
                        "Gemuk" -> R.drawable.character_boy_gemuk
                        "Obesitas" -> R.drawable.character_boy_obesitas
                        else -> R.drawable.character_ideal
                    }
                }
                2 -> { // Skin ELITE Laki-laki (Ganti dengan aset Anda nanti)
                    when (simulasiKondisiTubuh) {
                        "Kurus" -> R.drawable.boy_skin_elite_kurus
                        "Normal (Ideal)" -> R.drawable.boy_skin_elite_ideal
                        "Gemuk" -> R.drawable.boy_skin_elite_gemuk
                        "Obesitas" -> R.drawable.boy_skin_elite_obesitas
                        else -> R.drawable.boy_skin_elite_ideal
                    }
                }
                3 -> { // Skin SPECIAL Laki-laki (Ganti dengan aset Anda nanti)
                    when (simulasiKondisiTubuh) {
                        "Kurus" -> R.drawable.boy_skin_special_kurus
                        "Normal (Ideal)" -> R.drawable.boy_skin_special_ideal
                        "Gemuk" -> R.drawable.boy_skin_special_gemuk
                        "Obesitas" -> R.drawable.boy_skin_special_obesitas
                        else -> R.drawable.boy_skin_special_ideal
                    }
                }
                else -> R.drawable.character_ideal
            }
            binding.characterIllustration.setImageResource(imageRes)

        } else {
            binding.dashProfilePict.setImageResource(R.drawable.profile_girl)

            // SET BACKGROUND PEREMPUAN (Sesuai ID Background yang dipilih)
            val bgRes = when (currentBackgroundId) {
                2 -> R.drawable.background_perempuan_skin_elite      // ID 2: Elite Background
                3 -> R.drawable.background_perempuan_skin_special    // ID 3: Special Background Perempuan
                else -> R.drawable.bg_dashboard_girl                 // ID 1: Basic Background Perempuan
            }
            binding.bgIconCharacter.setImageResource(bgRes)

            // NESTED WHEN: Cek Skin ID dulu, baru cek BMI
            val imageRes = when (currentSkinId) {
                1 -> { // Skin BASIC Perempuan
                    when (simulasiKondisiTubuh) {
                        "Kurus" -> R.drawable.character_girl_lebih_kurus
                        "Normal (Ideal)" -> R.drawable.character_girl_ideal
                        "Gemuk" -> R.drawable.character_girl_gemuk
                        "Obesitas" -> R.drawable.character_girl_obesitas
                        else -> R.drawable.character_girl
                    }
                }
                2 -> { // Skin ELITE Perempuan (Ganti dengan aset Anda nanti)
                    when (simulasiKondisiTubuh) {
                        "Kurus" -> R.drawable.girl_skin_elite_kurus
                        "Normal (Ideal)" -> R.drawable.girl_skin_elite_ideal
                        "Gemuk" -> R.drawable.girl_skin_elite_gemuk
                        "Obesitas" -> R.drawable.girl_skin_elite_obesitas
                        else -> R.drawable.girl_skin_elite_ideal
                    }
                }
                3 -> { // Skin SPECIAL Perempuan (Ganti dengan aset Anda nanti)
                    when (simulasiKondisiTubuh) {
                        "Kurus" -> R.drawable.girl_skin_special_kurus
                        "Normal (Ideal)" -> R.drawable.girl_skin_special_ideal
                        "Gemuk" -> R.drawable.girl_skin_special_gemuk
                        "Obesitas" -> R.drawable.girl_skin_special_obesitas
                        else -> R.drawable.girl_skin_special_ideal
                    }
                }
                else -> R.drawable.character_girl_ideal
            }
            binding.characterIllustration.setImageResource(imageRes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}