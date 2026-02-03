package com.example.sehatin.Auth

import androidx.lifecycle.ViewModel

class LoginViewModel (private val repository: LoginRepository) : ViewModel() {
    fun login(email: String, pass: String): Boolean {
        val isSuccess = repository.checkLogin(email, pass)
        if (isSuccess) {
            repository.setLoginSuccess()
        }
        return isSuccess
    }
}