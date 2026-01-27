package com.example.sehatin.ui.Splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sehatin.Auth.LoginActivity
import com.example.sehatin.R
import com.example.sehatin.ui.Intro.IntroActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val imgLogo = findViewById<ImageView>(R.id.logo_image)
        val txtLogo = findViewById<TextView>(R.id.logo_text)
        imgLogo.translationX = -100f
        imgLogo.alpha = 0f
        txtLogo.translationX = 100f
        txtLogo.alpha = 0f
        val duration = 1500L
        imgLogo.animate().translationX(0f).alpha(1f).setDuration(duration).start()
        txtLogo.animate().translationX(0f).alpha(1f).setDuration(duration).start()
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            finish()
        }
    }
}