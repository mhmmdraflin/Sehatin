package com.example.sehatin.Auth

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R// Pastikan import ini benar jika UserData dipisah

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels {
        val pref = UserPreference(this)
        val repo = RegisterRepository(pref)
        RegisterViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Sesuai ID XML Register Anda
        val etNama = findViewById<EditText>(R.id.kolom_nama)
        val etEmail = findViewById<EditText>(R.id.kolom_Email)
        val etPass = findViewById<EditText>(R.id.kolom_Password)
        val etConfirmPass = findViewById<EditText>(R.id.kolom_Konfirmasi)
        val btnDaftar = findViewById<AppCompatButton>(R.id.register_button)

        btnDaftar.setOnClickListener {
            val nama = etNama.text.toString()
            val email = etEmail.text.toString()
            val pass = etPass.text.toString()
            val confirmPass = etConfirmPass.text.toString()

            if (nama.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
            } else if (pass != confirmPass) {
                Toast.makeText(this, "Password tidak sama!", Toast.LENGTH_SHORT).show()
            } else {
                // Simpan Akun
                viewModel.register(nama, email, pass)

                Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()

                // Pindah ke UserDataActivity
                // Pastikan class UserDataActivity sudah ada
                val intent = Intent(this, com.example.sehatin.UserData.UserDataActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}