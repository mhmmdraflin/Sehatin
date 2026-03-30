package com.example.sehatin.ui.SideFeature.Olahraga

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OlahragaViewModel(private val repository: OlahragaRepository) : ViewModel() {

    // FUNGSI LAMA
    fun getCompletedGerakanIds(): LiveData<List<Int>> {
        return repository.getCompletedGerakanIds().asLiveData()
    }

    fun simpanGerakanSelesai(id: Int) {
        viewModelScope.launch {
            repository.simpanGerakanSelesai(id)
        }
    }

    fun tambahKaloriDanExp(kalori: Int, exp: Int) {
        viewModelScope.launch {
            repository.tambahKaloriDanExp(kalori, exp)
        }
    }

    fun getTotalExp(): LiveData<Int> {
        return repository.getTotalExp().asLiveData()
    }
    fun getTotalKalori(): LiveData<Int> {
        return repository.getTotalKalori().asLiveData()
    }

    // FUNGSI BARU (DITAMBAHKAN)
    fun getKaloriHarian(): LiveData<Int> {
        return repository.getKaloriHarian().asLiveData()
    }

    fun getKaloriAkumulasi(): LiveData<Int> {
        return repository.getKaloriAkumulasi().asLiveData()
    }
}