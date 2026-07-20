# 🏃‍♂️✨ SEHATIN – Aplikasi Pelacak Kesehatan & Kebugaran Gamifikasi

**SEHATIN** adalah aplikasi Android yang dirancang dengan elemen **Gamifikasi** yang kuat untuk memotivasi pengguna agar lebih disiplin dalam menerapkan gaya hidup sehat. Melalui aplikasi ini, pengguna dapat menyelesaikan misi, mengumpulkan poin, dan mengkustomisasi avatar mereka.

> 📘 **Catatan:** Proyek ini merupakan *proyek Tugas Akhir (Skripsi)* program studi Informatika. Aplikasi ini berjalan sepenuhnya secara **Offline (Local Storage)** tanpa memerlukan koneksi API eksternal, sehingga menjamin privasi, keamanan data, dan kecepatan akses bagi pengguna.

---

## 👥 Pengerjaan Proyek

Proyek ini dikerjakan secara individu oleh mahasiswa:

| Nama                     | NIM       |
| ------------------------ | --------- |
| Muhammad Rafli Nurfathan | 221111009 |

---

## 📱 Fitur Utama

* ✅ **Kalkulator BMI Pintar**
  Menghitung Indeks Massa Tubuh (BMI) secara akurat dengan opsi klasifikasi standar Asia maupun Internasional (WHO).

* ✅ **Sistem Gamifikasi & Papan Skor**
  Pengguna dapat menyelesaikan misi harian untuk mendapatkan Poin dan EXP, serta meraih lencana kebanggaan seperti *Master Plank*, *Pejuang Push Up*, hingga *Sultan Poin*.

* ✅ **Toko & Inventaris Kustomisasi**
  Tukarkan poin yang didapat dengan *item* eksklusif, seperti pakaian karakter (Elite/Rare) dan latar belakang ruangan yang estetik.

* ✅ **Tantangan Kebugaran Terintegrasi**
  Modul pelacak aktivitas fisik untuk latihan tubuh seperti Push Up dan Plank.

* ✅ **Kuis Edukasi Nutrisi**
  Tingkatkan pemahaman tentang makanan sehat dengan cara yang interaktif dan menyenangkan.

* ✅ **Audio Dinamis & Pengingat**
  Dilengkapi sistem alarm pengingat waktu lokal, *Background Music* (BGM), dan *Sound Effects* (SFX) interaktif (*Zero-Delay* menggunakan SoundPool).

---

## 🗃️ Teknologi & Arsitektur

| Komponen           | Teknologi                           |
| ------------------ | ------------------------------------ |
| Bahasa Pemrograman  | Kotlin                              |
| IDE                 | Android Studio                      |
| Pola Arsitektur     | MVVM (*Model-View-ViewModel*)       |
| Penyimpanan Lokal   | DataStore & SharedPreferences       |
| UI Design           | XML Layouts & Material Design       |
| Manajemen Audio     | MediaPlayer (BGM) & SoundPool (SFX) |

---

## 🚀 Cara Menjalankan Proyek

1. **Clone Repositori:**

   ```bash
   git clone https://github.com/username-anda/sehatin.git
   ```

2. Buka folder proyek menggunakan **Android Studio**.
3. Tunggu hingga proses *Gradle Sync* selesai.
4. Tekan tombol **Run** (`Shift + F10`) untuk menjalankan aplikasi di Emulator atau Perangkat Fisik.

> ⚠️ **Penting:** Sangat disarankan untuk menjalankan aplikasi di **Perangkat Android Fisik** agar fitur audio SFX dan Alarm Pengingat dapat diuji dan berjalan dengan maksimal.

---

## 📸 Tampilan Antarmuka

| Dashboard / Beranda                       | Toko Kustomisasi                      | Lencana Pencapaian                     |
| ------------------------------------------ | -------------------------------------- | ---------------------------------------- |
| ![Dashboard](docs/screenshots/dashboard.png) | ![Toko](docs/screenshots/toko.png)     | ![Lencana](docs/screenshots/lencana.png) |

> 💡 Simpan gambar tangkapan layar aplikasi di folder `docs/screenshots/` pada repositori, lalu sesuaikan nama file di atas agar gambar tampil dengan benar di GitHub.

---

## 📝 Catatan Tambahan

* Aplikasi ini tidak menggunakan API eksternal; semua *state* pencapaian dan data kepemilikan *item* disimpan secara **lokal**.
* Fokus utama pengembangan ada pada interaktivitas UI/UX dan keandalan sistem audio serta gamifikasi.

---

## 📌 Rencana Pengembangan

* [ ] Integrasi Database Cloud / Backend API
* [ ] Fitur *Leaderboard* antar pengguna (Online)
* [ ] Penambahan variasi misi dan item kustomisasi
* [ ] Mode pelacakan kalori harian yang lebih detail

---

## 🙌 Kontribusi

Kontribusi sangat terbuka! Silakan *fork* repository ini dan kirim *pull request*, atau ajukan masalah (*issues*) jika kamu menemukan *bug* atau punya saran fitur baru.

---

## 📲 Kontak Pengembang

* 👤 Nama: **Muhammad Rafli Nurfathan**
* 📧 Email: [nurfathanrafli85@gmail.com](mailto:nurfathanrafli85@gmail.com)
* 🔗 LinkedIn: [linkedin.com/in/mhmmdraflin](https://www.linkedin.com/in/mhmmdraflin)
