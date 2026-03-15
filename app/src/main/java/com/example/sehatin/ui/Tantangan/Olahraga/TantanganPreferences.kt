package com.example.sehatin.ui.Tantangan

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStoreTantangan: DataStore<Preferences> by preferencesDataStore(name = "tantangan_prefs")

class TantanganPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    // ========================
    // MEMBUAT KUNCI UNIK BERDASARKAN AKUN
    // ========================
    private fun getMisiKey(userKey: String) = stringSetPreferencesKey("completed_missions_$userKey")
    private fun getExpKey(userKey: String) = intPreferencesKey("total_exp_$userKey")
    private fun getPoinKey(userKey: String) = intPreferencesKey("total_poin_$userKey")

    fun getCompletedMissions(userKey: String): Flow<List<Int>> {
        return dataStore.data.map { preferences ->
            val stringSet = preferences[getMisiKey(userKey)] ?: emptySet()
            stringSet.mapNotNull { it.toIntOrNull() }
        }
    }

    fun getTotalExp(userKey: String): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[getExpKey(userKey)] ?: 0
        }
    }

    fun getTotalPoin(userKey: String): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[getPoinKey(userKey)] ?: 0
        }
    }

    suspend fun saveCompletedMission(userKey: String, id: Int) {
        dataStore.edit { preferences ->
            val currentSet = preferences[getMisiKey(userKey)] ?: emptySet()
            val newSet = currentSet.toMutableSet()
            newSet.add(id.toString())
            preferences[getMisiKey(userKey)] = newSet
        }
    }

    suspend fun addExp(userKey: String, exp: Int) {
        dataStore.edit { preferences ->
            val currentExp = preferences[getExpKey(userKey)] ?: 0
            preferences[getExpKey(userKey)] = currentExp + exp
        }
    }

    suspend fun addPoin(userKey: String, poin: Int) {
        dataStore.edit { preferences ->
            val currentPoin = preferences[getPoinKey(userKey)] ?: 0
            preferences[getPoinKey(userKey)] = currentPoin + poin
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TantanganPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): TantanganPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = TantanganPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}