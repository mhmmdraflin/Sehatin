package com.example.sehatin.ui.Tantangan.Olahraga

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.ui.Tantangan.Olahraga.DetailTantanganActivity
import com.example.sehatin.ui.Tantangan.TantanganPreferences
import com.example.sehatin.ui.Tantangan.TantanganRepository
import com.example.sehatin.ui.Tantangan.TantanganViewModel
import com.example.sehatin.ui.Tantangan.TantanganViewModelFactory
import com.example.sehatin.ui.Tantangan.dataStoreTantangan
import com.google.android.material.card.MaterialCardView

class TantanganFragment : Fragment() {

    private lateinit var viewModel: TantanganViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tantangan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ==========================================
        // TENTUKAN IDENTITAS USER AKTIF (Menggunakan getName)
        // ==========================================
        val userPref = UserPreference(requireContext())
        val userKey = userPref.getName() ?: "guest_user"

        val pref = TantanganPreferences.getInstance(requireContext().dataStoreTantangan)
        val factory = TantanganViewModelFactory(TantanganRepository(pref))
        viewModel = ViewModelProvider(this, factory)[TantanganViewModel::class.java]

        val tvTotalPoin = view.findViewById<TextView>(R.id.tv_total_poin_header)
        val btnPeriksaOlahraga = view.findViewById<MaterialCardView>(R.id.btn_periksa)

        // BACA TOTAL POIN BERDASARKAN AKUN
        viewModel.getTotalPoin(userKey).observe(viewLifecycleOwner) { totalPoin ->
            tvTotalPoin.text = "$totalPoin Poin"
        }

        btnPeriksaOlahraga.setOnClickListener {
            val intent = Intent(requireContext(), DetailTantanganActivity::class.java)
            startActivity(intent)
        }
    }
}