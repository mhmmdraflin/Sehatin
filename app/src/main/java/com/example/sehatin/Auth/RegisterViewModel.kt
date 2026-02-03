package com.example.sehatin.Auth

import androidx.lifecycle.ViewModel

class RegisterViewModel (private val repository: RegisterRepository) : ViewModel() {
    fun register(nama: String, email: String, pass: String) {
        repository.registerUser(nama, email, pass)
    }
}