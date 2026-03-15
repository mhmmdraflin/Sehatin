package com.example.sehatin.ui.Tantangan

import kotlinx.coroutines.flow.Flow

class TantanganRepository(private val pref: TantanganPreferences) {
    fun getCompletedMissions(userKey: String): Flow<List<Int>> = pref.getCompletedMissions(userKey)
    fun getTotalExp(userKey: String): Flow<Int> = pref.getTotalExp(userKey)
    fun getTotalPoin(userKey: String): Flow<Int> = pref.getTotalPoin(userKey)

    suspend fun simpanMisiSelesai(userKey: String, id: Int) = pref.saveCompletedMission(userKey, id)
    suspend fun tambahExp(userKey: String, exp: Int) = pref.addExp(userKey, exp)
    suspend fun tambahPoin(userKey: String, poin: Int) = pref.addPoin(userKey, poin)
}