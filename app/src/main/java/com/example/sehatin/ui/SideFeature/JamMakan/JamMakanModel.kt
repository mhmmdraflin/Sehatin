package com.example.sehatin.ui.SideFeature.JamMakan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sehatin.R
import com.google.android.material.materialswitch.MaterialSwitch

// 1. Cetakan Data yang telah diperbaiki (Bebas Error)
data class JamMakanModel(
    val id: Long,
    var kategori: String,
    var jam: Int,
    var menit: Int,
    var hari: String = "Sekali Saja",
    var isActive: Boolean = true // Tipe data diperbaiki menjadi Boolean agar switch berfungsi dengan benar
)

// 2. Adapter untuk RecyclerView
class JamMakanAdapter(
    private val listAlarm: MutableList<JamMakanModel>,
    private val onAlarmClick: (JamMakanModel, Int) -> Unit,
    private val onSwitchToggle: (JamMakanModel, Boolean) -> Unit
) : RecyclerView.Adapter<JamMakanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvKategori: TextView = view.findViewById(R.id.tv_kategori_makan)
        val tvWaktu: TextView = view.findViewById(R.id.tv_waktu_makan)
        val tvHari: TextView = view.findViewById(R.id.tv_hari_makan)
        val switchAlarm: MaterialSwitch = view.findViewById(R.id.switch_alarm)
        val cardItem: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jam_makan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = listAlarm[position]

        // Memformat angka tunggal menjadi dua digit (Contoh: jam 8 jadi 08:00)
        val formatJam = String.format("%02d", alarm.jam)
        val formatMenit = String.format("%02d", alarm.menit)

        holder.tvWaktu.text = "$formatJam:$formatMenit"
        holder.tvKategori.text = alarm.kategori
        holder.tvHari.text = alarm.hari

        // Hindari pemicu animasi yang salah saat user melakukan scroll pada daftar
        holder.switchAlarm.setOnCheckedChangeListener(null)
        holder.switchAlarm.isChecked = alarm.isActive

        holder.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            onSwitchToggle(alarm, isChecked)
        }

        holder.cardItem.setOnClickListener {
            onAlarmClick(alarm, position)
        }
    }

    override fun getItemCount(): Int = listAlarm.size
}