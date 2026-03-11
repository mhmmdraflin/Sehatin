package com.example.sehatin.ui.SideFeature.JamMakan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class JamMakanViewModel(private val repository: JamMakanRepository) : ViewModel() {

    // LiveData agar Activity bisa 'mengamati' perubahan data secara otomatis
    private val _alarms = MutableLiveData<List<JamMakanModel>>()
    val alarms: LiveData<List<JamMakanModel>> get() = _alarms

    private var currentList = mutableListOf<JamMakanModel>()

    init {
        loadAlarms()
    }

    private fun loadAlarms() {
        currentList = repository.getAlarms()
        currentList.sortBy { it.jam * 60 + it.menit } // Urutkan dari pagi ke malam
        _alarms.value = currentList
    }

    fun addAlarm(alarm: JamMakanModel) {
        currentList.add(alarm)
        saveAndPost()
    }

    fun updateAlarm(oldAlarm: JamMakanModel, newAlarm: JamMakanModel) {
        val index = currentList.indexOfFirst { it.id == oldAlarm.id }
        if (index != -1) {
            currentList[index] = newAlarm
            saveAndPost()
        }
    }

    fun deleteAlarm(alarm: JamMakanModel) {
        currentList.removeAll { it.id == alarm.id }
        saveAndPost()
    }

    fun toggleAlarmStatus(alarm: JamMakanModel, isActive: Boolean) {
        val index = currentList.indexOfFirst { it.id == alarm.id }
        if (index != -1) {
            currentList[index].isActive = isActive
            saveAndPost()
        }
    }

    private fun saveAndPost() {
        currentList.sortBy { it.jam * 60 + it.menit }
        repository.saveAlarms(currentList)
        _alarms.value = currentList
    }
}