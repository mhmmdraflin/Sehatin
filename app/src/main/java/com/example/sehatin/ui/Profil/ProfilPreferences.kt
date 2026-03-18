package com.example.sehatin.ui.Profil

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// 1. Inisialisasi DataStore untuk Profil & Inventaris
val Context.dataStoreProfil: DataStore<Preferences> by preferencesDataStore(name = "profil_preferences")

// 2. Data Class untuk menampung status inventaris user
data class ProfilData(
    val backgroundId: Int, // 1 = Default, 2 = Gym, 3 = Taman
    val characterId: Int,  // 1 = Ideal, 2 = Push Up
    val hasBgGym: Boolean, // True jika sudah dibeli
    val hasBgTaman: Boolean // True jika sudah dibeli
)

// 3. PREFERENCES (Database Lokal)
class ProfilPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val EQUIPPED_BG_KEY = intPreferencesKey("equipped_bg")
    private val EQUIPPED_CHAR_KEY = intPreferencesKey("equipped_char")

    // Kunci untuk barang yang sudah dibeli
    private val HAS_BG_GYM = booleanPreferencesKey("has_bg_gym")
    private val HAS_BG_TAMAN = booleanPreferencesKey("has_bg_taman")

    fun getProfilData(): Flow<ProfilData> {
        return dataStore.data.map { preferences ->
            ProfilData(
                backgroundId = preferences[EQUIPPED_BG_KEY] ?: 1,
                characterId = preferences[EQUIPPED_CHAR_KEY] ?: 1,
                hasBgGym = preferences[HAS_BG_GYM] ?: false,
                hasBgTaman = preferences[HAS_BG_TAMAN] ?: false
            )
        }
    }

    // Menyimpan Item yang sedang dipakai (Equipped)
    suspend fun saveEquippedBg(bgId: Int) {
        dataStore.edit { it[EQUIPPED_BG_KEY] = bgId }
    }

    suspend fun saveEquippedChar(charId: Int) {
        dataStore.edit { it[EQUIPPED_CHAR_KEY] = charId }
    }

    // Menyimpan status pembelian dari Toko (Tukar Poin)
    suspend fun buyBgGym() {
        dataStore.edit { it[HAS_BG_GYM] = true }
    }

    suspend fun buyBgTaman() {
        dataStore.edit { it[HAS_BG_TAMAN] = true }
    }

    companion object {
        @Volatile
        private var INSTANCE: ProfilPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): ProfilPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = ProfilPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}