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
import android.widget.TextView
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
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaPreferences
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaRepository
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaViewModel
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaViewModelFactory
import com.example.sehatin.ui.SideFeature.Olahraga.dataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // =======================================================
    // INISIALISASI DASHBOARD VIEWMODEL
    // =======================================================
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

        // Memuat data profil ke layar
        tampilkanDataProfil()

        // Memuat fungsi klik tombol kategori
        setupTombolKategori()

        // Memuat fungsi klik tombol lonceng notifikasi
        binding.dashboardEditprof.setOnClickListener {
            bukaRiwayatNotifikasi()
        }

        // =======================================================
        // INTEGRASI EXP OLAHRAGA & ANIMASI PROGRESS BAR
        // =======================================================
        val prefOlahraga = OlahragaPreferences.getInstance(requireContext().dataStore)
        val factoryOlahraga = OlahragaViewModelFactory(OlahragaRepository(prefOlahraga))
        val olahragaViewModel = ViewModelProvider(this, factoryOlahraga)[OlahragaViewModel::class.java]

        olahragaViewModel.getTotalExp().observe(viewLifecycleOwner) { totalExp ->
            val MAX_EXP_PER_LEVEL = 100

            // Rumus Level: (Total EXP / 100) + 1 (Agar mulai dari Level 1)
            val levelSekarang = (totalExp / MAX_EXP_PER_LEVEL) + 1

            // Rumus Bar: Sisa EXP untuk menuju level berikutnya
            val progressSaatIni = totalExp % MAX_EXP_PER_LEVEL

            binding.tvLevelAngka.text = levelSekarang.toString()

            // Parameter 'true' di bawah ini yang akan membuat bar biru beranimasi mulus!
            binding.pbExpLevel.setProgressCompat(progressSaatIni, true)
        }
    }

    override fun onResume() {
        super.onResume()
        // Selalu cek badge (titik merah) setiap kali layar Dashboard muncul
        cekBadgeNotifikasi()
    }

    // =======================================================
    // LOGIKA PUSAT NOTIFIKASI & BADGE (TITIK MERAH)
    // =======================================================
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

    // =======================================================
    // FUNGSI KLIK PINDAH HALAMAN KATEGORI
    // =======================================================
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

    // =======================================================
    // LOGIKA TAMPILAN PROFIL KONDISI FISIK
    // =======================================================
    @SuppressLint("SetTextI18n")
    private fun tampilkanDataProfil() {
        val nama = dashboardViewModel.getName() ?: "Sobat Sehatin"
        val fisikUser = dashboardViewModel.getUserBody()
        val kondisiTubuh = dashboardViewModel.getKondisiTubuh()
        val pointUser = dashboardViewModel.getPoint()

        // Menampilkan teks data diri
        binding.dashboardUsername.text = nama
        binding.tvUmurVal.text = "${fisikUser.umur} Tahun"
        binding.tvTinggiVal.text = "${fisikUser.tinggi} cm"
        binding.tvBeratVal.text = "${fisikUser.berat} kg"
        binding.kondisiTubuh.text = kondisiTubuh
        binding.dashboardPoint.text = "$pointUser Point"

        // Logika Ganti Latar Belakang & Karakter sesuai Gender dan BMI
        if (fisikUser.gender == "L") {
            binding.bgIconCharacter.setImageResource(R.drawable.bg_dashboard_character)
            binding.dashProfilePict.setImageResource(R.drawable.profile_boy)

            val imageRes = when (kondisiTubuh) {
                "Kurus" -> R.drawable.character_boy_lebih_kurus
                "Normal" -> R.drawable.character_ideal
                "Gemuk" -> R.drawable.character_boy_gemuk
                "Obesitas" -> R.drawable.character_boy_obesitas
                else -> R.drawable.character_ideal
            }
            binding.characterIllustration.setImageResource(imageRes)
        } else {
            binding.bgIconCharacter.setImageResource(R.drawable.bg_dashboard_girl)
            binding.dashProfilePict.setImageResource(R.drawable.profile_girl)

            val imageRes = when (kondisiTubuh) {
                "Kurus" -> R.drawable.character_girl_lebih_kurus
                "Normal" -> R.drawable.character_girl_ideal
                "Gemuk" -> R.drawable.character_girl_gemuk
                "Obesitas" -> R.drawable.character_girl_obesitas
                else -> R.drawable.character_girl
            }
            binding.characterIllustration.setImageResource(imageRes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}