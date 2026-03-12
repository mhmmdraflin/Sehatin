package com.example.sehatin.ui.SideFeature

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sehatin.Adapter.MakananAdapter
import com.example.sehatin.Adapter.MakananSehat
import com.example.sehatin.R
import com.google.android.material.card.MaterialCardView

class Informasi_MakananActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informasi_makanan)

        val btnBack = findViewById<MaterialCardView>(R.id.btn_back)
        btnBack.setOnClickListener { finish() }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val rvMakanan = findViewById<RecyclerView>(R.id.rv_makanan)

        // DATA DUMMY MAKANAN SEHAT
        val daftarMakanan = listOf(
            MakananSehat(
                judul = "Dada Ayam Panggang",
                kalori = 165,
                deskripsiLengkap = "Dada ayam tanpa kulit panggang adalah sumber protein tanpa lemak terbaik. Sangat cocok untuk pembentukan otot karena mengandung 31g protein per 100g porsi.",
                gambarUrl = "https://images.unsplash.com/photo-1598514982205-f36b96d1e8d4?q=80&w=500&auto=format&fit=crop"
            ),
            MakananSehat(
                judul = "Oatmeal Buah Berry",
                kalori = 250,
                deskripsiLengkap = "Gandum utuh kaya serat yang menjaga perut kenyang lebih lama. Ditambah dengan antioksidan dari buah berry, menjadikannya menu sarapan juara.",
                gambarUrl = "https://images.unsplash.com/photo-1517673132405-a56a62b18caf?q=80&w=500&auto=format&fit=crop"
            ),
            MakananSehat(
                judul = "Telur Rebus Matang",
                kalori = 78,
                deskripsiLengkap = "Satu butir telur rebus mengandung 6g protein berkualitas tinggi dan asam amino esensial. Sangat praktis sebagai camilan sehat sehabis olahraga.",
                gambarUrl = "https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?q=80&w=500&auto=format&fit=crop"
            ),
            MakananSehat(
                judul = "Sup Kacang Merah",
                kalori = 180,
                deskripsiLengkap = "Kacang merah sangat kaya akan karbohidrat kompleks dan serat pencernaan. Cocok sebagai menu makan malam hangat tanpa membuat perut buncit.",
                gambarUrl = "https://images.unsplash.com/photo-1548943487-a2e4b43b6858?q=80&w=500&auto=format&fit=crop"
            ),
            MakananSehat(
                judul = "Ikan Salmon Bakar",
                kalori = 208,
                deskripsiLengkap = "Kaya akan Asam Lemak Omega-3 yang sangat baik untuk kesehatan jantung dan otak. Mengandung lemak sehat yang dibutuhkan tubuh.",
                gambarUrl = "https://images.unsplash.com/photo-1485921325833-c519f76c4927?q=80&w=500&auto=format&fit=crop"
            ),
            MakananSehat(
                judul = "Almond Panggang",
                kalori = 164,
                deskripsiLengkap = "Kacang almond mengandung Vitamin E dan Magnesium tingkat tinggi. Makan segenggam almond bisa menekan rasa lapar di sore hari.",
                gambarUrl = "https://images.unsplash.com/photo-1508061253366-f7da158b6d46?q=80&w=500&auto=format&fit=crop"
            )
        )

        val adapter = MakananAdapter(daftarMakanan) { makananTerpilih ->
            val intent = Intent(this, DetailInformasiMakananActivity::class.java).apply {
                putExtra("EXTRA_JUDUL", makananTerpilih.judul)
                putExtra("EXTRA_KALORI", makananTerpilih.kalori)
                putExtra("EXTRA_DESKRIPSI", makananTerpilih.deskripsiLengkap)
                // Kirim URL gambar ke halaman detail
                putExtra("EXTRA_GAMBAR_URL", makananTerpilih.gambarUrl)
            }
            startActivity(intent)
        }

        rvMakanan.layoutManager = LinearLayoutManager(this)
        rvMakanan.adapter = adapter
    }
}