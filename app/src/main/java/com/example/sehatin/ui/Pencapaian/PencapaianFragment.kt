package com.example.sehatin.ui.Pencapaian

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.R
import com.example.sehatin.databinding.FragmentPencapaianBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class PencapaianFragment : Fragment() {

    private var _binding: FragmentPencapaianBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PencapaianViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPencapaianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inisialisasi ViewModel & DataStore
        val pref = PencapaianPreferences.getInstance(requireContext().dataStorePencapaian)
        val factory = PencapaianViewModelFactory(PencapaianRepository(pref))
        viewModel = ViewModelProvider(this, factory)[PencapaianViewModel::class.java]

        // 2. Pantau (Observe) perubahan data secara Real-Time
        viewModel.pencapaianState.observe(viewLifecycleOwner) { state ->

            // AUTO-CLAIM UNTUK USER LAMA
            if (state.welcome == 0) {
                viewModel.updateProgress(pref.WELCOME_KEY, 1)
            }

            // Perbarui tampilan UI dan berikan fitur Klik
            updateUI(state)
        }
    }

    private fun updateUI(state: PencapaianState) {
        // Format: (Kartu, ProgressBar, TextView, Nilai Saat Ini, Target Maksimal, Judul, Deskripsi, Icon)

        aturLencana(
            binding.cardAchievWelcome, binding.progressWelcome, binding.tvProgressWelcome,
            state.welcome, 1, "Langkah Pertama", "Resmi menjadi bagian dari keluarga Sehatin", R.drawable.logo_sehatin
        )

        aturLencana(
            binding.cardAchievBmi, binding.progressBmi, binding.tvProgressBmi,
            state.bmi, 1, "Kesadaran Diri", "Cek Kalkulator BMI Anda untuk pertama kali", R.drawable.logo_sehatin
        )

        aturLencana(
            binding.cardAchievMakanan, binding.progressMakanan, binding.tvProgressMakanan,
            state.makanan, 10, "Si Paling Paham Nutrisi", "Selesaikan 10 Misi Kuis Edukasi Makanan", R.drawable.logo_sehatin
        )

        aturLencana(
            binding.cardAchievPushup, binding.progressPushup, binding.tvProgressPushup,
            state.pushup, 1, "Pejuang Push Up", "Selesaikan Tantangan Push Up perdanamu", R.drawable.logo_sehatin
        )

        aturLencana(
            binding.cardAchievPlank, binding.progressPlank, binding.tvProgressPlank,
            state.plank, 5, "Master Plank", "Bertahan dalam tantangan Plank 5 kali", R.drawable.logo_sehatin
        )

        aturLencana(
            binding.cardAchievPengingat, binding.progressPengingat, binding.tvProgressPengingat,
            state.pengingat, 3, "Disiplin Waktu", "Aktifkan minimal 3 Alarm Pengingat", R.drawable.logo_sehatin
        )

        aturLencana(
            binding.cardAchievPoin, binding.progressPoin, binding.tvProgressPoin,
            state.poin, 1000, "Sultan Poin", "Kumpulkan 1000 Poin Sehatin pertamamu", R.drawable.logo_sehatin
        )

        aturLencana(
            binding.cardAchievExp, binding.progressExp, binding.tvProgressExp,
            state.exp, 500, "Level Up!", "Kumpulkan 500 EXP dari berbagai misi", R.drawable.logo_sehatin
        )
    }

    private fun aturLencana(
        cardView: MaterialCardView,
        progressBar: com.google.android.material.progressindicator.LinearProgressIndicator,
        textView: TextView,
        currentValue: Int,
        maxValue: Int,
        judul: String,
        deskripsi: String,
        iconRes: Int
    ) {
        // Cegah agar progres tidak melebihi batas
        val safeValue = if (currentValue > maxValue) maxValue else currentValue

        progressBar.max = maxValue
        progressBar.progress = safeValue

        val isTercapai = safeValue >= maxValue

        if (isTercapai) {
            // UI Jika Lencana Selesai Tercapai
            textView.text = "TERCAPAI!"
            textView.setTextColor(Color.parseColor("#4CAF50"))
            progressBar.setIndicatorColor(Color.parseColor("#4CAF50"))
        } else {
            // UI Jika Lencana Belum Selesai
            textView.text = "$safeValue/$maxValue"
            textView.setTextColor(Color.parseColor("#FFD700"))
            progressBar.setIndicatorColor(Color.parseColor("#FFD700"))
        }

        // ==========================================
        // LOGIKA KLIK KARTU (POP-UP / TOAST)
        // ==========================================
        cardView.setOnClickListener {
            if (isTercapai) {
                tampilkanDialogLencana(judul, deskripsi, iconRes)
            } else {
                Toast.makeText(requireContext(), "Pencapaian belum terpenuhi! Selesaikan misinya.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ==========================================
    // FUNGSI MEMUNCULKAN POP-UP (DIALOG)
    // ==========================================
    private fun tampilkanDialogLencana(judul: String, deskripsi: String, iconRes: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_lencana, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Buat background kotak dialog bawaan Android menjadi transparan
        // agar MaterialCardView kita yang melengkung terlihat cantik
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val ivIcon = dialogView.findViewById<ImageView>(R.id.iv_dialog_badge)
        val tvJudul = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
        val tvDeskripsi = dialogView.findViewById<TextView>(R.id.tv_dialog_desc)
        val btnTutup = dialogView.findViewById<MaterialButton>(R.id.btn_dialog_close)

        // Set data sesuai lencana yang diklik
        ivIcon.setImageResource(iconRes)
        tvJudul.text = judul
        tvDeskripsi.text = deskripsi

        btnTutup.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}