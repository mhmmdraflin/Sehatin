package com.example.sehatin.ui.Profil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProfilViewModelFactory(private val repository: ProfilRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfilViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfilViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}