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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.example.sehatin.ui.Tantangan.TantanganPreferences
import com.example.sehatin.ui.Tantangan.TantanganRepository
import com.example.sehatin.ui.Tantangan.TantanganViewModel
import com.example.sehatin.ui.Tantangan.TantanganViewModelFactory
import com.example.sehatin.ui.Tantangan.dataStoreTantangan

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

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

        tampilkanDataProfil()
        setupTombolKategori()
        binding.dashboardEditprof.setOnClickListener { bukaRiwayatNotifikasi() }

        // ==========================================
        // TENTUKAN IDENTITAS USER AKTIF (Menggunakan getName)
        // ==========================================
        val userPref = UserPreference(requireContext())
        val userKey = userPref.getName() ?: "guest_user"

        // ==========================================
        // INTEGRASI EXP & POIN BERDASARKAN AKUN
        // ==========================================
        val prefTantangan = TantanganPreferences.getInstance(requireContext().dataStoreTantangan)
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

    @SuppressLint("SetTextI18n")
    private fun tampilkanDataProfil() {
        val nama = dashboardViewModel.getName() ?: "Sobat Sehatin"
        val fisikUser = dashboardViewModel.getUserBody()
        val kondisiTubuh = dashboardViewModel.getKondisiTubuh()

        binding.dashboardUsername.text = nama
        binding.tvUmurVal.text = "${fisikUser.umur} Tahun"
        binding.tvTinggiVal.text = "${fisikUser.tinggi} cm"
        binding.tvBeratVal.text = "${fisikUser.berat} kg"
        binding.kondisiTubuh.text = kondisiTubuh

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