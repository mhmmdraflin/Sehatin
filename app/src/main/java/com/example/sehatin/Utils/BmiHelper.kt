package com.example.sehatin.Utils

object BmiHelper {

    fun calculateBMI(berat: Double, tinggiCm: Double): Double {
        val tinggiM = tinggiCm / 100
        return berat / (tinggiM * tinggiM)
    }

    // Standar Kemenkes RI (PMK No 2 Tahun 2020) - Usia 5-19 Tahun
    fun getStatusGiziRemaja(umur: Int, gender: String, bmi: Double): String {
        // Jika dewasa (>19 tahun), pakai standar umum
        if (umur > 19) {
            return when {
                bmi < 18.5 -> "Kurus"
                bmi < 25.0 -> "Normal"
                bmi < 27.0 -> "Gemuk"
                else -> "Obesitas"
            }
        }

        // Batas ambang sederhana untuk Remaja (Laki/Perempuan)
        val batasNormal = if (gender == "L") getLimitLaki(umur) else getLimitPerempuan(umur)
        val (minNormal, maxNormal) = batasNormal

        return when {
            bmi < minNormal -> "Gizi Kurang"
            bmi <= maxNormal -> "Normal"
            bmi <= (maxNormal + 3.0) -> "Gemuk"
            else -> "Obesitas"
        }
    }

    private fun getLimitLaki(umur: Int): Pair<Double, Double> {
        return when (umur) {
            10 -> 13.7 to 18.5; 11 -> 14.1 to 19.2; 12 -> 14.5 to 19.9
            13 -> 14.9 to 20.8; 14 -> 15.5 to 21.8; 15 -> 16.0 to 22.7
            16 -> 16.5 to 23.5; 17 -> 16.9 to 24.3; 18 -> 17.3 to 24.9
            19 -> 17.6 to 25.4; else -> 18.5 to 25.0
        }
    }

    private fun getLimitPerempuan(umur: Int): Pair<Double, Double> {
        return when (umur) {
            10 -> 13.5 to 19.0; 11 -> 13.9 to 19.9; 12 -> 14.4 to 20.8
            13 -> 14.9 to 21.8; 14 -> 15.4 to 22.7; 15 -> 15.9 to 23.5
            16 -> 16.2 to 24.1; 17 -> 16.4 to 24.5; 18 -> 16.6 to 24.8
            19 -> 16.8 to 25.0; else -> 18.5 to 25.0
        }
    }
}