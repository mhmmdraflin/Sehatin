package com.example.sehatin.Utils // Sesuaikan package-nya jika perlu, letakkan di dalam folder Utils atau semacamnya

import com.example.sehatin.R

object CharacterImageUtils {

    // ====================================================
    // FUNGSI UTAMA: PEMANGGIL KARAKTER (SKIN)
    // ====================================================
    fun getCharacterImageRes(gender: String, kondisi: String, skinId: Int): Int {
        return if (gender == "L") {
            // JIKA LAKI-LAKI
            when (skinId) {
                1 -> getMaleBasic(kondisi)
                2 -> getMaleElite(kondisi)
                3 -> getMaleSpecial(kondisi)
                else -> getMaleBasic(kondisi) // Default ke Basic
            }
        } else {
            // JIKA PEREMPUAN
            when (skinId) {
                1 -> getFemaleBasic(kondisi)
                2 -> getFemaleElite(kondisi)
                3 -> getFemaleSpecial(kondisi)
                else -> getFemaleBasic(kondisi) // Default ke Basic
            }
        }
    }

    // ====================================================
    // FUNGSI BARU: PEMANGGIL LATAR BELAKANG (BACKGROUND)
    // ====================================================
    fun getBackgroundImageRes(gender: String, backgroundId: Int): Int {
        return if (gender == "L") {
            // Background Laki-laki
            when (backgroundId) {
                2 -> R.drawable.background_laki_perempuan_skin_elite
                3 -> R.drawable.background_lakilaki_skin_special
                else -> R.drawable.bg_dashboard_character // Default Basic Laki-laki
            }
        } else {
            // Background Perempuan
            when (backgroundId) {
                2 -> R.drawable.background_perempuan_skin_elite
                3 -> R.drawable.background_perempuan_skin_special
                else -> R.drawable.bg_dashboard_girl // Default Basic Perempuan
            }
        }
    }

    // ====================================================
    // 1. SKIN BASIC (Bawaan Aplikasi)
    // ====================================================
    private fun getMaleBasic(kondisi: String): Int = when (kondisi) {
        "Kurus" -> R.drawable.character_boy_lebih_kurus
        "Normal (Ideal)" -> R.drawable.character_ideal
        "Gemuk" -> R.drawable.character_boy_gemuk
        "Obesitas" -> R.drawable.character_boy_obesitas
        else -> R.drawable.character_ideal
    }

    private fun getFemaleBasic(kondisi: String): Int = when (kondisi) {
        "Kurus" -> R.drawable.character_girl_lebih_kurus
        "Normal (Ideal)" -> R.drawable.character_girl_ideal
        "Gemuk" -> R.drawable.character_girl_gemuk
        "Obesitas" -> R.drawable.character_girl_obesitas
        else -> R.drawable.character_girl
    }

    // ====================================================
    // 2. SKIN ELITE (ID = 2)
    // ====================================================
    private fun getMaleElite(kondisi: String): Int = when (kondisi) {
        "Kurus" -> R.drawable.boy_skin_elite_kurus
        "Normal (Ideal)" -> R.drawable.boy_skin_elite_ideal
        "Gemuk" -> R.drawable.boy_skin_elite_gemuk
        "Obesitas" -> R.drawable.boy_skin_elite_obesitas
        else -> R.drawable.boy_skin_elite_ideal
    }

    private fun getFemaleElite(kondisi: String): Int = when (kondisi) {
        "Kurus" -> R.drawable.girl_skin_elite_kurus
        "Normal (Ideal)" -> R.drawable.girl_skin_elite_ideal
        "Gemuk" -> R.drawable.girl_skin_elite_gemuk
        "Obesitas" -> R.drawable.girl_skin_elite_obesitas
        else -> R.drawable.girl_skin_elite_ideal
    }

    // ====================================================
    // 3. SKIN SPECIAL (ID = 3)
    // ====================================================
    private fun getMaleSpecial(kondisi: String): Int = when (kondisi) {
        "Kurus" -> R.drawable.boy_skin_special_kurus
        "Normal (Ideal)" -> R.drawable.boy_skin_special_ideal
        "Gemuk" -> R.drawable.boy_skin_special_gemuk
        "Obesitas" -> R.drawable.boy_skin_special_obesitas
        else -> R.drawable.boy_skin_special_ideal
    }

    private fun getFemaleSpecial(kondisi: String): Int = when (kondisi) {
        "Kurus" -> R.drawable.girl_skin_special_kurus
        "Normal (Ideal)" -> R.drawable.girl_skin_special_ideal
        "Gemuk" -> R.drawable.girl_skin_special_gemuk
        "Obesitas" -> R.drawable.girl_skin_special_obesitas
        else -> R.drawable.girl_skin_special_ideal
    }
}