package com.example.sehatin.ui.SideFeature.Olahraga

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey // Tambahan Import
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat // Tambahan Import
import java.util.Date // Tambahan Import
import java.util.Locale // Tambahan Import

// Inisialisasi DataStore (Taruh di luar class agar menjadi Singleton bawaan)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "olahraga_prefs")

class OlahragaPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    // Kunci (Key) LAMA (Tetap dipertahankan)
    private val KUNCI_GERAKAN_SELESAI = stringSetPreferencesKey("completed_exercises")
    private val KUNCI_TOTAL_KALORI = intPreferencesKey("total_calories")
    private val KUNCI_TOTAL_EXP = intPreferencesKey("total_exp")
    private val KALORI_KEY = intPreferencesKey("total_kalori_harian")
    private val EXP_KEY = intPreferencesKey("total_exp_olahraga")
    private val GERAKAN_SELESAI_KEY = stringSetPreferencesKey("gerakan_selesai_list")

    // =======================================================
    // KUNCI BARU (Khusus Fitur Reset Jam 12 Malam & Akumulasi)
    // =======================================================
    private val KALORI_HARIAN_BARU_KEY = intPreferencesKey("kalori_harian_fix")
    private val KALORI_AKUMULASI_BARU_KEY = intPreferencesKey("kalori_akumulasi_fix")
    private val TANGGAL_TERAKHIR_KEY = stringPreferencesKey("tanggal_terakhir_fix")

    // =======================================================
    // FUNGSI LAMA (Tidak ada yang dirubah/dihapus)
    // =======================================================
    fun getCompletedExercises(): Flow<List<Int>> {
        return dataStore.data.map { preferences ->
            val stringSet = preferences[KUNCI_GERAKAN_SELESAI] ?: emptySet()
            stringSet.mapNotNull { it.toIntOrNull() }
        }
    }

    fun getTotalExp(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[KUNCI_TOTAL_EXP] ?: 0
        }
    }

    suspend fun saveCompletedExercise(id: Int) {
        dataStore.edit { preferences ->
            val currentSet = preferences[KUNCI_GERAKAN_SELESAI] ?: emptySet()
            val newSet = currentSet.toMutableSet()
            newSet.add(id.toString())
            preferences[KUNCI_GERAKAN_SELESAI] = newSet
        }
    }

    suspend fun addKaloriAndExp(kalori: Int, exp: Int) {
        dataStore.edit { preferences ->
            val currentKalori = preferences[KUNCI_TOTAL_KALORI] ?: 0
            val currentExp = preferences[KUNCI_TOTAL_EXP] ?: 0
            preferences[KUNCI_TOTAL_KALORI] = currentKalori + kalori
            preferences[KUNCI_TOTAL_EXP] = currentExp + exp
        }
    }

    fun getTotalKalori(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[KALORI_KEY] ?: 0
        }
    }

    // =======================================================
    // FUNGSI BARU (DITAMBAHKAN UNTUK DASHBOARD GAMIFIKASI)
    // =======================================================
    private fun getTanggalHariIni(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getKaloriHarian(): Flow<Int> {
        return dataStore.data.map { preferences ->
            val tanggalTerakhir = preferences[TANGGAL_TERAKHIR_KEY] ?: ""
            if (tanggalTerakhir != getTanggalHariIni()) 0 else (preferences[KALORI_HARIAN_BARU_KEY] ?: 0)
        }
    }

    fun getKaloriAkumulasi(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[KALORI_AKUMULASI_BARU_KEY] ?: 0
        }
    }

    // Fungsi canggih: Menyimpan data ke kunci lama DAN kunci baru sekaligus
    suspend fun tambahSemuaKaloriDanExp(kaloriTambahan: Int, expTambahan: Int) {
        dataStore.edit { preferences ->
            // 1. Simpan ke sistem lama (Agar fitur lain tidak error)
            val currentKalori = preferences[KUNCI_TOTAL_KALORI] ?: 0
            val currentExp = preferences[KUNCI_TOTAL_EXP] ?: 0
            preferences[KUNCI_TOTAL_KALORI] = currentKalori + kaloriTambahan
            preferences[KUNCI_TOTAL_EXP] = currentExp + expTambahan

            // 2. Simpan ke sistem baru (Harian & Akumulasi)
            val tanggalTerakhir = preferences[TANGGAL_TERAKHIR_KEY] ?: ""
            val tanggalHariIni = getTanggalHariIni()

            val kaloriHarianSaatIni = if (tanggalTerakhir != tanggalHariIni) 0 else (preferences[KALORI_HARIAN_BARU_KEY] ?: 0)
            val kaloriAkumulasiSaatIni = preferences[KALORI_AKUMULASI_BARU_KEY] ?: 0

            preferences[KALORI_HARIAN_BARU_KEY] = kaloriHarianSaatIni + kaloriTambahan
            preferences[KALORI_AKUMULASI_BARU_KEY] = kaloriAkumulasiSaatIni + kaloriTambahan
            preferences[TANGGAL_TERAKHIR_KEY] = tanggalHariIni
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