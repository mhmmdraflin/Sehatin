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
        binding.kolomKonfirmasi.targetPasswordView = binding.kolomPassword

        binding.kolomPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentConfirmText = binding.kolomKonfirmasi.text.toString()
                if (currentConfirmText.isNotEmpty()) {
                    binding.kolomKonfirmasi.text = Editable.Factory.getInstance().newEditable(currentConfirmText)
                    binding.kolomKonfirmasi.setSelection(currentConfirmText.length)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.registerResult.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                // Arahkan ke pengisian data fisik setelah daftar
                val intent = Intent(this, com.example.sehatin.UserData.UserDataActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.registerButton.setOnClickListener {
            val nama = binding.kolomNama.text.toString().trim()
            val email = binding.kolomEmail.text.toString().trim() // Hindari spasi
            val pass = binding.kolomPassword.text.toString()
            val confirmPass = binding.kolomKonfirmasi.text.toString()

            if (nama.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.kolomEmail.error != null || binding.kolomPassword.error != null || binding.kolomKonfirmasi.error != null) {
                Toast.makeText(this, "Perbaiki format yang salah terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cek apakah email sudah dipakai orang lain
            if (userPref.isEmailRegistered(email)) {
                binding.kolomEmail.error = "Email sudah terdaftar!"
                Toast.makeText(this, "Gunakan email lain", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != confirmPass) {
                binding.kolomKonfirmasi.error = "Konfirmasi password tidak sesuai"
                return@setOnClickListener
            }

            viewModel.register(nama, email, pass)
        }
    }
}