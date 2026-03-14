package com.example.sehatin.ui.SideFeature.Olahraga

import kotlinx.coroutines.flow.Flow

class OlahragaRepository(private val pref: OlahragaPreferences) {

    fun getCompletedGerakanIds(): Flow<List<Int>> {
        return pref.getCompletedExercises()
    }

    suspend fun simpanGerakanSelesai(id: Int) {
        pref.saveCompletedExercise(id)
    }

    suspend fun tambahKaloriDanExp(kalori: Int, exp: Int) {
        pref.addKaloriAndExp(kalori, exp)
    }

    fun getTotalExp(): Flow<Int> {
        return pref.getTotalExp()
    }
}