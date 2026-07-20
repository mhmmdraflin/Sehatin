package com.example.sehatin.ui.SideFeature

import android.content.Intent
import android.media.AudioAttributes // IMPORT AUDIO ATTRIBUTES
import android.media.SoundPool       // IMPORT SOUNDPOOL
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sehatin.Adapter.MakananAdapter
import com.example.sehatin.Adapter.MakananSehat
import com.example.sehatin.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText

class Informasi_MakananActivity : AppCompatActivity() {

    private lateinit var rvMakanan: RecyclerView
    private lateinit var etSearch: TextInputEditText

    // Variabel untuk menyimpan data asli secara utuh (A-Z)
    private var daftarMakananLengkap = listOf<MakananSehat>()

    // VARIABEL SOUNDPOOL (Untuk Suara Zero-Delay)
    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informasi_makanan)

        // =======================================================
        // INISIALISASI SOUNDPOOL
        // =======================================================
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3) // Maksimal putar 3 suara bersamaan jika diklik cepat
            .setAudioAttributes(audioAttributes)
            .build()

        // Load suara ke memori sejak awal Activity dibuka
        clickSoundId = soundPool?.load(this, R.raw.tombol_klik_sehatin, 1) ?: 0
        // =======================================================

        val btnBack = findViewById<MaterialCardView>(R.id.btn_back)
        btnBack.setOnClickListener {
            mainkanSuaraKlik() // MEMAINKAN SUARA KLIK
            finish()
        }

        rvMakanan = findViewById(R.id.rv_makanan)
        etSearch = findViewById(R.id.et_search)

        rvMakanan.layoutManager = LinearLayoutManager(this)

        // 1. Muat semua data makanan ke dalam variabel
        inisialisasiDataMakanan()

        // 2. Tampilkan semua data utuh saat halaman pertama kali dibuka
        tampilkanData(daftarMakananLengkap)

        // 3. LOGIKA PENCARIAN: Memantau setiap huruf yang diketik oleh user
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ambil teks yang diketik pengguna dan hilangkan spasi berlebih
                val keyword = s.toString().trim()
                filterMakanan(keyword)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // ========================================================
    // FUNGSI UNTUK MEMAINKAN SUARA KLIK (ZERO-DELAY)
    // ========================================================
    private fun mainkanSuaraKlik() {
        soundPool?.play(clickSoundId, 1f, 1f, 0, 0, 1f)
    }

    // Fungsi untuk menyaring data berdasarkan kata kunci (Keyword)
    private fun filterMakanan(keyword: String) {
        if (keyword.isEmpty()) {
            // Jika kolom pencarian dikosongkan, tampilkan kembali semua makanan
            tampilkanData(daftarMakananLengkap)
        } else {
            // Saring makanan yang judulnya mengandung kata kunci
            val dataDisaring = daftarMakananLengkap.filter { makanan ->
                makanan.judul.contains(keyword, ignoreCase = true)
            }
            tampilkanData(dataDisaring)
        }
    }

    // Fungsi untuk memperbarui RecyclerView dengan data yang baru
    private fun tampilkanData(data: List<MakananSehat>) {
        val adapter = MakananAdapter(data) { makananTerpilih ->

            mainkanSuaraKlik() // MEMAINKAN SUARA SAAT ITEM MAKANAN DIKLIK

            val intent = Intent(this, DetailInformasiMakananActivity::class.java).apply {
                putExtra("EXTRA_JUDUL", makananTerpilih.judul)
                putExtra("EXTRA_KALORI", makananTerpilih.kalori)
                putExtra("EXTRA_DESKRIPSI", makananTerpilih.deskripsiLengkap)
                putExtra("EXTRA_GAMBAR_URL", makananTerpilih.gambarUrl)
            }
            startActivity(intent)
        }
        rvMakanan.adapter = adapter
    }

    // Fungsi berisi daftar lengkap makanan A-Z
    private fun inisialisasiDataMakanan() {
        daftarMakananLengkap = listOf(
            MakananSehat(
                judul = "Apel Merah",
                kalori = 52,
                deskripsiLengkap = getString(R.string.desc_apel),
                gambarUrl = getString(R.string.url_apel)
            ),
            MakananSehat(
                judul = "Brokoli Rebus",
                kalori = 34,
                deskripsiLengkap = getString(R.string.desc_brokoli),
                gambarUrl = getString(R.string.url_brokoli)
            ),
            MakananSehat(
                judul = "Chia Seed",
                kalori = 137, // per 1 oz
                deskripsiLengkap = getString(R.string.desc_chia_seed),
                gambarUrl = getString(R.string.url_chia_seed)
            ),
            MakananSehat(
                judul = "Dada Ayam Panggang",
                kalori = 165,
                deskripsiLengkap = getString(R.string.desc_dada_ayam),
                gambarUrl = getString(R.string.url_dada_ayam)
            ),
            MakananSehat(
                judul = "Edamame Rebus",
                kalori = 121,
                deskripsiLengkap = getString(R.string.desc_edamame),
                gambarUrl = getString(R.string.url_edamame)
            ),
            MakananSehat(
                judul = "Fillet Ikan Kakap",
                kalori = 92,
                deskripsiLengkap = getString(R.string.desc_ikan_kakap),
                gambarUrl = getString(R.string.url_ikan_kakap)
            ),
            MakananSehat(
                judul = "Gandum Utuh (Oatmeal)",
                kalori = 68,
                deskripsiLengkap = getString(R.string.desc_gandum),
                gambarUrl = getString(R.string.url_gandum)
            ),
            MakananSehat(
                judul = "Hati Ayam Rebus",
                kalori = 116,
                deskripsiLengkap = getString(R.string.desc_hati_ayam),
                gambarUrl = getString(R.string.url_hati_ayam)
            ),
            MakananSehat(
                judul = "Ikan Salmon",
                kalori = 208,
                deskripsiLengkap = getString(R.string.desc_ikan_salmon),
                gambarUrl = getString(R.string.url_ikan_salmon)
            ),
            MakananSehat(
                judul = "Jagung Manis Rebus",
                kalori = 86,
                deskripsiLengkap = getString(R.string.desc_jagung),
                gambarUrl = getString(R.string.url_jagung)
            ),
            MakananSehat(
                judul = "Kacang Almond",
                kalori = 579,
                deskripsiLengkap = getString(R.string.desc_kacang_almond),
                gambarUrl = getString(R.string.url_kacang_almond)
            ),
            MakananSehat(
                judul = "Labu Siam Rebus",
                kalori = 19,
                deskripsiLengkap = getString(R.string.desc_labu_siam),
                gambarUrl = getString(R.string.url_labu_siam)
            ),
            MakananSehat(
                judul = "Mangga Harum Manis",
                kalori = 60,
                deskripsiLengkap = getString(R.string.desc_mangga),
                gambarUrl = getString(R.string.url_mangga)
            ),
            MakananSehat(
                judul = "Nasi Merah",
                kalori = 110,
                deskripsiLengkap = getString(R.string.desc_nasi_merah),
                gambarUrl = getString(R.string.url_nasi_merah)
            ),
            MakananSehat(
                judul = "Oatmeal Buah Berry",
                kalori = 250,
                deskripsiLengkap = getString(R.string.desc_oatmeal),
                gambarUrl = getString(R.string.url_oatmeal)
            ),
            MakananSehat(
                judul = "Pisang Ambon",
                kalori = 105,
                deskripsiLengkap = getString(R.string.desc_pisang),
                gambarUrl = getString(R.string.url_pisang)
            ),
            MakananSehat(
                judul = "Quinoa",
                kalori = 120,
                deskripsiLengkap = getString(R.string.desc_quinoa),
                gambarUrl = getString(R.string.url_quinoa)
            ),
            MakananSehat(
                judul = "Roti Gandum",
                kalori = 247,
                deskripsiLengkap = getString(R.string.desc_roti_gandum),
                gambarUrl = getString(R.string.url_roti_gandum)
            ),
            MakananSehat(
                judul = "Susu Kedelai (Tanpa Gula)",
                kalori = 54,
                deskripsiLengkap = getString(R.string.desc_susu_kedelai),
                gambarUrl = getString(R.string.url_susu_kedelai)
            ),
            MakananSehat(
                judul = "Telur Rebus Matang",
                kalori = 78,
                deskripsiLengkap = getString(R.string.desc_telur_rebus),
                gambarUrl = getString(R.string.url_telur_rebus)
            ),
            MakananSehat(
                judul = "Ubi Jalar Rebus",
                kalori = 86,
                deskripsiLengkap = getString(R.string.desc_ubi_jalar),
                gambarUrl = getString(R.string.url_ubi_jalar)
            ),
            MakananSehat(
                judul = "Virgin Coconut Oil (VCO)",
                kalori = 120, // per sendok makan
                deskripsiLengkap = getString(R.string.desc_vco),
                gambarUrl = getString(R.string.url_vco)
            ),
            MakananSehat(
                judul = "Wortel Rebus",
                kalori = 41,
                deskripsiLengkap = getString(R.string.desc_wortel),
                gambarUrl = getString(R.string.url_wortel)
            ),
            MakananSehat(
                judul = "Xigua (Semangka)",
                kalori = 30,
                deskripsiLengkap = getString(R.string.desc_semangka),
                gambarUrl = getString(R.string.url_semangka)
            ),
            MakananSehat(
                judul = "Yoghurt Plain",
                kalori = 59,
                deskripsiLengkap = getString(R.string.desc_yoghurt),
                gambarUrl = getString(R.string.url_yoghurt)
            ),
            MakananSehat(
                judul = "Zucchini Panggang",
                kalori = 17,
                deskripsiLengkap = getString(R.string.desc_zucchini),
                gambarUrl = getString(R.string.url_zucchini)
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        // Bersihkan memori SoundPool
        soundPool?.release()
        soundPool = null
    }
}