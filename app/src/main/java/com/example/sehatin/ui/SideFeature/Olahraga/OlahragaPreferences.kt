package com.example.sehatin.ui.SideFeature.Olahraga

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Inisialisasi DataStore (Taruh di luar class agar menjadi Singleton bawaan)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "olahraga_prefs")

class OlahragaPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    // Kunci (Key) untuk menyimpan data
    private val KUNCI_GERAKAN_SELESAI = stringSetPreferencesKey("completed_exercises")
    private val KUNCI_TOTAL_KALORI = intPreferencesKey("total_calories")
    private val KUNCI_TOTAL_EXP = intPreferencesKey("total_exp")

    // 1. Membaca daftar ID yang sudah selesai
    fun getCompletedExercises(): Flow<List<Int>> {
        return dataStore.data.map { preferences ->
            val stringSet = preferences[KUNCI_GERAKAN_SELESAI] ?: emptySet()
            stringSet.mapNotNull { it.toIntOrNull() } // Ubah dari String Set kembali ke List of Int
        }
    }

    // Membaca Total EXP
    fun getTotalExp(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[KUNCI_TOTAL_EXP] ?: 0
        }
    }

    // 2. Menyimpan ID gerakan yang baru diselesaikan
    suspend fun saveCompletedExercise(id: Int) {
        dataStore.edit { preferences ->
            val currentSet = preferences[KUNCI_GERAKAN_SELESAI] ?: emptySet()
            val newSet = currentSet.toMutableSet()
            newSet.add(id.toString())
            preferences[KUNCI_GERAKAN_SELESAI] = newSet
        }
    }

    // 3. Menambahkan Kalori dan EXP
    suspend fun addKaloriAndExp(kalori: Int, exp: Int) {
        dataStore.edit { preferences ->
            val currentKalori = preferences[KUNCI_TOTAL_KALORI] ?: 0
            val currentExp = preferences[KUNCI_TOTAL_EXP] ?: 0
            preferences[KUNCI_TOTAL_KALORI] = currentKalori + kalori
            preferences[KUNCI_TOTAL_EXP] = currentExp + exp
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: OlahragaPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): OlahragaPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = OlahragaPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}