package com.example.sehatin.ui.Splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sehatin.Auth.LoginActivity
import com.example.sehatin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // PERBAIKAN: Typo 'CourotineScope' diperbaiki jadi 'CoroutineScope'
        CoroutineScope(Dispatchers.Main).launch {
            delay(4000)
            // Pindah ke MainActivity setelah 4 detik
            val splashIntent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(splashIntent)
            finish()
        }
    }
}