package com.example.sehatin.Auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.Main.MainActivity
import com.example.sehatin.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        val pref = UserPreference(this)
        val repo = LoginRepository(pref)
        LoginViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ========================================================
        // FITUR "INGAT SAYA" (100% MVVM Murni)
        // Bertanya pada ViewModel, bukan ke SharedPreferences
        // ========================================================
        if (viewModel.isRememberMe()) {
            binding.kolomNamaLogin.setText(viewModel.getSavedEmail())
            binding.kolomPasswordLogin.setText(viewModel.getSavedPassword())
            binding.checkboxPrivacy.isChecked = true
        }

        // ========================================================
        // 1. OBSERVER UNTUK STATUS LOADING
        // ========================================================
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingOverlay.visibility = View.VISIBLE
            } else {
                binding.loadingOverlay.visibility = View.GONE
            }
        }

        // ========================================================
        // 2. OBSERVER UNTUK HASIL LOGIN
        // ========================================================
        viewModel.loginResult.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Email atau Password Salah!", Toast.LENGTH_SHORT).show()
            }
        }

        // ========================================================
        // 3. AKSI KLIK TOMBOL LOGIN
        // ========================================================
        binding.registerButton.setOnClickListener {
            val email = binding.kolomNamaLogin.text.toString()
            val pass = binding.kolomPasswordLogin.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                // Simpan status checkbox ke ViewModel
                viewModel.saveRememberMeStatus(binding.checkboxPrivacy.isChecked)

                // Lanjut proses login
                viewModel.login(email, pass)
            }
        }

        // ========================================================
        // 4. AKSI KLIK TOMBOL DAFTAR
        // ========================================================
        binding.btnDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}