package com.example.sehatin.ui.SideFeature.JamMakan

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JamMakanRepository(context: Context) {
    private val prefs = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Mengambil data dari memori
    fun getAlarms(): MutableList<JamMakanModel> {
        val jsonString = prefs.getString("LIST_ALARM", null)
        return if (jsonString != null) {
            val type = object : TypeToken<MutableList<JamMakanModel>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            mutableListOf()
        }
    }

    // Menyimpan data ke memori
    fun saveAlarms(alarms: List<JamMakanModel>) {
        val jsonString = gson.toJson(alarms)
        prefs.edit().putString("LIST_ALARM", jsonString).apply()
    }
}