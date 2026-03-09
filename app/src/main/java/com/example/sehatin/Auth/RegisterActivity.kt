package com.example.sehatin.Auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userPref: UserPreference

    private val viewModel: RegisterViewModel by viewModels {
        val pref = UserPreference(this)
        val repo = RegisterRepository(pref)
        RegisterViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPref = UserPreference(this)

        // =================================================================
        // [KUNCI PERBAIKAN] Hubungkan kolom konfirmasi dengan kolom password utama
        // =================================================================
        binding.kolomKonfirmasi.targetPasswordView = binding.kolomPassword

        // =================================================================
        // [PRO-TIP] Memaksa kolom konfirmasi mengecek ulang setiap kali
        // user merubah isi dari kolom password yang pertama
        // =================================================================
        binding.kolomPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Sengaja mengatur ulang teks konfirmasi agar validasinya terpicu ulang
                val currentConfirmText = binding.kolomKonfirmasi.text.toString()
                if (currentConfirmText.isNotEmpty()) {
                    binding.kolomKonfirmasi.text = Editable.Factory.getInstance().newEditable(currentConfirmText)
                    binding.kolomKonfirmasi.setSelection(currentConfirmText.length) // Kembalikan kursor ke ujung
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

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
        // 2. OBSERVER UNTUK HASIL REGISTRASI
        // ========================================================
        viewModel.registerResult.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, com.example.sehatin.UserData.UserDataActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // ========================================================
        // 3. AKSI KLIK TOMBOL DAFTAR
        // ========================================================
        binding.registerButton.setOnClickListener {
            val nama = binding.kolomNama.text.toString().trim()
            val email = binding.kolomEmail.text.toString().trim()
            val pass = binding.kolomPassword.text.toString()
            val confirmPass = binding.kolomKonfirmasi.text.toString()

            // 1. Cek Data Kosong
            if (nama.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Cek Custom View Error (Jangan lanjut kalau masih ada error pop-up di kolom)
            if (binding.kolomEmail.error != null || binding.kolomPassword.error != null || binding.kolomKonfirmasi.error != null) {
                Toast.makeText(this, "Perbaiki format yang salah terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Cek Apakah Email Sudah Digunakan? (Membandingkan dengan data di lokal)
            val savedEmail = userPref.getEmail()
            if (email.equals(savedEmail, ignoreCase = true)) {
                binding.kolomEmail.error = "Email sudah terdaftar!" // Tampilkan pop-up error
                Toast.makeText(this, "Gunakan email lain", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 4. Cek Konfirmasi Password (Keamanan Ekstra)
            if (pass != confirmPass) {
                binding.kolomKonfirmasi.error = "Konfirmasi password tidak sesuai"
                return@setOnClickListener
            }

            // Jika semua lolos, Daftar!
            viewModel.register(nama, email, pass)
        }
    }
}