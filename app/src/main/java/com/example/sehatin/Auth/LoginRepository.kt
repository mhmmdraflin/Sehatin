package com.example.sehatin.Auth

import com.example.sehatin.Data.Model.UserPreference

class LoginRepository(private val pref: UserPreference) {
    fun checkLogin(email: String, pass: String): Boolean {
        return pref.isValidLogin(email, pass) // Cek di loker dinamis
    }

    fun setLoginSuccess(email: String) {
        pref.setKunciAktif(email) // Rekam siapa yang sedang login
    }

    fun isRememberMe(): Boolean = pref.isRememberMe()
    fun getSavedEmail(): String? = pref.getEmail()
    fun getSavedPassword(): String? = pref.getPassword()
    fun setRememberMe(isChecked: Boolean) = pref.setRememberMe(isChecked)
}