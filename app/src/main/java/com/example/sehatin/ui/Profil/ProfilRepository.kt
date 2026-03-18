package com.example.sehatin.ui.Profil

class ProfilRepository(private val pref: ProfilPreferences) {
    fun getProfilData() = pref.getProfilData()
    suspend fun saveEquippedBg(bgId: Int) = pref.saveEquippedBg(bgId)
    suspend fun saveEquippedChar(charId: Int) = pref.saveEquippedChar(charId)
    suspend fun buyBgGym() = pref.buyBgGym()
    suspend fun buyBgTaman() = pref.buyBgTaman()
}