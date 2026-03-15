package com.example.sehatin.Auth

import com.example.sehatin.Data.Model.UserPreference

class RegisterRepository(private val pref: UserPreference) {
    fun registerUser(nama: String, email: String, pass: String) {
        // Buat loker baru
        pref.registerNewAccount(nama, email, pass)
        // Set otomatis jadi akun aktif saat daftar selesai
        pref.setKunciAktif(email)
    }
}