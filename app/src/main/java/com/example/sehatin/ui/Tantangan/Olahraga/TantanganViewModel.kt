package com.example.sehatin.ui.Tantangan

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TantanganViewModel(private val repository: TantanganRepository) : ViewModel() {

    fun getCompletedMissions(userKey: String): LiveData<List<Int>> = repository.getCompletedMissions(userKey).asLiveData()
    fun getTotalExp(userKey: String): LiveData<Int> = repository.getTotalExp(userKey).asLiveData()
    fun getTotalPoin(userKey: String): LiveData<Int> = repository.getTotalPoin(userKey).asLiveData()

    // Menggunakan CoroutineScope(Dispatchers.IO) agar kebal dari penutupan aplikasi
    fun simpanMisiSelesai(userKey: String, id: Int) {
        CoroutineScope(Dispatchers.IO).launch { repository.simpanMisiSelesai(userKey, id) }
    }

    fun tambahExp(userKey: String, exp: Int) {
        CoroutineScope(Dispatchers.IO).launch { repository.tambahExp(userKey, exp) }
    }

    fun tambahPoin(userKey: String, poin: Int) {
        CoroutineScope(Dispatchers.IO).launch { repository.tambahPoin(userKey, poin) }
    }
}