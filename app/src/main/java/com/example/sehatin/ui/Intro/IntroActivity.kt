package com.example.sehatin.ui.Intro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.sehatin.Auth.LoginActivity
import com.example.sehatin.R
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Runnable

class IntroActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnNext: AppCompatButton
    private lateinit var btnSkip: TextView

    private val sliderHandler = Handler(Looper.getMainLooper())

    private val introData by lazy {
        listOf(
            IntroItem(
                R.drawable.intro1,
                getString(R.string.intro1),
                getString(R.string.intro1_desc)
            ),
            IntroItem(
                R.drawable.intro_page2,
                getString(R.string.intro2),
                getString(R.string.intro2_desc)
            ),
            IntroItem(
                R.drawable.intro_page3,
                getString(R.string.intro3),
                getString(R.string.intro3_desc)
            )
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        btnNext = findViewById(R.id.btn_next)
        btnSkip = findViewById(R.id.btn_skip)

        // 1. Setup Adapter
        val adapter = IntroAdapter(introData)
        viewPager.adapter = adapter

        // Mulai dari posisi tengah agar user bisa swipe kiri/kanan (Infinite Illusion)
        // Kita mulai dari angka yang habis dibagi ukuran data (misal: 1000 * 3)
        // agar perhitungan modulo tetap akurat.
        viewPager.setCurrentItem(introData.size * 100, false)

        // 2. SETUP DOTS INDICATOR (MANUAL - JANGAN PAKAI MEDIATOR)
        // Kita buat tab hanya sebanyak data asli (3 biji), bukan Int.MAX_VALUE
        introData.forEach { _ ->
            tabLayout.addTab(tabLayout.newTab())
        }

        // Matikan interaksi klik pada tab (opsional, karena slide kita infinite)
        tabLayout.touchables.forEach { it.isEnabled = false }


        // 3. Logic Tombol Next
        btnNext.setOnClickListener {
            val currentRealPosition = viewPager.currentItem % introData.size
            if (currentRealPosition == introData.size - 1) {
                navigateToLogin()
            } else {
                viewPager.currentItem += 1
            }
        }

        // 4. Logic Tombol Skip
        btnSkip.setOnClickListener {
            navigateToLogin()
        }

        // 5. Callback Perubahan Halaman & Sinkronisasi Dots
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Hitung posisi asli (0, 1, atau 2)
                val realPosition = position % introData.size

                // SINKRONISASI MANUAL TAB LAYOUT
                // Pilih tab yang sesuai dengan posisi asli
                val tab = tabLayout.getTabAt(realPosition)
                tab?.select()

                // Ubah teks tombol
                if (realPosition == introData.size - 1) {
                    btnNext.text = getString(R.string.Mulai_Sekarang)
                } else {
                    btnNext.text = getString(R.string.Selanjutnya)
                }

                // Reset Timer Auto Scroll
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000)
            }
        })
    }

    // --- LOGIC INFINITE AUTO SWIPE ---
    private val sliderRunnable = Runnable {
        viewPager.currentItem += 1
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    data class IntroItem(val image: Int, val title: String, val desc: String)

    // --- ADAPTER ---
    inner class IntroAdapter(private val items: List<IntroItem>) :
        RecyclerView.Adapter<IntroAdapter.IntroViewHolder>() {

        inner class IntroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val imgIntro = view.findViewById<ImageView>(R.id.img_intro)
            private val tvTitle = view.findViewById<TextView>(R.id.tv_title)
            private val tvDesc = view.findViewById<TextView>(R.id.tv_desc)

            fun bind(item: IntroItem) {
                imgIntro.setImageResource(item.image)
                tvTitle.text = item.title
                tvDesc.text = item.desc
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_intro, parent, false)
            return IntroViewHolder(view)
        }

        override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
            val realPosition = position % items.size
            holder.bind(items[realPosition])
        }

        override fun getItemCount(): Int = Int.MAX_VALUE
    }
}