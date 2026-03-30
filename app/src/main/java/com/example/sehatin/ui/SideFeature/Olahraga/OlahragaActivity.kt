package com.example.sehatin.ui.SideFeature.Olahraga

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sehatin.R
import com.google.android.material.card.MaterialCardView

class OlahragaActivity : AppCompatActivity() {

    private lateinit var viewModel: OlahragaViewModel
    private lateinit var adapter: OlahragaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_olahraga)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<MaterialCardView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // ==========================================
        // INISIALISASI MVVM OLAHRAGA
        // ==========================================
        val pref = OlahragaPreferences.getInstance(applicationContext.dataStore)
        val factory = OlahragaViewModelFactory(OlahragaRepository(pref))
        viewModel = ViewModelProvider(this, factory)[OlahragaViewModel::class.java]

        setupDaftarOlahraga()
    }

    private fun setupDaftarOlahraga() {
        val rvOlahraga = findViewById<RecyclerView>(R.id.rv_olahraga)

        // MASUKKAN TAKARAN EXP MASING-MASING DI SINI (Angka ke-5)
        val daftarGerakan = listOf(
            GerakanOlahraga(1, "Push Up", 30, 15, 10, R.drawable.ic_pushup, R.raw.push_up_illustration),
            GerakanOlahraga(2, "Plank", 45, 20, 15, R.drawable.ic_pushup, R.raw.plank_illustration),
            GerakanOlahraga(3, "Sit Up", 30, 12, 10, R.drawable.ic_pushup, R.raw.sit_up_illustration),
            GerakanOlahraga(4, "Squat", 40, 25, 20, R.drawable.ic_pushup, R.raw.squat_illustration),
            GerakanOlahraga(5, "Lunges", 30, 18, 15, R.drawable.ic_pushup, R.raw.lunges_illustration),
            GerakanOlahraga(6, "Bicycle Crunch", 40, 22, 20, R.drawable.ic_pushup, R.raw.bicycle_crunch_illustration),
            GerakanOlahraga(7, "Leg Raise", 35, 15, 15, R.drawable.ic_pushup, R.raw.leg_raise_illustration)
        )

        adapter = OlahragaAdapter(daftarGerakan) { gerakanTerpilih ->

            // Pindah ke layar Timer (SesiOlahragaActivity) dengan membawa bekal data
            val intent = Intent(this, SesiOlahragaActivity::class.java).apply {
                putExtra("EXTRA_ID_GERAKAN", gerakanTerpilih.idGerakan)
                putExtra("EXTRA_NAMA_GERAKAN", gerakanTerpilih.namaGerakan)
                putExtra("EXTRA_DURASI", gerakanTerpilih.durasiDetik)
                putExtra("EXTRA_KALORI", gerakanTerpilih.kaloriTerbakar)

                // KIRIM EXP ASLI KE SESI OLAHRAGA
                putExtra("EXTRA_EXP_DIDAPAT", gerakanTerpilih.expDidapat)

                putExtra("EXTRA_GIF_FILE", gerakanTerpilih.gifResourceFile)
            }
            startActivity(intent)
        }

        rvOlahraga.layoutManager = LinearLayoutManager(this)
        rvOlahraga.adapter = adapter

        // FITUR STATUS SELESAI DIMATIKAN SESUAI PERMINTAAN
        // Tombol sekarang selalu menampilkan teks statis "Lakukan"
        // viewModel.getCompletedGerakanIds().observe(this) { completedIds ->
        //     adapter.setCompletedGerakan(completedIds)
        // }
    }
}