package com.example.sehatin.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserDataViewModelFactory (private val repository: UserDataRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDataViewModel::class.java)) {
            return UserDataViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}