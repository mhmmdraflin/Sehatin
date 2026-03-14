package com.example.sehatin.ui.SideFeature.Olahraga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OlahragaViewModelFactory(private val repository: OlahragaRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OlahragaViewModel::class.java)) {
            return OlahragaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}