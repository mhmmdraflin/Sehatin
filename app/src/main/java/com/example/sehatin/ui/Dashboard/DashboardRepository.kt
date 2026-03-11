package com.example.sehatin.ui.Dashboard

import com.example.sehatin.Data.Local.UserBody
import com.example.sehatin.Data.Model.UserPreference

class DashboardRepository(private val pref: UserPreference) {

    fun getName(): String? {
        return pref.getName()
    }

    fun getUserBody(): UserBody {
        return pref.getUserBody()
    }

    fun getKondisiTubuh(): String {
        return pref.getKondisiTubuh()
    }

    fun getPoint(): Int {
        return pref.getPoint()
    }

    fun getExp(): Int {
        return pref.getExp()
    }
}