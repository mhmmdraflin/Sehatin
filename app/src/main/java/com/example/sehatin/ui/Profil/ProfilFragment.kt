package com.example.sehatin.ui.Profil

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView // Tambahan Import untuk ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.Auth.LoginActivity
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText

import com.example.sehatin.ui.Tantangan.TantanganPreferences
import com.example.sehatin.ui.Tantangan.TantanganRepository
import com.example.sehatin.ui.Tantangan.TantanganViewModel
import com.example.sehatin.ui.Tantangan.TantanganViewModelFactory
import com.example.sehatin.ui.Tantangan.dataStoreTantangan

class ProfilFragment : Fragment() {

    private lateinit var viewModelTantangan: TantanganViewModel

    // Variabel penampung untuk keperluan Transfer Saldo
    private var currentExpToMigrate = 0
    private var currentPoinToMigrate = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profil, container, false)

        val tvNama = view.findViewById<TextView>(R.id.tv_nama_profil)
        val tvEmail = view.findViewById<TextView>(R.id.tv_email_profil)
        val tvLevel = view.findViewById<TextView>(R.id.tv_profil_level)
        val tvPoin = view.findViewById<TextView>(R.id.tv_profil_poin)
        val pbExp = view.findViewById<LinearProgressIndicator>(R.id.pb_profil_exp)
        val tvExpDetail = view.findViewById<TextView>(R.id.tv_profil_exp_detail)

        // Menangkap ID Foto Profil
        val ivFotoProfil = view.findViewById<ImageView>(R.id.iv_foto_profil)

        val btnEditProfil = view.findViewById<MaterialCardView>(R.id.btn_edit_profil_card)
        val btnTukarPoin = view.findViewById<MaterialCardView>(R.id.btn_tukar_poin)
        val btnInventaris = view.findViewById<MaterialCardView>(R.id.btn_inventaris)
        val btnLogout = view.findViewById<MaterialCardView>(R.id.btn_logout)

        val userPref = UserPreference(requireContext())
        val userKey = userPref.getName() ?: "Sobat Sehatin"
        val userGender = userPref.getUserBody().gender // Mengambil data Gender

        tvNama.text = userKey
        tvEmail.text = userPref.getEmail() ?: "$userKey@gmail.com"

        // ==========================================
        // LOGIKA SET FOTO PROFIL BERDASARKAN GENDER
        // ==========================================
        if (userGender == "L") {
            ivFotoProfil.setImageResource(R.drawable.profile_boy)
        } else {
            ivFotoProfil.setImageResource(R.drawable.profile_girl)
        }

        val prefTantangan = TantanganPreferences.getInstance(requireContext().dataStoreTantangan)
        val factory = TantanganViewModelFactory(TantanganRepository(prefTantangan))
        viewModelTantangan = ViewModelProvider(this, factory)[TantanganViewModel::class.java]

        // Pantau EXP sekaligus simpan ke variabel untuk migrasi
        viewModelTantangan.getTotalExp(userKey).observe(viewLifecycleOwner) { totalExp ->
            currentExpToMigrate = totalExp
            val levelSekarang = (totalExp / 100) + 1
            val sisaExpUntukBar = totalExp % 100

            tvLevel.text = "Level $levelSekarang"
            pbExp.progress = sisaExpUntukBar
            tvExpDetail.text = "$sisaExpUntukBar / 100 EXP"
        }

        // Pantau Poin sekaligus simpan ke variabel untuk migrasi
        viewModelTantangan.getTotalPoin(userKey).observe(viewLifecycleOwner) { currentPoin ->
            currentPoinToMigrate = currentPoin
            tvPoin.text = "$currentPoin Poin"
        }

        // ==========================================
        // LOGIKA KLIK EDIT PROFIL
        // ==========================================
        btnEditProfil.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profil, null)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val etNama = dialogView.findViewById<TextInputEditText>(R.id.et_edit_nama)
            val etEmail = dialogView.findViewById<TextInputEditText>(R.id.et_edit_email)
            val btnBatal = dialogView.findViewById<MaterialButton>(R.id.btn_batal_edit)
            val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btn_simpan_edit)

            etNama.setText(userPref.getName())
            val currentEmail = userPref.getEmail() ?: "${userPref.getName()}@gmail.com"
            etEmail.setText(currentEmail)

            btnBatal.setOnClickListener { dialog.dismiss() }

            btnSimpan.setOnClickListener {
                val oldName = userPref.getName() ?: "Sobat Sehatin"
                val namaBaru = etNama.text.toString().trim()
                val emailBaru = etEmail.text.toString().trim()

                if (namaBaru.isNotEmpty() && emailBaru.isNotEmpty()) {

                    // JIKA NAMA BERUBAH -> TRANSFER SALDO POIN & EXP KE NAMA BARU
                    if (oldName != namaBaru) {
                        // 1. Tambahkan saldo ke nama baru
                        viewModelTantangan.tambahExp(namaBaru, currentExpToMigrate)
                        viewModelTantangan.tambahPoin(namaBaru, currentPoinToMigrate)

                        // 2. Kosongkan saldo di nama lama agar tidak ada duplikasi data
                        viewModelTantangan.tambahExp(oldName, -currentExpToMigrate)
                        viewModelTantangan.tambahPoin(oldName, -currentPoinToMigrate)
                    }

                    // Simpan data pendaftaran ke Database
                    userPref.updateProfile(namaBaru, emailBaru)

                    tvNama.text = namaBaru
                    tvEmail.text = emailBaru

                    Toast.makeText(requireContext(), "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Nama dan Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }

        btnTukarPoin.setOnClickListener { startActivity(Intent(requireContext(), TukarPoinActivity::class.java)) }
        btnInventaris.setOnClickListener { startActivity(Intent(requireContext(), InventarisActivity::class.java)) }

        btnLogout.setOnClickListener {
            userPref.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}