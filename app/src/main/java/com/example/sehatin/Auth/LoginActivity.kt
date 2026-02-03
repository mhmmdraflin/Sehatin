package com.example.sehatin.Auth

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.Main.MainActivity
import com.example.sehatin.R

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels {
        val pref = UserPreference(this)
        val repo = LoginRepository(pref)
        LoginViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.kolom_nama_login)
        val etPass = findViewById<EditText>(R.id.kolom_password_login)
        val btnLogin = findViewById<AppCompatButton>(R.id.register_button) // ID di XML Login
        val btnKeDaftar = findViewById<TextView>(R.id.btn_daftar)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPass.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                val success = viewModel.login(email, pass)
                if (success) {
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                    // Pindah ke MainActivity (Home)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    // PERBAIKAN DI SINI: Ganti LENGTH_ERROR menjadi LENGTH_SHORT
                    Toast.makeText(this, "Email atau Password Salah!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnKeDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}