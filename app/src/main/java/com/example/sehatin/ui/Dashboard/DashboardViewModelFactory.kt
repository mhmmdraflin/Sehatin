package com.example.sehatin.ui.Dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DashboardViewModelFactory(private val repository: DashboardRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}