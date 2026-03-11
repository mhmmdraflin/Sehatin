package com.example.sehatin.Data.Model

import android.content.Context
import androidx.core.content.edit
import com.example.sehatin.Data.Local.UserBody

class UserPreference(context: Context) {

    // 1. Definisikan nama file database-nya di sini sebagai String
    private val name = "SehatinPrefs"

    // 2. Gunakan variabel 'name' tersebut di sini
    private val pref = context.getSharedPreferences(name, Context.MODE_PRIVATE)

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

        private const val KEY_KONDISI_TUBUH = "user_kondisi_tubuh"
        private const val KEY_REMEMBER_ME = "remember_me"

        // Poin & EXP
        private const val KEY_USER_POINT = "user_point"
        private const val KEY_USER_EXP = "user_exp"
    }

    fun setRememberMe(value: Boolean) {
        pref.edit { putBoolean(KEY_REMEMBER_ME, value) }
    }

    fun isRememberMe(): Boolean {
        return pref.getBoolean(KEY_REMEMBER_ME, false)
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

    fun setKondisiTubuh(kondisiTubuh: String) {
        pref.edit { putString(KEY_KONDISI_TUBUH, kondisiTubuh) }
    }

    fun getKondisiTubuh(): String {
        return pref.getString(KEY_KONDISI_TUBUH, "Belum Dihitung") ?: "Belum Dihitung"
    }

    // Cek Status Login
    fun isLogin(): Boolean = pref.getBoolean(KEY_IS_LOGIN, false)

    // --- BAGIAN USER DATA (FISIK) ---

    // 1. Simpan Data Fisik
    fun setUserBody(umur: String, tinggi: String, berat: String, gender: String) {
        pref.edit {
            putString(KEY_UMUR, umur)
            putString(KEY_TINGGI, tinggi)
            putString(KEY_BERAT, berat)
            putString(KEY_GENDER, gender)
        }
    }

    // 2. Ambil Data Fisik
    fun getUserBody(): UserBody {
        return UserBody(
            umur = pref.getString(KEY_UMUR, "0") ?: "0",
            tinggi = pref.getString(KEY_TINGGI, "0") ?: "0",
            berat = pref.getString(KEY_BERAT, "0") ?: "0",
            gender = pref.getString(KEY_GENDER, "L") ?: "L" // Default Laki-laki
        )
    }

    // 3. Cek Kelengkapan Data (GATEKEEPER Login)
    fun isUserDataFilled(): Boolean {
        val umur = pref.getString(KEY_UMUR, null)
        val tinggi = pref.getString(KEY_TINGGI, null)
        val berat = pref.getString(KEY_BERAT, null)

        return !umur.isNullOrEmpty() && !tinggi.isNullOrEmpty() && !berat.isNullOrEmpty()
    }

    // --- LOGIKA POINT (KOIN) & EXP (LEVEL) ---

    fun getPoint(): Int {
        return pref.getInt(KEY_USER_POINT, 0)
    }

    fun tambahPoint(tambahan: Int) {
        val poinSekarang = getPoint()
        pref.edit {
            putInt(KEY_USER_POINT, poinSekarang + tambahan)
        }
    }

    fun getExp(): Int {
        // [PERBAIKAN] Menggunakan variabel 'pref' yang benar
        return pref.getInt(KEY_USER_EXP, 0)
    }

    fun setExp(exp: Int) {
        // [PERBAIKAN] Menggunakan format 'pref.edit {}' agar konsisten
        pref.edit {
            putInt(KEY_USER_EXP, exp)
        }
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

        val point = getPoint()
        val exp = getExp()

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
            
            [Gamifikasi]
            Point : $point
            EXP   : $exp
            ================================
        """.trimIndent()
    }
}