package com.example.sehatin.Auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatin.Data.Local.UserBody
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserDataViewModel(private val repository: UserDataRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    fun saveData(umur: String, tinggi: String, berat: String, gender: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500) // Animasi loading sesaat

            // =======================================================
            // 1. DI SINILAH KALKULASI BMI TERJADI SEBELUM PINDAH HALAMAN
            // =======================================================
            val kondisiTubuh = hitungBMI(tinggi, berat, gender)

            // 2. Simpan Data Fisik (Umur, Tinggi, Berat, Gender)
            val user = UserBody(umur, tinggi, berat, gender)
            repository.saveUser(user)

            // 3. Simpan Hasil Kalkulasi Kondisi Tubuh (Kurus/Normal/dll)
            repository.saveKondisiTubuh(kondisiTubuh)

            _isLoading.value = false
            _saveResult.value = true
        }
    }

    // =======================================================
    // RUMUS BMI & PENENTUAN KONDISI TUBUH (SESUAI GAMBAR TABEL)
    // =======================================================
    private fun hitungBMI(tinggiStr: String, beratStr: String, gender: String): String {
        return try {
            val tinggiCm = tinggiStr.toFloat()
            val beratKg = beratStr.toFloat()

            if (tinggiCm > 0f && beratKg > 0f) {
                // Konversi Centimeter ke Meter
                val tinggiMeter = tinggiCm / 100
                // Rumus asli BMI: Berat (kg) dibagi Tinggi (m) kuadrat
                val bmi = beratKg / (tinggiMeter * tinggiMeter)

                // Logika pembeda BMI (Berdasarkan Tabel Anda)
                if (gender == "L") {
                    when {
                        bmi < 17.0 -> "Kurus"
                        bmi >= 17.0 && bmi <= 23.0 -> "Normal"
                        bmi > 23.0 && bmi <= 27.0 -> "Gemuk"
                        else -> "Obesitas"
                    }
                } else {
                    when {
                        bmi < 18.0 -> "Kurus"
                        bmi >= 18.0 && bmi <= 25.0 -> "Normal"
                        bmi > 25.0 && bmi <= 27.0 -> "Gemuk"
                        else -> "Obesitas"
                    }
                }
            } else {
                "Data Tidak Valid"
            }
        } catch (e: NumberFormatException) {
            "Gagal Hitung"
        }
    }
}