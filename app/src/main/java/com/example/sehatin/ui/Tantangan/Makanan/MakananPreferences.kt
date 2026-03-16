package com.example.sehatin.ui.Tantangan.Makanan

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStoreMakanan: DataStore<Preferences> by preferencesDataStore(name = "makanan_prefs")

class MakananPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    // Kunci unik berdasarkan User Key (Email/Nama)
    private fun getMisiKey(userKey: String) = stringSetPreferencesKey("makanan_selesai_$userKey")

    fun getCompletedMissions(userKey: String): Flow<List<Int>> {
        return dataStore.data.map { preferences ->
            val stringSet = preferences[getMisiKey(userKey)] ?: emptySet()
            stringSet.mapNotNull { it.toIntOrNull() }
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

    companion object {
        @Volatile
        private var INSTANCE: MakananPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): MakananPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = MakananPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}