package com.example.sehatin.Utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log

object BackgroundMusicManager : Application.ActivityLifecycleCallbacks {
    private var mediaPlayer: MediaPlayer? = null

    // PELACAK SUPER AKURAT: Menyimpan ID unik dari setiap halaman yang terbuka
    private val activeActivities = mutableSetOf<Int>()

    private var isLifecycleRegistered = false
    private var currentAudioResId: Int = -1

    fun initialize(application: Application, audioResId: Int) {
        // Pendaftaran Lifecycle cukup 1 kali seumur hidup aplikasi
        if (!isLifecycleRegistered) {
            isLifecycleRegistered = true
            application.registerActivityLifecycleCallbacks(this)
        }

        // Jika lagu yang diminta sama dan player masih hidup, abaikan agar tidak tumpang tindih
        if (currentAudioResId == audioResId && mediaPlayer != null) {
            if (activeActivities.isNotEmpty()) playMusic()
            return
        }

        // Hentikan musik lama secara bersih
        stopMusicInternal()
        currentAudioResId = audioResId

        try {
            mediaPlayer = MediaPlayer.create(application, audioResId)
            mediaPlayer?.isLooping = true

            // =======================================================
            // SISTEM PENYEMBUHAN OTOMATIS (SELF-HEALING)
            // Jika Android tiba-tiba memutus memori audio, restart lagunya!
            // =======================================================
            mediaPlayer?.setOnErrorListener { _, _, _ ->
                Log.e("BGM_SEHATIN", "Audio terputus oleh OS! Memulai ulang...")
                stopMusicInternal()
                initialize(application, audioResId)
                true
            }

            // Terapkan Volume dari SharedPreferences
            val sharedPrefs = application.getSharedPreferences("AudioSettings", Context.MODE_PRIVATE)
            val volumeMusikInt = sharedPrefs.getInt("VOLUME_MUSIK", 100)
            val volumeMusikFloat = volumeMusikInt / 100f
            setVolume(volumeMusikFloat)

            // Jika ada halaman yang sedang terbuka di layar, langsung putar
            if (activeActivities.isNotEmpty()) {
                playMusic()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playMusic() {
        try {
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pauseMusic() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resume() {
        // Hanya lanjutkan musik jika aplikasinya memang sedang dibuka
        if (activeActivities.isNotEmpty()) {
            playMusic()
        }
    }

    fun stopMusic() {
        stopMusicInternal()
    }

    private fun stopMusicInternal() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            currentAudioResId = -1
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVolume(volume: Float) {
        try {
            val vol = volume.coerceIn(0f, 1f)
            mediaPlayer?.setVolume(vol, vol)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ==========================================
    // SENSOR LIFECYCLE BERBASIS HIMPUNAN (ANTI-BUG)
    // ==========================================
    override fun onActivityStarted(activity: Activity) {
        // Tambahkan ID halaman ke dalam daftar aktif
        activeActivities.add(activity.hashCode())
        playMusic()
    }

    override fun onActivityStopped(activity: Activity) {
        // Coret ID halaman dari daftar aktif
        activeActivities.remove(activity.hashCode())

        // Jika daftarnya benar-benar kosong (0 halaman), berarti user keluar aplikasi
        if (activeActivities.isEmpty()) {
            pauseMusic()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}