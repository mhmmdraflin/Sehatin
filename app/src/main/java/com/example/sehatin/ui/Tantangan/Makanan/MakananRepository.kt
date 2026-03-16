package com.example.sehatin.ui.Tantangan.Makanan

import kotlinx.coroutines.flow.Flow

class MakananRepository(private val pref: MakananPreferences) {
    fun getCompletedMissions(userKey: String): Flow<List<Int>> = pref.getCompletedMissions(userKey)
    suspend fun simpanMisiSelesai(userKey: String, id: Int) = pref.saveCompletedMission(userKey, id)
}