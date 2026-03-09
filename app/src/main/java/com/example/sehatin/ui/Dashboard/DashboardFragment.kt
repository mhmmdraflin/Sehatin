package com.example.sehatin.ui.Dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPref: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPref = UserPreference(requireContext())

        tampilkanDataProfil()

        // Navigasi Menu (Bisa di-uncomment jika Activity sudah siap)
        // binding.btnBmiCard.setOnClickListener { ... }
    }

    @SuppressLint("SetTextI18n")
    private fun tampilkanDataProfil() {
        val nama = userPref.getName() ?: "Sobat Sehatin"
        val fisikUser = userPref.getUserBody()
        val kondisiTubuh = userPref.getKondisiTubuh()

        // [BARU] Ambil jumlah Point dari penyimpanan (Default: 0)
        val pointUser = userPref.getPoint()

        binding.dashboardUsername.text = nama
        binding.tvUmurVal.text = "${fisikUser.umur} Tahun"
        binding.tvTinggiVal.text = "${fisikUser.tinggi} cm"
        binding.tvBeratVal.text = "${fisikUser.berat} kg"
        binding.kondisiTubuh.text = kondisiTubuh

        // [BARU] Tampilkan Point ke Layar
        binding.dashboardPoint.text = "$pointUser Point"

        // =======================================================
        // LOGIKA GANTI BACKGROUND & KARAKTER (GENDER + BMI)
        // =======================================================
        if (fisikUser.gender == "L") {
            // 1. Setting Background & Foto Profil Laki-laki
            binding.bgIconCharacter.setImageResource(R.drawable.bg_dashboard_character)
            binding.dashProfilePict.setImageResource(R.drawable.profile_boy)

            // 2. Setting Karakter Laki-laki berdasarkan BMI
            val imageRes = when (kondisiTubuh) {
                "Kurus" -> R.drawable.character_boy_lebih_kurus
                "Normal" -> R.drawable.character_ideal
                "Gemuk" -> R.drawable.character_boy_gemuk
                "Obesitas" -> R.drawable.character_boy_obesitas
                else -> R.drawable.character_ideal
            }
            binding.characterIllustration.setImageResource(imageRes)

        } else {
            // 1. Setting Background & Foto Profil Perempuan
            binding.bgIconCharacter.setImageResource(R.drawable.bg_dashboard_girl)
            binding.dashProfilePict.setImageResource(R.drawable.profile_girl)

            // 2. Setting Karakter Perempuan berdasarkan BMI
            val imageRes = when (kondisiTubuh) {
                "Kurus" -> R.drawable.character_girl
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