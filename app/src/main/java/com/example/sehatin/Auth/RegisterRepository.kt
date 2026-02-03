package com.example.sehatin.Auth

import com.example.sehatin.Data.Model.UserPreference


class RegisterRepository(private val pref: UserPreference) {
    fun registerUser(nama: String, email: String, pass: String) {
        pref.saveAccount(nama, email, pass)
    }
}
