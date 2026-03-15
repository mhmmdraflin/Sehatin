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

        // Fitur Ingat Saya
        if (viewModel.isRememberMe()) {
            binding.kolomNamaLogin.setText(viewModel.getSavedEmail())
            binding.kolomPasswordLogin.setText(viewModel.getSavedPassword())
            binding.checkboxPrivacy.isChecked = true
        }

        // Observer Loading
        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observer Hasil Login
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

        // Tombol Login
        binding.registerButton.setOnClickListener {
            val email = binding.kolomNamaLogin.text.toString().trim() // Pakai trim agar aman dari spasi
            val pass = binding.kolomPasswordLogin.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveRememberMeStatus(binding.checkboxPrivacy.isChecked)
                viewModel.login(email, pass)
            }
        }

        // Tombol Daftar
        binding.btnDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}