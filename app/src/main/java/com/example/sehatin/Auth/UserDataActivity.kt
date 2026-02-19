package com.example.sehatin.UserData

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.sehatin.Auth.LoginActivity
import com.example.sehatin.Auth.UserDataRepository
import com.example.sehatin.Auth.UserDataViewModel
import com.example.sehatin.Auth.UserDataViewModelFactory
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import android.util.Log

class UserDataActivity : AppCompatActivity() {

    private val viewModel: UserDataViewModel by viewModels {
        val pref = UserPreference(this)
        val repo = UserDataRepository(pref)
        UserDataViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)

        // ID ini sudah BENAR dan SESUAI dengan XML Anda
        val etUmur = findViewById<EditText>(R.id.et_umur)
        val etTinggi = findViewById<EditText>(R.id.et_tinggi)
        val etBerat = findViewById<EditText>(R.id.et_berat)
        val btnKirim = findViewById<AppCompatButton>(R.id.btn_kirim)

        btnKirim.setOnClickListener {
            val umur = etUmur.text.toString()
            val tinggi = etTinggi.text.toString()
            val berat = etBerat.text.toString()
            val gender = "L" // Default value for now, consider adding gender selection in UI later

            if (umur.isEmpty() || tinggi.isEmpty() || berat.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi data ya!", Toast.LENGTH_SHORT).show()
            } else {
                // 1. Simpan Data Fisik
                viewModel.saveData(umur, tinggi, berat, gender)

                // 2. [BARU] CEK DATA DI LOGCAT
                // Kita panggil fungsi getAllDataDebug() dari UserPreference
                val pref = UserPreference(this)
                val dataLengkap = pref.getAllDataDebug()

                // Tampilkan di Logcat dengan tag "CEK_DATABASE"
                Log.d("CEK_DATABASE", dataLengkap)

                Toast.makeText(this, "Data tersimpan! Silakan Login.", Toast.LENGTH_SHORT).show()

                // 3. Pindah ke Login
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}
