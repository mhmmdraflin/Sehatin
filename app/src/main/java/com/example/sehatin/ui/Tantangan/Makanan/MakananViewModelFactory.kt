package com.example.sehatin.ui.Tantangan.Makanan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MakananViewModelFactory(private val repository: MakananRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MakananViewModel::class.java)) {
            return MakananViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}