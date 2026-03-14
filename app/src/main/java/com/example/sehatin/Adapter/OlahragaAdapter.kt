package com.example.sehatin.ui.SideFeature.Olahraga

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sehatin.R
import com.google.android.material.card.MaterialCardView

// 1. Model Data Olahraga (Ditambahkan expDidapat)
data class GerakanOlahraga(
    val idGerakan: Int,
    val namaGerakan: String,
    val durasiDetik: Int,
    val kaloriTerbakar: Int,
    val expDidapat: Int, // Takaran EXP khusus
    val gambarStatis: Int,
    val gifResourceFile: Int
)

// 2. Adapter
class OlahragaAdapter(
    private var listGerakan: List<GerakanOlahraga>,
    private val onItemClick: (GerakanOlahraga) -> Unit
) : RecyclerView.Adapter<OlahragaAdapter.OlahragaViewHolder>() {

    // Menyimpan daftar ID gerakan yang sudah selesai
    private var completedIds: List<Int> = emptyList()

    fun setCompletedGerakan(ids: List<Int>) {
        this.completedIds = ids
        notifyDataSetChanged()
    }

    class OlahragaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tv_item_nama_olahraga)
        val tvInfo: TextView = view.findViewById(R.id.tv_item_info_olahraga)
        val ivKarakter: ImageView = view.findViewById(R.id.iv_char_pushup)
        val tvStatus: TextView = view.findViewById(R.id.btn_item_status)
        val cardItem: MaterialCardView = view as MaterialCardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OlahragaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_olahraga, parent, false)
        return OlahragaViewHolder(view)
    }

    override fun onBindViewHolder(holder: OlahragaViewHolder, position: Int) {
        val gerakan = listGerakan[position]

        holder.tvNama.text = gerakan.namaGerakan
        holder.tvInfo.text = "⏱ ${gerakan.durasiDetik} Detik  •  🔥 ${gerakan.kaloriTerbakar} Kalori"
        holder.ivKarakter.setImageResource(gerakan.gambarStatis)

        // Logika Status MVVM
        if (completedIds.contains(gerakan.idGerakan)) {
            holder.tvStatus.text = "Selesai"
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_status_selesai) // Pastikan file drawable ini ada
        } else {
            holder.tvStatus.text = "Belum Selesai"
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_status)
        }

        holder.cardItem.setOnClickListener { onItemClick(gerakan) }
    }

    override fun getItemCount(): Int = listGerakan.size
}