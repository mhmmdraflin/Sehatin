package com.example.sehatin.ui.Splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.sehatin.databinding.ActivitySplashBinding
import com.example.sehatin.ui.Intro.IntroActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoImage.translationX = -200f
        binding.logoImage.alpha = 0f

        binding.logoText.translationX = 200f
        binding.logoText.alpha = 0f

        binding.tvSlogan.translationY = 50f
        binding.tvSlogan.alpha = 0f

        val duration = 1200L

        binding.logoImage.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()

        binding.logoText.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()

        binding.tvSlogan.animate()
            .translationY(0f)
            .alpha(1f)
            .setStartDelay(600)
            .setDuration(800)
            .start()

        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            finish()
        }
    }
}