package com.example.sehatin.ui.SideFeature.Olahraga

import kotlinx.coroutines.flow.Flow

class OlahragaRepository(private val pref: OlahragaPreferences) {

    // FUNGSI LAMA
    fun getCompletedGerakanIds(): Flow<List<Int>> {
        return pref.getCompletedExercises()
    }

    suspend fun simpanGerakanSelesai(id: Int) {
        pref.saveCompletedExercise(id)
    }

    // Modifikasi kecil: Kita panggil fungsi yang menyimpan ke kunci lama & baru sekaligus
    suspend fun tambahKaloriDanExp(kalori: Int, exp: Int) {
        pref.tambahSemuaKaloriDanExp(kalori, exp)
    }

    fun getTotalExp(): Flow<Int> {
        return pref.getTotalExp()
    }
    fun getTotalKalori(): Flow<Int> {
        return pref.getTotalKalori()
    }

    // FUNGSI BARU (DITAMBAHKAN)
    fun getKaloriHarian(): Flow<Int> {
        return pref.getKaloriHarian()
    }

    fun getKaloriAkumulasi(): Flow<Int> {
        return pref.getKaloriAkumulasi()
    }
}