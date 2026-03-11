package com.example.sehatin.ui.SideFeature.JamMakan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class JamMakanViewModelFactory(private val repository: JamMakanRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JamMakanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JamMakanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}