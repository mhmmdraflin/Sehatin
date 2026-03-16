package com.example.sehatin.ui.Tantangan.Makanan

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MakananViewModel(private val repository: MakananRepository) : ViewModel() {

    fun getCompletedMissions(userKey: String): LiveData<List<Int>> {
        return repository.getCompletedMissions(userKey).asLiveData()
    }

    fun simpanMisiSelesai(userKey: String, id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.simpanMisiSelesai(userKey, id)
        }
    }
}