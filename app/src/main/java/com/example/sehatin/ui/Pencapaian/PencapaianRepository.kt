package com.example.sehatin.ui.Pencapaian

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

class PencapaianRepository(private val pref: PencapaianPreferences) {

    fun getSemuaPencapaian(): Flow<PencapaianState> {
        return pref.getPencapaianProgress()
    }

    suspend fun setProgress(key: Preferences.Key<Int>, value: Int) {
        pref.updateProgress(key, value)
    }

    // Kita juga berikan akses ke Keys agar mudah dipanggil
    val keys = pref
}