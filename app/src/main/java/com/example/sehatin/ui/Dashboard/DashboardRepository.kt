package com.example.sehatin.ui.Dashboard

import com.example.sehatin.Data.Local.UserBody
import com.example.sehatin.Data.Model.UserPreference

class DashboardRepository(private val pref: UserPreference) {

    fun getName(): String? {
        return pref.getName() // Mengambil nama dari UserPreference
    }

    fun getUserBody(): UserBody {
        return pref.getUserBody() // Mengambil data fisik (umur, tinggi, berat)
    }

    fun getKondisiTubuh(): String {
        return pref.getKondisiTubuh() // Mengambil status BMI (Kurus/Ideal/Gemuk)
    }

    fun getPoint(): Int {
        return pref.getPoint() // Mengambil point
    }

    fun getExp(): Int {
        return pref.getExp() // Mengambil exp
    }

    // =======================================================
    // FUNGSI BARU: MENERUSKAN UPDATE BERAT BADAN KE DATABASE
    // =======================================================
    fun updateBeratBadan(beratBaru: String) {
        // Meneruskan perintah penyimpanan ke UserPreference
        pref.updateBeratBadan(beratBaru)
    }
}