package com.example.sehatin.Data.Model

import android.content.Context
import androidx.core.content.edit

class UserPreference(context: Context) {

    // 1. Definisikan nama file database-nya di sini sebagai String
    private val name = "SehatinPrefs"

    // 2. Gunakan variabel 'name' tersebut di sini
    private val pref = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    // HAPUS fungsi getSharedPreferences kosong yang tadi ada disini

    companion object {
        private const val KEY_EMAIL = "user_email"
        private const val KEY_PASSWORD = "user_password"
        private const val KEY_NAME = "user_name"
        private const val KEY_IS_LOGIN = "is_login"

        // Data Fisik
        private const val KEY_UMUR = "user_umur"
        private const val KEY_TINGGI = "user_tinggi"
        private const val KEY_BERAT = "user_berat"
    }

    // --- BAGIAN AUTH ---
    fun saveAccount(nama: String, email: String, pass: String) {
        pref.edit {
            putString(KEY_NAME, nama)
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, pass)
        }
    }
    // === TAMBAHKAN FUNGSI INI DI PALING BAWAH ===
    // Fungsi ini hanya untuk mengecek apakah data tersimpan atau tidak
    fun getAllDataDebug(): String {
        val nama = pref.getString(KEY_NAME, "KOSONG")
        val email = pref.getString(KEY_EMAIL, "KOSONG")
        val pass = pref.getString(KEY_PASSWORD, "KOSONG")

        val umur = pref.getString(KEY_UMUR, "KOSONG")
        val tinggi = pref.getString(KEY_TINGGI, "KOSONG")
        val berat = pref.getString(KEY_BERAT, "KOSONG")

        return """
            === CEK DATA USER ===
            [Data Akun]
            Nama  : $nama
            Email : $email
            Pass  : $pass
            
            [Data Fisik]
            Umur  : $umur
            Tinggi: $tinggi
            Berat : $berat
            =====================
        """.trimIndent()
    }

    fun getEmail(): String? = pref.getString(KEY_EMAIL, null)
    fun getPassword(): String? = pref.getString(KEY_PASSWORD, null)

    fun setLogin(isLogin: Boolean) {
        pref.edit { putBoolean(KEY_IS_LOGIN, isLogin) }
    }

    fun isLogin(): Boolean = pref.getBoolean(KEY_IS_LOGIN, false)

    fun setUserBody(userBody: com.example.sehatin.Data.Local.UserBody) {
        pref.edit {
            putString(KEY_UMUR, userBody.umur)
            putString(KEY_TINGGI, userBody.tinggi)
            putString(KEY_BERAT, userBody.berat)
        }
    }
}