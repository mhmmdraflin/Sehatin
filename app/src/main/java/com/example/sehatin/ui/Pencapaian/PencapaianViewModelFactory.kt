package com.example.sehatin.ui.Pencapaian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PencapaianViewModelFactory(private val repository: PencapaianRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PencapaianViewModel::class.java)) {
            return PencapaianViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}

