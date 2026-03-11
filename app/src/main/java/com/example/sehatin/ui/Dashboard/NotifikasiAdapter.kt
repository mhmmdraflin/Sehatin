package com.example.sehatin.ui.Dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sehatin.R
import com.example.sehatin.ui.SideFeature.JamMakan.NotifikasiRiwayat

class NotifikasiAdapter(private var listNotif: List<NotifikasiRiwayat>) : RecyclerView.Adapter<NotifikasiAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvWaktu: TextView = view.findViewById(R.id.tv_item_waktu_notif)
        val tvJudul: TextView = view.findViewById(R.id.tv_item_judul_notif)
        val tvPesan: TextView = view.findViewById(R.id.tv_item_pesan_notif)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notifikasi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notif = listNotif[position]
        holder.tvWaktu.text = notif.waktu
        holder.tvJudul.text = notif.judul
        holder.tvPesan.text = notif.pesan
    }

    override fun getItemCount(): Int = listNotif.size

    fun updateData(newList: List<NotifikasiRiwayat>) {
        listNotif = newList
        notifyDataSetChanged()
    }
}