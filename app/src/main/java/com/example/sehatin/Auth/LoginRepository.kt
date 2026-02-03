package com.example.sehatin.Auth

import com.example.sehatin.Data.Model.UserPreference

class LoginRepository (private val pref: UserPreference) {
    fun checkLogin(email: String, pass: String): Boolean {
        val savedEmail = pref.getEmail()
        val savedPass = pref.getPassword()
        return email == savedEmail && pass == savedPass
    }

    fun setLoginSuccess() {
        pref.setLogin(true)
    }
}