package com.example.sehatin.Auth

import com.example.sehatin.Data.Local.UserBody
import com.example.sehatin.Data.Model.UserPreference

class UserDataRepository(private val pref: UserPreference) {
    fun saveUser(user: UserBody) {
        pref.setUserBody(user)
    }
}