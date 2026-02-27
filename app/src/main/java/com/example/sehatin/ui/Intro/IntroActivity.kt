package com.example.sehatin.ui.Intro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.sehatin.Auth.LoginActivity
import com.example.sehatin.R
import com.example.sehatin.databinding.ActivityIntroBinding
import com.example.sehatin.databinding.ItemIntroBinding

class IntroActivity : AppCompatActivity() {

    // Inisialisasi ViewBinding untuk Activity
    private lateinit var binding: ActivityIntroBinding

    private val sliderHandler = Handler(Looper.getMainLooper())

    private val introData by lazy {
        listOf(
            IntroItem(R.drawable.intro1, getString(R.string.intro1), getString(R.string.intro1_desc)),
            IntroItem(R.drawable.intro2, getString(R.string.intro2), getString(R.string.intro2_desc)),
            IntroItem(R.drawable.intro3, getString(R.string.intro3), getString(R.string.intro3_desc))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate Binding
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPagerAndTabs()
        setupButtons()
    }

    private fun setupViewPagerAndTabs() {
        val adapter = IntroAdapter(introData)
        binding.viewPager.adapter = adapter

        // Mulai dari angka yang habis dibagi ukuran data (misal: 1000 * 3) untuk Infinite Slider
        binding.viewPager.setCurrentItem(introData.size * 100, false)

        // Setup manual indikator titik (dots)
        introData.forEach { _ ->
            binding.tabLayout.addTab(binding.tabLayout.newTab())
        }
        binding.tabLayout.touchables.forEach { it.isEnabled = false }

        // Callback ketika halaman digeser
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val realPosition = position % introData.size

                // Pilih titik (dot) yang sesuai
                binding.tabLayout.getTabAt(realPosition)?.select()

                // Ubah teks tombol di halaman terakhir
                if (realPosition == introData.size - 1) {
                    binding.btnNext.text = getString(R.string.Mulai_Sekarang)
                } else {
                    binding.btnNext.text = getString(R.string.Selanjutnya)
                }

                // Reset auto-scroll timer
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000)
            }
        })
    }

    private fun setupButtons() {
        // Logika Tombol Next
        binding.btnNext.setOnClickListener {
            val currentRealPosition = binding.viewPager.currentItem % introData.size
            if (currentRealPosition == introData.size - 1) {
                navigateToLogin()
            } else {
                binding.viewPager.currentItem += 1
            }
        }

        // Logika Tombol Skip (Terkoneksi ke Card pembungkusnya)
        binding.btnSkipCard.setOnClickListener {
            navigateToLogin()
        }
    }

    // Runnable untuk Auto-Swipe
    private val sliderRunnable = Runnable {
        binding.viewPager.currentItem += 1
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

    // Data Class
    data class IntroItem(val image: Int, val title: String, val desc: String)

    // --- ADAPTER MENGGUNAKAN VIEWBINDING ---
    inner class IntroAdapter(private val items: List<IntroItem>) :
        RecyclerView.Adapter<IntroAdapter.IntroViewHolder>() {

        // ViewHolder sekarang menerima parameter Binding, bukan View biasa
        inner class IntroViewHolder(val itemBinding: ItemIntroBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bind(item: IntroItem) {
                itemBinding.imgIntro.setImageResource(item.image)
                itemBinding.tvTitle.text = item.title
                itemBinding.tvDesc.text = item.desc
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
            // Inflate Binding untuk Item Intro
            val itemBinding = ItemIntroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return IntroViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
            val realPosition = position % items.size
            holder.bind(items[realPosition])
        }

        override fun getItemCount(): Int = Int.MAX_VALUE
    }
}