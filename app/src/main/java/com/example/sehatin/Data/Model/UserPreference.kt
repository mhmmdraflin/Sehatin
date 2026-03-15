package com.example.sehatin.Data.Model

import android.content.Context
import androidx.core.content.edit
import com.example.sehatin.Data.Local.UserBody

class UserPreference(context: Context) {

    private val name = "SehatinPrefs"
    private val pref = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    companion object {
        // Kunci untuk Sesi Aktif (Siapa yang sedang buka aplikasi saat ini)
        private const val KEY_EMAIL = "user_email"
        private const val KEY_NAME = "user_name"
        private const val KEY_IS_LOGIN = "is_login"
        private const val KEY_REMEMBER_ME = "remember_me"

        // Kunci Dasar Fisik (Nanti akan digabungkan dengan Email agar unik per akun)
        private const val KEY_GENDER = "user_gender_"
        private const val KEY_UMUR = "user_umur_"
        private const val KEY_TINGGI = "user_tinggi_"
        private const val KEY_BERAT = "user_berat_"
        private const val KEY_KONDISI_TUBUH = "user_kondisi_tubuh_"

        // Gamifikasi (Sisa kode lama, sebaiknya gunakan TantanganPreferences yang baru)
        private const val KEY_USER_POINT = "user_point_"
        private const val KEY_USER_EXP = "user_exp_"
    }

    // ==========================================
    // 1. FITUR MULTI-AKUN (LOKER DINAMIS)
    // ==========================================

    // Mendaftarkan akun baru tanpa menghapus akun lama
    fun registerNewAccount(nama: String, email: String, pass: String) {
        pref.edit {
            putString("NAMA_$email", nama)
            putString("PASS_$email", pass)
        }
    }

    // Mengecek apakah email dan password cocok
    fun isValidLogin(email: String, pass: String): Boolean {
        val savedPass = pref.getString("PASS_$email", null)
        return savedPass != null && savedPass == pass
    }

    // Mengecek apakah email sudah dipakai daftar sebelumnya
    fun isEmailRegistered(email: String): Boolean {
        return pref.getString("PASS_$email", null) != null
    }

    // Mengunci sesi aktif saat login berhasil
    fun setKunciAktif(email: String) {
        val nama = pref.getString("NAMA_$email", "Sobat Sehatin")
        pref.edit {
            putString(KEY_EMAIL, email)
            putString(KEY_NAME, nama)
            putBoolean(KEY_IS_LOGIN, true)
        }
    }

    // ==========================================
    // 2. DATA SESI AKTIF (RESEPSIONIS)
    // ==========================================

    fun setRememberMe(value: Boolean) { pref.edit { putBoolean(KEY_REMEMBER_ME, value) } }
    fun isRememberMe(): Boolean = pref.getBoolean(KEY_REMEMBER_ME, false)

    fun getName(): String? = pref.getString(KEY_NAME, "Sobat Sehatin")
    fun getEmail(): String? = pref.getString(KEY_EMAIL, null)

    // Untuk fitur remember me: Ambil password dari email yang sedang aktif
    fun getPassword(): String? {
        val currentEmail = getEmail() ?: return null
        return pref.getString("PASS_$currentEmail", null)
    }

    fun setLogin(isLogin: Boolean) { pref.edit { putBoolean(KEY_IS_LOGIN, isLogin) } }
    fun isLogin(): Boolean = pref.getBoolean(KEY_IS_LOGIN, false)

    // Helper untuk mengambil email aktif sebagai kunci loker
    private fun getActiveEmailKey(): String {
        return getEmail() ?: "guest"
    }

    // ==========================================
    // 3. DATA FISIK & KONDISI TUBUH (UNIK PER AKUN)
    // ==========================================

    fun setKondisiTubuh(kondisiTubuh: String) {
        pref.edit { putString(KEY_KONDISI_TUBUH + getActiveEmailKey(), kondisiTubuh) }
    }

    fun getKondisiTubuh(): String {
        return pref.getString(KEY_KONDISI_TUBUH + getActiveEmailKey(), "Belum Dihitung") ?: "Belum Dihitung"
    }

    fun setUserBody(umur: String, tinggi: String, berat: String, gender: String) {
        pref.edit {
            putString(KEY_UMUR + getActiveEmailKey(), umur)
            putString(KEY_TINGGI + getActiveEmailKey(), tinggi)
            putString(KEY_BERAT + getActiveEmailKey(), berat)
            putString(KEY_GENDER + getActiveEmailKey(), gender)
        }
    }

    fun getUserBody(): UserBody {
        val emailKey = getActiveEmailKey()
        return UserBody(
            umur = pref.getString(KEY_UMUR + emailKey, "0") ?: "0",
            tinggi = pref.getString(KEY_TINGGI + emailKey, "0") ?: "0",
            berat = pref.getString(KEY_BERAT + emailKey, "0") ?: "0",
            gender = pref.getString(KEY_GENDER + emailKey, "L") ?: "L"
        )
    }

    fun isUserDataFilled(): Boolean {
        val emailKey = getActiveEmailKey()
        val umur = pref.getString(KEY_UMUR + emailKey, null)
        val tinggi = pref.getString(KEY_TINGGI + emailKey, null)
        val berat = pref.getString(KEY_BERAT + emailKey, null)

        return !umur.isNullOrEmpty() && !tinggi.isNullOrEmpty() && !berat.isNullOrEmpty()
    }

    // ==========================================
    // 4. POINT & EXP LAMA (Legacy - Dinamis)
    // ==========================================
    fun getPoint(): Int = pref.getInt(KEY_USER_POINT + getActiveEmailKey(), 0)
    fun tambahPoint(tambahan: Int) { pref.edit { putInt(KEY_USER_POINT + getActiveEmailKey(), getPoint() + tambahan) } }

    fun getExp(): Int = pref.getInt(KEY_USER_EXP + getActiveEmailKey(), 0)
    fun setExp(exp: Int) { pref.edit { putInt(KEY_USER_EXP + getActiveEmailKey(), exp) } }

    // ==========================================
    // 5. DEBUGGING (LOGCAT)
    // ==========================================
    fun getAllDataDebug(): String {
        val isLogin = isLogin()
        val email = getEmail() ?: "KOSONG"
        val nama = getName() ?: "KOSONG"
        val userBody = getUserBody()

        return """
            === CEK DATA USER PREFERENCE ===
            [Status Login]: $isLogin
            [Akun Aktif] : $nama ($email)
            [Data Fisik] : Gender=${userBody.gender}, Umur=${userBody.umur}, TB=${userBody.tinggi}, BB=${userBody.berat}
            ================================
        """.trimIndent()
    }
}