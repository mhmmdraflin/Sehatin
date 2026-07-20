package com.example.sehatin.ui.Pencapaian

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.SoundPool
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

    // VARIABEL SOUNDPOOL (Untuk Suara Zero-Delay)
    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    // Cek inisialisasi agar reset BMI hanya jalan sekali
    private var isFirstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPencapaianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // =======================================================
        // INISIALISASI SOUNDPOOL
        // =======================================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        clickSoundId = soundPool?.load(requireContext(), R.raw.tombol_klik_sehatin, 1) ?: 0

        // =======================================================
        // INISIALISASI VIEWMODEL & DATASTORE
        // =======================================================
        val pref = PencapaianPreferences.getInstance(requireContext().dataStorePencapaian)
        val factory = PencapaianViewModelFactory(PencapaianRepository(pref))
        viewModel = ViewModelProvider(this, factory)[PencapaianViewModel::class.java]

        // Pantau Perubahan Data
        viewModel.pencapaianState.observe(viewLifecycleOwner) { state ->

            // 1. AUTO-CLAIM UNTUK USER LAMA/BARU (Langkah Pertama)
            if (state.welcome == 0) {
                viewModel.updateProgress(pref.WELCOME_KEY, 1)
            }

            // 2. PERBAIKAN BUG BMI: Jika ini pertama kali diload,
            // pastikan bahwa BMI tidak otomatis bernilai 1.
            // (Hanya jika Anda menggunakan SharedPreferences lain untuk mengecek login)
            if (isFirstLoad) {
                isFirstLoad = false
                val sharedPrefs = requireContext().getSharedPreferences("BMI_Prefs", android.content.Context.MODE_PRIVATE)
                val isBmiDone = sharedPrefs.getBoolean("hasCheckedBMI", false)

                // Jika sistem belum pernah cek BMI sama sekali tapi state nya 1, paksa jadi 0
                if (!isBmiDone && state.bmi > 0) {
                    viewModel.updateProgress(pref.BMI_KEY, 0)
                }
            }

            updateUI(state)
        }
    }

    private fun mainkanSuaraKlik() {
        soundPool?.play(clickSoundId, 1f, 1f, 0, 0, 1f)
    }

    private fun updateUI(state: PencapaianState) {
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
        val safeValue = if (currentValue > maxValue) maxValue else currentValue

        progressBar.max = maxValue
        progressBar.progress = safeValue

        val isTercapai = safeValue >= maxValue

        if (isTercapai) {
            textView.text = "TERCAPAI!"
            textView.setTextColor(Color.parseColor("#4CAF50"))
            progressBar.setIndicatorColor(Color.parseColor("#4CAF50"))
        } else {
            textView.text = "$safeValue/$maxValue"
            textView.setTextColor(Color.parseColor("#FFD700"))
            progressBar.setIndicatorColor(Color.parseColor("#FFD700"))
        }

        cardView.setOnClickListener {
            mainkanSuaraKlik()
            if (isTercapai) {
                tampilkanDialogLencana(judul, deskripsi, iconRes)
            } else {
                Toast.makeText(requireContext(), "Pencapaian belum terpenuhi! Selesaikan misinya.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun tampilkanDialogLencana(judul: String, deskripsi: String, iconRes: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_lencana, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val ivIcon = dialogView.findViewById<ImageView>(R.id.iv_dialog_badge)
        val tvJudul = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
        val tvDeskripsi = dialogView.findViewById<TextView>(R.id.tv_dialog_desc)
        val btnTutup = dialogView.findViewById<MaterialButton>(R.id.btn_dialog_close)

        ivIcon.setImageResource(iconRes)
        tvJudul.text = judul
        tvDeskripsi.text = deskripsi

        btnTutup.setOnClickListener {
            mainkanSuaraKlik()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soundPool?.release()
        soundPool = null
        _binding = null
    }
}