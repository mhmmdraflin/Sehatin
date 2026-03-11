package com.example.sehatin.ui.SideFeature.JamMakan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.sehatin.R
import com.example.sehatin.Main.MainActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit

// Data Class untuk menyimpan riwayat notifikasi ke Dashboard
data class NotifikasiRiwayat(
    val id: Long,
    val judul: String,
    val pesan: String,
    val waktu: String
)

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val kategori = intent.getStringExtra("EXTRA_KATEGORI") ?: "Waktunya Makan"
        val idAlarm = intent.getIntExtra("EXTRA_ID", 1)

        val pesan = "Hai Sobat Sehatin! Sudah waktunya untuk $kategori. Jangan lupa makan makanan bergizi ya!"

        // 1. Tampilkan Notifikasi di HP
        tampilkanNotifikasi(context, idAlarm, kategori, pesan)

        // 2. Simpan ke Riwayat untuk ditampilkan di Dashboard nanti
        simpanRiwayatNotifikasi(context, kategori, pesan)
    }

    private fun tampilkanNotifikasi(context: Context, id: Int, judul: String, pesan: String) {
        val channelId = "Sehatin_Alarm_Channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Membuat Channel untuk Android 8.0 (Oreo) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pengingat Jam Makan Sehatin",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Aksi saat notifikasi di-klik (Buka aplikasi)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Desain Notifikasi
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo_informasi_makanan) // Ganti dengan icon yang Anda punya
            .setContentTitle(judul)
            .setContentText(pesan)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(id, builder.build())
    }

    private fun simpanRiwayatNotifikasi(context: Context, judul: String, pesan: String) {
        val prefs = context.getSharedPreferences("NotifikasiPrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        // Ambil data riwayat lama
        val jsonLama = prefs.getString("LIST_NOTIFIKASI", null)
        val type = object : TypeToken<MutableList<NotifikasiRiwayat>>() {}.type
        val daftarRiwayat: MutableList<NotifikasiRiwayat> = if (jsonLama != null) {
            gson.fromJson(jsonLama, type)
        } else {
            mutableListOf()
        }

        // Tambah riwayat baru (dengan jam saat ini)
        val waktuSekarang = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date())
        val riwayatBaru = NotifikasiRiwayat(System.currentTimeMillis(), judul, pesan, waktuSekarang)

        // Taruh di paling atas (index 0)
        daftarRiwayat.add(0, riwayatBaru)

        // Simpan kembali
        prefs.edit { putString("LIST_NOTIFIKASI", gson.toJson(daftarRiwayat)) }
    }
}