package com.example.sehatin.ui.Profil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfilViewModel(private val repository: ProfilRepository) : ViewModel() {

    // Ambil data item yang sedang dipakai & dimiliki (Live Data)
    fun getProfilData() = repository.getProfilData().asLiveData()

    fun saveEquippedBg(bgId: Int) = viewModelScope.launch { repository.saveEquippedBg(bgId) }
    fun saveEquippedChar(charId: Int) = viewModelScope.launch { repository.saveEquippedChar(charId) }

    fun buyBgGym() = viewModelScope.launch { repository.buyBgGym() }
    fun buyBgTaman() = viewModelScope.launch { repository.buyBgTaman() }
}