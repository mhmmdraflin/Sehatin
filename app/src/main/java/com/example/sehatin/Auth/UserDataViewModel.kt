package com.example.sehatin.Auth

import androidx.lifecycle.ViewModel
import com.example.sehatin.Data.Local.UserBody

class UserDataViewModel(private val repository: UserDataRepository) : ViewModel() {
    fun saveData(umur: String, tinggi: String, berat: String, gender: String) {
        val user = UserBody(umur, tinggi, berat, gender)
        repository.saveUser(user)
    }
}
