package com.example.sehatin.ui.Tantangan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TantanganViewModelFactory(private val repository: TantanganRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TantanganViewModel::class.java)) {
            return TantanganViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}