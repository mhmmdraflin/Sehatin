package com.example.sehatin.Data.Model

import android.content.Context
import androidx.core.content.edit
import com.example.sehatin.Data.Local.UserBody

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
        private const val KEY_GENDER = "user_gender"
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

    // Ambil Data Akun
    fun getName(): String? = pref.getString(KEY_NAME, "Sobat Sehatin") // Default jika kosong
    fun getEmail(): String? = pref.getString(KEY_EMAIL, null)
    fun getPassword(): String? = pref.getString(KEY_PASSWORD, null)

    // Status Login
    fun setLogin(isLogin: Boolean) {
        pref.edit { putBoolean(KEY_IS_LOGIN, isLogin) }
    }

    fun isLogin(): Boolean = pref.getBoolean(KEY_IS_LOGIN, false)

    // --- BAGIAN USER DATA (FISIK) ---

    // 1. Simpan Data Fisik (Update: Menambah Gender)
    // Kita ubah parameternya jadi String satu-satu agar lebih fleksibel dari ViewModel
    fun setUserBody(umur: String, tinggi: String, berat: String, gender: String) {
        pref.edit {
            putString(KEY_UMUR, umur)
            putString(KEY_TINGGI, tinggi)
            putString(KEY_BERAT, berat)
            putString(KEY_GENDER, gender)
        }
    }

    // 2. Ambil Data Fisik (Untuk Beranda / Hitung BMI)
    fun getUserBody(): UserBody {
        return UserBody(
            umur = pref.getString(KEY_UMUR, "0") ?: "0",
            tinggi = pref.getString(KEY_TINGGI, "0") ?: "0",
            berat = pref.getString(KEY_BERAT, "0") ?: "0",
            gender = pref.getString(KEY_GENDER, "L") ?: "L" // Default Laki-laki
        )
    }

    // 3. Cek Kelengkapan Data (GATEKEEPER Login)
    // Fungsi ini dipakai LoginActivity untuk menentukan user ke Beranda atau UserData
    fun isUserDataFilled(): Boolean {
        val umur = pref.getString(KEY_UMUR, null)
        val tinggi = pref.getString(KEY_TINGGI, null)
        val berat = pref.getString(KEY_BERAT, null)

        // Jika salah satu kosong, return false (Belum lengkap)
        return !umur.isNullOrEmpty() && !tinggi.isNullOrEmpty() && !berat.isNullOrEmpty()
    }

    // --- DEBUGGING (LOGCAT) ---
    fun getAllDataDebug(): String {
        val nama = pref.getString(KEY_NAME, "KOSONG")
        val email = pref.getString(KEY_EMAIL, "KOSONG")
        val pass = pref.getString(KEY_PASSWORD, "KOSONG")
        val isLogin = pref.getBoolean(KEY_IS_LOGIN, false)

        val umur = pref.getString(KEY_UMUR, "KOSONG")
        val tinggi = pref.getString(KEY_TINGGI, "KOSONG")
        val berat = pref.getString(KEY_BERAT, "KOSONG")
        val gender = pref.getString(KEY_GENDER, "KOSONG")

        return """
            === CEK DATA USER PREFERENCE ===
            [Status Login]: $isLogin
            
            [Data Akun]
            Nama  : $nama
            Email : $email
            Pass  : $pass
            
            [Data Fisik]
            Gender: $gender
            Umur  : $umur
            Tinggi: $tinggi
            Berat : $berat
            ================================
        """.trimIndent()
    }
}