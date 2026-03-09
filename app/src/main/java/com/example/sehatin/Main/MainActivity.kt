package com.example.sehatin.Main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPref: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.container) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userPref = UserPreference(this)
        val gender = userPref.getUserBody().gender

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.mobile_navigation)

        // =======================================================
        // 1. TENTUKAN HALAMAN AWAL (START DESTINATION)
        // =======================================================
        val startDestId = if (gender == "P") {
            R.id.navigation_dashboard_girl
        } else {
            R.id.navigation_dashboard
        }

        navGraph.setStartDestination(startDestId)
        navController.graph = navGraph

        // =======================================================
        // 2. KUSTOMISASI KLIK NAVBAR BAWAH
        // =======================================================
        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_beranda -> {
                    // Jika user menekan Beranda, kita kembali ke halaman utama yang sudah di-set di atas
                    // popBackStack berfungsi agar halaman tidak menumpuk saat pindah-pindah menu
                    if (navController.currentDestination?.id != startDestId) {
                        navController.popBackStack(startDestId, false)
                    }
                    true
                }
                else -> {
                    // Biarkan menu lain (Profil, Tantangan, dll) diurus otomatis oleh sistem
                    NavigationUI.onNavDestinationSelected(item, navController)
                }
            }
        }

        // =======================================================
        // 3. PASTIKAN IKON BERANDA SELALU MENYALA SAAT DI DASHBOARD
        // =======================================================
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_dashboard || destination.id == R.id.navigation_dashboard_girl) {
                // Paksa ikon beranda (dengan ID navigation_beranda) menyala
                binding.navView.menu.findItem(R.id.navigation_beranda)?.isChecked = true
            }
        }
    }
}