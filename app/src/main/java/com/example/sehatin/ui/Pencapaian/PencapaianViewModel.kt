package com.example.sehatin.ui.Pencapaian

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PencapaianViewModel(private val repository: PencapaianRepository) : ViewModel() {

    // Data Live yang akan dipantau oleh Fragment
    val pencapaianState: LiveData<PencapaianState> = repository.getSemuaPencapaian().asLiveData()

    // Fungsi umum untuk menambah progres dari bagian aplikasi mana pun
    fun updateProgress(key: Preferences.Key<Int>, value: Int) {
        viewModelScope.launch {
            repository.setProgress(key, value)
        }
    }

    fun getKeys() = repository.keys
}
