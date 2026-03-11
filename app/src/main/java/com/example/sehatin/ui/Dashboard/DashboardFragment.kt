package com.example.sehatin.ui.Dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.databinding.FragmentDashboardBinding
import com.example.sehatin.ui.SideFeature.BodyMassIndexActivity
import com.example.sehatin.ui.SideFeature.Informasi_MakananActivity
import com.example.sehatin.ui.SideFeature.JamMakan.Pengingat_Jam_MakanActivity
import com.example.sehatin.ui.SideFeature.Olahraga.OlahragaActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // =======================================================
    // INISIALISASI VIEWMODEL
    // =======================================================
    private val viewModel: DashboardViewModel by viewModels {
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
    }

    // =======================================================
    // FUNGSI KLIK PINDAH HALAMAN (INTENT)
    // =======================================================
    private fun setupTombolKategori() {
        // 1. Tombol Body Mass Index (BMI)
        binding.btnBmiCard.setOnClickListener {
            val intent = Intent(requireContext(), BodyMassIndexActivity::class.java)
            startActivity(intent)
        }

        // 2. Tombol Pengingat Jam Makan
        binding.btnPengingatMakanCard.setOnClickListener {
            val intent = Intent(requireContext(), Pengingat_Jam_MakanActivity::class.java)
            startActivity(intent)
        }

        // 3. Tombol Informasi Makanan
        binding.btnInfoMakananCard.setOnClickListener {
            val intent = Intent(requireContext(), Informasi_MakananActivity::class.java)
            startActivity(intent)
        }

        // 4. Tombol Olahraga
        binding.btnOlahragaCard.setOnClickListener {
            val intent = Intent(requireContext(), OlahragaActivity::class.java)
            startActivity(intent)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun tampilkanDataProfil() {
        val nama = viewModel.getName() ?: "Sobat Sehatin"
        val fisikUser = viewModel.getUserBody()
        val kondisiTubuh = viewModel.getKondisiTubuh()

        //  Point dan EXP
        val pointUser = viewModel.getPoint()
        val expUser = viewModel.getExp()


        // Menampilkan teks ke XML
        binding.dashboardUsername.text = nama
        binding.tvUmurVal.text = "${fisikUser.umur} Tahun"
        binding.tvTinggiVal.text = "${fisikUser.tinggi} cm"
        binding.tvBeratVal.text = "${fisikUser.berat} kg"
        binding.kondisiTubuh.text = kondisiTubuh
        binding.dashboardPoint.text = "$pointUser Point"

        // =======================================================
        // LOGIKA LEVEL & EXP (PROGRESS BAR BERGERAK)
        // Asumsi: Setiap 100 Point = Naik 1 Level
        // =======================================================
        val currentLevel = expUser / 100 // Contoh: EXP 150 / 100 = Level 1
        val currentProgress = expUser % 100 // Sisa bagi: EXP 150 % 100 = Progress 50 (50%)

        // Menampilkan angka level (0, 1, 2, dst)
        binding.tvLevelAngka.text = currentLevel.toString()

        // Membuat garis biru (Progress Bar) bergerak mengikuti EXP
        binding.pbExpLevel.setProgressCompat(currentProgress, true)

        // =======================================================
        // LOGIKA GANTI BACKGROUND & KARAKTER (GENDER + BMI)
        // =======================================================
        if (fisikUser.gender == "L") {
            // Dashboard untuk Laki-laki
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
            // Dashboard untuk Perempuan
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
        _binding = null // Mencegah memory leak
    }
}