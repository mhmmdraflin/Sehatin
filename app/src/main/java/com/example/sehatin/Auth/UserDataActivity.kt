package com.example.sehatin.UserData

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sehatin.Auth.LoginActivity
import com.example.sehatin.Auth.UserDataRepository
import com.example.sehatin.Auth.UserDataViewModel
import com.example.sehatin.Auth.UserDataViewModelFactory
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.databinding.ActivityUserDataBinding

class UserDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDataBinding

    // Variabel untuk menyimpan gender yang dipilih (Default Laki-laki)
    private var selectedGender = "L"

    private val viewModel: UserDataViewModel by viewModels {
        val pref = UserPreference(this)
        val repo = UserDataRepository(pref)
        UserDataViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set tampilan awal kartu gender
        updateGenderUI()

        // Aksi klik kartu Laki-laki
        binding.cardMale.setOnClickListener {
            selectedGender = "L"
            updateGenderUI()
        }

        // Aksi klik kartu Perempuan
        binding.cardFemale.setOnClickListener {
            selectedGender = "P"
            updateGenderUI()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.saveResult.observe(this) { isSuccess ->
            if (isSuccess) {
                val pref = UserPreference(this)
                Log.d("CEK_DATABASE", pref.getAllDataDebug())

                Toast.makeText(this, "Data tersimpan! Silakan Login.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        binding.btnKirim.setOnClickListener {
            val umur = binding.etUmur.text.toString()
            val tinggi = binding.etTinggi.text.toString()
            val berat = binding.etBerat.text.toString()

            if (umur.isEmpty() || tinggi.isEmpty() || berat.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi data ya!", Toast.LENGTH_SHORT).show()
            } else {
                // Kirim data beserta gender yang dipilih user
                viewModel.saveData(umur, tinggi, berat, selectedGender)
            }
        }
    }

    // Fungsi untuk mengubah warna kartu gender saat diklik
    private fun updateGenderUI() {
        if (selectedGender == "L") {
            binding.cardMale.setStrokeColor(Color.parseColor("#33A1E0")) // Biru aktif
            binding.cardMale.strokeWidth = 6
            binding.cardFemale.setStrokeColor(Color.parseColor("#000000")) // Hitam non-aktif
            binding.cardFemale.strokeWidth = 2
        } else {
            binding.cardFemale.setStrokeColor(Color.parseColor("#E91E63")) // Pink aktif
            binding.cardFemale.strokeWidth = 6
            binding.cardMale.setStrokeColor(Color.parseColor("#000000")) // Hitam non-aktif
            binding.cardMale.strokeWidth = 2
        }
    }
}