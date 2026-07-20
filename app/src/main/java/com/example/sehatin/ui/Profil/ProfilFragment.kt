package com.example.sehatin.ui.Profil

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sehatin.Auth.LoginActivity
import com.example.sehatin.Data.Model.UserPreference
import com.example.sehatin.R
import com.example.sehatin.databinding.FragmentProfilBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.slider.Slider
import com.example.sehatin.ui.Tantangan.TantanganPreferences
import com.example.sehatin.ui.Tantangan.TantanganRepository
import com.example.sehatin.ui.Tantangan.TantanganViewModel
import com.example.sehatin.ui.Tantangan.TantanganViewModelFactory
import com.example.sehatin.ui.Tantangan.dataStoreTantangan
import androidx.core.graphics.drawable.toDrawable
import com.example.sehatin.Utils.BackgroundMusicManager

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelTantangan: TantanganViewModel

    private var currentExpToMigrate = 0
    private var currentPoinToMigrate = 0

    // VARIABEL SOUNDPOOL (Untuk Suara Zero-Delay)
    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
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

        // Memuat efek suara klik ke dalam memori
        clickSoundId = soundPool?.load(requireContext(), R.raw.tombol_klik_sehatin, 1) ?: 0
        // =======================================================

        val userPref = UserPreference(requireContext())
        val userKey = userPref.getName() ?: "Sobat Sehatin"
        val userGender = userPref.getUserBody().gender

        binding.tvNamaProfil.text = userKey
        binding.tvEmailProfil.text = userPref.getEmail() ?: "$userKey@gmail.com"

        if (userGender == "L") {
            binding.ivFotoProfil.setImageResource(R.drawable.profile_boy)
        } else {
            binding.ivFotoProfil.setImageResource(R.drawable.profile_girl)
        }

        val prefTantangan = TantanganPreferences.getInstance(requireContext().dataStoreTantangan)
        val factory = TantanganViewModelFactory(TantanganRepository(prefTantangan))
        viewModelTantangan = ViewModelProvider(this, factory)[TantanganViewModel::class.java]

        viewModelTantangan.getTotalExp(userKey).observe(viewLifecycleOwner) { totalExp ->
            currentExpToMigrate = totalExp
            val levelSekarang = (totalExp / 100) + 1
            val sisaExpUntukBar = totalExp % 100

            binding.tvProfilLevel.text = "Level $levelSekarang"
            binding.pbProfilExp.progress = sisaExpUntukBar
            binding.tvProfilExpDetail.text = "$sisaExpUntukBar / 100 EXP"
        }

        viewModelTantangan.getTotalPoin(userKey).observe(viewLifecycleOwner) { currentPoin ->
            currentPoinToMigrate = currentPoin
            binding.tvProfilPoin.text = "$currentPoin Poin"
        }

        // ==========================================
        // LOGIKA KLIK TOMBOL UTAMA PROFIL
        // ==========================================
        binding.btnPengaturan.setOnClickListener {
            mainkanSuaraKlik()
            tampilkanDialogPengaturanSuara()
        }

        binding.btnEditProfilCard.setOnClickListener {
            mainkanSuaraKlik()
            val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profil, null)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()

            dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

            val etNama = dialogView.findViewById<TextInputEditText>(R.id.et_edit_nama)
            val etEmail = dialogView.findViewById<TextInputEditText>(R.id.et_edit_email)
            val btnBatal = dialogView.findViewById<MaterialButton>(R.id.btn_batal_edit)
            val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btn_simpan_edit)

            etNama.setText(userPref.getName())
            val currentEmail = userPref.getEmail() ?: "${userPref.getName()}@gmail.com"
            etEmail.setText(currentEmail)

            btnBatal.setOnClickListener {
                mainkanSuaraKlik()
                dialog.dismiss()
            }

            btnSimpan.setOnClickListener {
                mainkanSuaraKlik()
                val oldName = userPref.getName() ?: "Sobat Sehatin"
                val namaBaru = etNama.text.toString().trim()
                val emailBaru = etEmail.text.toString().trim()

                if (namaBaru.isNotEmpty() && emailBaru.isNotEmpty()) {
                    if (oldName != namaBaru) {
                        viewModelTantangan.tambahExp(namaBaru, currentExpToMigrate)
                        viewModelTantangan.tambahPoin(namaBaru, currentPoinToMigrate)
                        viewModelTantangan.tambahExp(oldName, -currentExpToMigrate)
                        viewModelTantangan.tambahPoin(oldName, -currentPoinToMigrate)
                    }

                    userPref.updateProfile(namaBaru, emailBaru)
                    binding.tvNamaProfil.text = namaBaru
                    binding.tvEmailProfil.text = emailBaru

                    Toast.makeText(requireContext(), "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Nama dan Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }

        binding.btnTukarPoin.setOnClickListener {
            mainkanSuaraKlik()
            startActivity(Intent(requireContext(), TukarPoinActivity::class.java))
        }

        binding.btnInventaris.setOnClickListener {
            mainkanSuaraKlik()
            startActivity(Intent(requireContext(), InventarisActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            mainkanSuaraKlik()
            userPref.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    // ========================================================
    // FUNGSI PEMANGGIL SUARA KLIK DINAMIS
    // ========================================================
    private fun mainkanSuaraKlik() {
        val sharedPrefs = requireContext().getSharedPreferences("AudioSettings", Context.MODE_PRIVATE)
        val volumeSfxInt = sharedPrefs.getInt("VOLUME_SFX", 100)
        val volumeSfxFloat = volumeSfxInt / 100f
        soundPool?.play(clickSoundId, volumeSfxFloat, volumeSfxFloat, 0, 0, 1f)
    }

    // ========================================================
    // DIALOG PENGATURAN VOLUME SUARA
    // ========================================================
    private fun tampilkanDialogPengaturanSuara() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_pengaturan_suara, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val sliderMusik = dialogView.findViewById<Slider>(R.id.slider_musik_latar)
        val sliderSfx = dialogView.findViewById<Slider>(R.id.slider_sfx_tombol)
        val btnTutup = dialogView.findViewById<MaterialButton>(R.id.btn_tutup_pengaturan)

        val sharedPrefs = requireContext().getSharedPreferences("AudioSettings", Context.MODE_PRIVATE)
        val volumeMusikLama = sharedPrefs.getInt("VOLUME_MUSIK", 100)
        val volumeSfxLama = sharedPrefs.getInt("VOLUME_SFX", 100)

        sliderMusik.value = volumeMusikLama.toFloat()
        sliderSfx.value = volumeSfxLama.toFloat()

        // UBAH VOLUME MUSIK SECARA REAL-TIME SAAT SLIDER DIGESER
        sliderMusik.addOnChangeListener { _, value, _ ->
            val floatVolume = value / 100f
            BackgroundMusicManager.setVolume(floatVolume)
        }

        // PREVIEW SFX SECARA REAL-TIME SAAT SLIDER DIGESER
        sliderSfx.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                val floatVolume = value / 100f
                soundPool?.play(clickSoundId, floatVolume, floatVolume, 0, 0, 1f)
            }
        }

        btnTutup.setOnClickListener {
            // Tangkap nilai terbaru dari slider
            val volumeMusikBaru = sliderMusik.value.toInt()
            val volumeSfxBaru = sliderSfx.value.toInt()

            // Simpan perubahan ke memori
            sharedPrefs.edit().apply {
                putInt("VOLUME_MUSIK", volumeMusikBaru)
                putInt("VOLUME_SFX", volumeSfxBaru)
                apply()
            }

            // Mainkan suara SETELAH data disimpan agar volumenya 100% akurat sesuai setelan baru
            mainkanSuaraKlik()

            Toast.makeText(requireContext(), "Pengaturan suara disimpan!", Toast.LENGTH_SHORT).show()
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