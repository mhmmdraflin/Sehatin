package com.example.sehatin.ui.SideFeature.JamMakan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sehatin.R
import com.example.sehatin.databinding.ActivityPengingatJamMakanBinding
import com.example.sehatin.ui.Pencapaian.PencapaianPreferences
import com.example.sehatin.ui.Pencapaian.PencapaianRepository
import com.example.sehatin.ui.Pencapaian.PencapaianViewModel
import com.example.sehatin.ui.Pencapaian.PencapaianViewModelFactory
import com.example.sehatin.ui.Pencapaian.dataStorePencapaian
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import java.util.Calendar

class Pengingat_Jam_MakanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengingatJamMakanBinding
    private lateinit var adapter: JamMakanAdapter

    // Inisialisasi ViewModel dengan Factory & Repository
    private val viewModel: JamMakanViewModel by viewModels {
        JamMakanViewModelFactory(JamMakanRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengingatJamMakanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        memintaIzinSistem()
        setupRecyclerView()
        observeViewModel() // Memantau perubahan data dari ViewModel

        binding.btnBack.setOnClickListener { finish() }

        binding.fabAddAlarm.setOnClickListener {
            tampilkanDialogPilihKategori(null)
        }
    }

    private fun memintaIzinSistem() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerView() {
        // Menggunakan list kosong sebagai inisialisasi awal, akan diupdate oleh Observer
        adapter = JamMakanAdapter(
            listAlarm = mutableListOf(),
            onAlarmClick = { alarmTerpilih, _ ->
                tampilkanOpsiEditAtauHapus(alarmTerpilih)
            },
            onSwitchToggle = { alarmTerpilih, isChecked ->
                viewModel.toggleAlarmStatus(alarmTerpilih, isChecked)
                if (isChecked) {
                    setAlarm(alarmTerpilih)
                    Toast.makeText(this, "${alarmTerpilih.kategori} Diaktifkan", Toast.LENGTH_SHORT).show()
                } else {
                    cancelAlarm(alarmTerpilih)
                    Toast.makeText(this, "${alarmTerpilih.kategori} Dimatikan", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.rvJamMakan.layoutManager = LinearLayoutManager(this)
        binding.rvJamMakan.adapter = adapter
    }

    // Fungsi canggih MVVM: Memantau data jika ada yang ditambah/dihapus/diubah
    private fun observeViewModel() {

        // ========================================================
        // SENSOR PENCAPAIAN: Lencana Alarm / Disiplin Waktu
        // ========================================================
        val prefPencapaian = PencapaianPreferences.getInstance(applicationContext.dataStorePencapaian)
        val factoryPencapaian = PencapaianViewModelFactory(PencapaianRepository(prefPencapaian))
        val viewModelPencapaian = ViewModelProvider(this, factoryPencapaian)[PencapaianViewModel::class.java]

        viewModel.alarms.observe(this) { daftarAlarm ->

            // Mengirimkan total alarm yang aktif ke Papan Pencapaian
            viewModelPencapaian.updateProgress(prefPencapaian.PENGINGAT_KEY, daftarAlarm.size)

            // Update data di Adapter secara otomatis
            adapter = JamMakanAdapter(
                listAlarm = daftarAlarm.toMutableList(),
                onAlarmClick = { alarmTerpilih, _ -> tampilkanOpsiEditAtauHapus(alarmTerpilih) },
                onSwitchToggle = { alarmTerpilih, isChecked ->
                    viewModel.toggleAlarmStatus(alarmTerpilih, isChecked)
                    if (isChecked) setAlarm(alarmTerpilih) else cancelAlarm(alarmTerpilih)
                }
            )
            binding.rvJamMakan.adapter = adapter
        }
    }

    // ==========================================
    // UI POP-UP MODERN (KATEGORI -> JAM -> HARI)
    // ==========================================

    private fun tampilkanDialogPilihKategori(alarmLama: JamMakanModel?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_kategori, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<Button>(R.id.btn_sarapan).setOnClickListener { dialog.dismiss(); tampilkanCustomTimePicker("Sarapan", alarmLama) }
        dialogView.findViewById<Button>(R.id.btn_makan_siang).setOnClickListener { dialog.dismiss(); tampilkanCustomTimePicker("Makan Siang", alarmLama) }
        dialogView.findViewById<Button>(R.id.btn_makan_malam).setOnClickListener { dialog.dismiss(); tampilkanCustomTimePicker("Makan Malam", alarmLama) }
        dialogView.findViewById<Button>(R.id.btn_cemilan).setOnClickListener { dialog.dismiss(); tampilkanCustomTimePicker("Cemilan Sehat", alarmLama) }

        dialog.show()
    }

    private fun tampilkanCustomTimePicker(kategori: String, alarmLama: JamMakanModel?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_waktu, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<TextView>(R.id.tv_judul_waktu).text = "Atur Jam $kategori"
        val timePicker = dialogView.findViewById<TimePicker>(R.id.custom_time_picker)
        timePicker.setIs24HourView(true)

        val calendar = Calendar.getInstance()
        val jamAwal = alarmLama?.jam ?: calendar.get(Calendar.HOUR_OF_DAY)
        val menitAwal = alarmLama?.menit ?: calendar.get(Calendar.MINUTE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.hour = jamAwal
            timePicker.minute = menitAwal
        } else {
            @Suppress("DEPRECATION")
            timePicker.currentHour = jamAwal
            @Suppress("DEPRECATION")
            timePicker.currentMinute = menitAwal
        }

        dialogView.findViewById<Button>(R.id.btn_batal_waktu).setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<Button>(R.id.btn_simpan_waktu).setOnClickListener {
            dialog.dismiss()
            val jPilihan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) timePicker.hour else @Suppress("DEPRECATION") timePicker.currentHour
            val mPilihan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) timePicker.minute else @Suppress("DEPRECATION") timePicker.currentMinute
            tampilkanDialogPilihHari(kategori, jPilihan, mPilihan, alarmLama)
        }
        dialog.show()
    }

    private fun tampilkanDialogPilihHari(kategori: String, jam: Int, menit: Int, alarmLama: JamMakanModel?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_pengaturan_alarm, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val rgTipeUlangi = dialogView.findViewById<RadioGroup>(R.id.rg_tipe_ulangi)
        val layoutKustomHari = dialogView.findViewById<LinearLayout>(R.id.layout_kustom_hari)
        val sliderSnooze = dialogView.findViewById<Slider>(R.id.slider_snooze)
        val tvLabelSnooze = dialogView.findViewById<TextView>(R.id.tv_label_snooze)

        val checkBoxes = arrayOf(
            dialogView.findViewById<CheckBox>(R.id.cb_senin), dialogView.findViewById<CheckBox>(R.id.cb_selasa),
            dialogView.findViewById<CheckBox>(R.id.cb_rabu), dialogView.findViewById<CheckBox>(R.id.cb_kamis),
            dialogView.findViewById<CheckBox>(R.id.cb_jumat), dialogView.findViewById<CheckBox>(R.id.cb_sabtu),
            dialogView.findViewById<CheckBox>(R.id.cb_minggu)
        )
        val namaHari = arrayOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

        var durasiSnoozePilihan = alarmLama?.snooze ?: 5
        sliderSnooze.value = durasiSnoozePilihan.toFloat()
        tvLabelSnooze.text = "Waktu Tunda (Snooze): $durasiSnoozePilihan Menit"

        if (alarmLama != null) {
            when (alarmLama.hari) {
                "Sekali Saja" -> dialogView.findViewById<RadioButton>(R.id.rb_sekali_saja).isChecked = true
                "Setiap Hari" -> dialogView.findViewById<RadioButton>(R.id.rb_setiap_hari).isChecked = true
                else -> {
                    dialogView.findViewById<RadioButton>(R.id.rb_kustom).isChecked = true
                    layoutKustomHari.visibility = View.VISIBLE
                    for (i in checkBoxes.indices) {
                        if (alarmLama.hari.contains(namaHari[i].substring(0, 3))) checkBoxes[i].isChecked = true
                    }
                }
            }
        } else {
            dialogView.findViewById<RadioButton>(R.id.rb_sekali_saja).isChecked = true
        }

        rgTipeUlangi.setOnCheckedChangeListener { _, checkedId ->
            layoutKustomHari.visibility = if (checkedId == R.id.rb_kustom) View.VISIBLE else View.GONE
        }

        sliderSnooze.addOnChangeListener { _, value, _ ->
            durasiSnoozePilihan = value.toInt()
            tvLabelSnooze.text = "Waktu Tunda (Snooze): $durasiSnoozePilihan Menit"
        }

        dialogView.findViewById<Button>(R.id.btn_batal_pengaturan).setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<Button>(R.id.btn_simpan_pengaturan).setOnClickListener {
            val teksHariAkhir = when (rgTipeUlangi.checkedRadioButtonId) {
                R.id.rb_sekali_saja -> "Sekali Saja"
                R.id.rb_setiap_hari -> "Setiap Hari"
                R.id.rb_kustom -> {
                    val hariDipilih = mutableListOf<String>()
                    for (i in checkBoxes.indices) if (checkBoxes[i].isChecked) hariDipilih.add(namaHari[i].substring(0, 3))
                    if (hariDipilih.isEmpty()) "Sekali Saja" else hariDipilih.joinToString(", ")
                }
                else -> "Sekali Saja"
            }
            dialog.dismiss()
            prosesSimpanAkhir(kategori, jam, menit, teksHariAkhir, durasiSnoozePilihan, alarmLama)
        }
        dialog.show()
    }

    private fun prosesSimpanAkhir(kategori: String, jam: Int, menit: Int, teksHari: String, snooze: Int, alarmLama: JamMakanModel?) {
        val alarmModel: JamMakanModel

        if (alarmLama == null) {
            alarmModel = JamMakanModel(System.currentTimeMillis(), kategori, jam, menit, teksHari, snooze, true)
            viewModel.addAlarm(alarmModel)
            Toast.makeText(this, "Alarm Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
        } else {
            val newAlarm = JamMakanModel(alarmLama.id, kategori, jam, menit, teksHari, snooze, true)
            viewModel.updateAlarm(alarmLama, newAlarm)
            alarmModel = newAlarm
            Toast.makeText(this, "Alarm Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
        }
        setAlarm(alarmModel)
    }

    // ==========================================
    // OPSI & ALARM MANAGER
    // ==========================================
    private fun tampilkanOpsiEditAtauHapus(alarm: JamMakanModel) {
        val opsi = arrayOf("Ubah Pengaturan Alarm", "Hapus Alarm")
        MaterialAlertDialogBuilder(this)
            .setTitle("Atur Pengingat")
            .setItems(opsi) { _, which ->
                when (which) {
                    0 -> tampilkanDialogPilihKategori(alarm)
                    1 -> hapusAlarm(alarm)
                }
            }
            .show()
    }

    private fun hapusAlarm(alarm: JamMakanModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Hapus Alarm")
            .setMessage("Yakin ingin menghapus pengingat ${alarm.kategori}?")
            .setPositiveButton("Hapus") { _, _ ->
                cancelAlarm(alarm)
                viewModel.deleteAlarm(alarm)
                Toast.makeText(this, "Alarm Dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun setAlarm(alarm: JamMakanModel) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_KATEGORI", alarm.kategori)
            putExtra("EXTRA_ID", alarm.id.toInt())
            putExtra("EXTRA_SNOOZE_WAKTU", alarm.snooze)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, alarm.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.jam)
            set(Calendar.MINUTE, alarm.menit)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
        }
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } catch (e: SecurityException) {
            Toast.makeText(this, "Izin Alarm ditolak sistem!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelAlarm(alarm: JamMakanModel) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, alarm.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }
}