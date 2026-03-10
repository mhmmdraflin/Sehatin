package com.example.sehatin.ui.Dashboard

import androidx.lifecycle.ViewModel
import com.example.sehatin.Data.Local.UserBody

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    fun getName(): String? {
        return repository.getName()
    }

    fun getUserBody(): UserBody {
        return repository.getUserBody()
    }

    fun getKondisiTubuh(): String {
        return repository.getKondisiTubuh()
    }

    fun getPoint(): Int {
        return repository.getPoint()
    }
}