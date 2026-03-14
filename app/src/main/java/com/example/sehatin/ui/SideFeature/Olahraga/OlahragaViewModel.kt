package com.example.sehatin.ui.SideFeature.Olahraga

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OlahragaViewModel(private val repository: OlahragaRepository) : ViewModel() {

    // Mengambil data secara realtime (asLiveData membuat Activity otomatis merespon perubahan)
    fun getCompletedGerakanIds(): LiveData<List<Int>> {
        return repository.getCompletedGerakanIds().asLiveData()
    }

    // Fungsi menyimpan gerakan
    fun simpanGerakanSelesai(id: Int) {
        viewModelScope.launch {
            repository.simpanGerakanSelesai(id)
        }
    }

    // Fungsi menambah statistik user
    fun tambahKaloriDanExp(kalori: Int, exp: Int) {
        viewModelScope.launch {
            repository.tambahKaloriDanExp(kalori, exp)
        }
    }

    fun getTotalExp(): LiveData<Int> {
        return repository.getTotalExp().asLiveData()
    }
}