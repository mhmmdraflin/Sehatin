package com.example.sehatin.ui.Pencapaian

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Inisialisasi DataStore khusus Pencapaian
val Context.dataStorePencapaian: DataStore<Preferences> by preferencesDataStore(name = "pencapaian_prefs")

// Data Class untuk membungkus semua nilai progres
data class PencapaianState(
    val welcome: Int,
    val bmi: Int,
    val makanan: Int,
    val pushup: Int,
    val plank: Int,
    val pengingat: Int,
    val poin: Int,
    val exp: Int
)

class PencapaianPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    // Kunci (Keys) untuk masing-masing lencana
    val WELCOME_KEY = intPreferencesKey("welcome_progress")
    val BMI_KEY = intPreferencesKey("bmi_progress")
    val MAKANAN_KEY = intPreferencesKey("makanan_progress")
    val PUSHUP_KEY = intPreferencesKey("pushup_progress")
    val PLANK_KEY = intPreferencesKey("plank_progress")
    val PENGINGAT_KEY = intPreferencesKey("pengingat_progress")
    val POIN_KEY = intPreferencesKey("poin_progress")
    val EXP_KEY = intPreferencesKey("exp_progress")

    // Mengambil semua data secara real-time (Flow)
    fun getPencapaianProgress(): Flow<PencapaianState> {
        return dataStore.data.map { preferences ->
            PencapaianState(
                welcome = preferences[WELCOME_KEY] ?: 0,
                bmi = preferences[BMI_KEY] ?: 0,
                makanan = preferences[MAKANAN_KEY] ?: 0,
                pushup = preferences[PUSHUP_KEY] ?: 0,
                plank = preferences[PLANK_KEY] ?: 0,
                pengingat = preferences[PENGINGAT_KEY] ?: 0,
                poin = preferences[POIN_KEY] ?: 0,
                exp = preferences[EXP_KEY] ?: 0
            )
        }
    }

    // Fungsi untuk menambah/memperbarui progres
    suspend fun updateProgress(key: Preferences.Key<Int>, newValue: Int) {
        dataStore.edit { preferences ->
            preferences[key] = newValue
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PencapaianPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): PencapaianPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = PencapaianPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}