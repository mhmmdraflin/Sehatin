package com.example.sehatin.ui.SideFeature.JamMakan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sehatin.R
import com.google.android.material.materialswitch.MaterialSwitch

// 1. Cetakan Data yang menyesuaikan UI Anda
data class JamMakanModel(
    val id: Long,
    var kategori: String,
    var jam: Int,
    var menit: Int,
    var hari: String = "Sekali Saja", // Default
    var snooze: Int = 5, // Tambahan fitur Snooze (Default 5 Menit)
    var isActive: Boolean = true
)

// 2. Adapter untuk RecyclerView
class JamMakanAdapter(
    private val listAlarm: MutableList<JamMakanModel>,
    private val onAlarmClick: (JamMakanModel, Int) -> Unit, // Untuk Edit/Hapus
    private val onSwitchToggle: (JamMakanModel, Boolean) -> Unit // Untuk Switch ON/OFF
) : RecyclerView.Adapter<JamMakanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvKategori: TextView = view.findViewById(R.id.tv_kategori_makan)
        val tvWaktu: TextView = view.findViewById(R.id.tv_waktu_makan)
        val tvHari: TextView = view.findViewById(R.id.tv_hari_makan)
        val switchAlarm: MaterialSwitch = view.findViewById(R.id.switch_alarm)
        val cardItem: View = view // Keseluruhan kartu untuk diklik
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jam_makan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = listAlarm[position]

        // Format angka tunggal jadi dua digit (Contoh: jam 8 jadi 08:00)
        val formatJam = String.format("%02d", alarm.jam)
        val formatMenit = String.format("%02d", alarm.menit)

        holder.tvWaktu.text = "$formatJam:$formatMenit"
        holder.tvKategori.text = alarm.kategori
        holder.tvHari.text = alarm.hari

        // Hindari trigger animasi saat scroll
        holder.switchAlarm.setOnCheckedChangeListener(null)
        holder.switchAlarm.isChecked = alarm.isActive

        // Deteksi jika switch dinyalakan/dimatikan
        holder.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            onSwitchToggle(alarm, isChecked)
        }

        // Deteksi jika keseluruhan kartu diklik (untuk Edit / Hapus)
        holder.cardItem.setOnClickListener {
            onAlarmClick(alarm, position)
        }
    }

    override fun getItemCount(): Int = listAlarm.size
}