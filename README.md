# Jejak Ibadah 🕌

**Catat langkahmu, jaga konsistensimu.**  
Aplikasi asisten ibadah harian berbasis Android yang ringan, bersih, dan bekerja 100% *offline*. Menggabungkan jadwal salat akurat, pengingat azan, arah kiblat, bacaan dzikir, dan pelacak kebiasaan ibadah (*streak*) yang ramah serta menjaga privasi pengguna.

> **Mata Kuliah:** Pengembangan Aplikasi Mobile  
> **Program Studi:** Teknik Informatika – UIN Sunan Gunung Djati Bandung  
> **Repositori:** [github.com/AdiSatriaPermana/Jejak-Ibadah](https://github.com/AdiSatriaPermana/Jejak-Ibadah.git)

---

## ✨ Fitur Utama (Versi 1.0)

* **Jadwal Salat & Pengingat Azan:** Perhitungan waktu salat lokal (*offline*) menggunakan metode Kemenag RI, hitung mundur waktu salat berikutnya, serta alarm azan tepat waktu (*Exact Alarm*).
* **Pelacak Ibadah & Streak Ramah:** Pencatatan ibadah harian (*Tepat waktu*, *Terlambat*, atau *Jeda hari ini*), perhitungan *streak* per jenis ibadah, dan *heatmap* bulanan tanpa elemen kompetisi/penghakiman.
* **Dzikir & Tasbih Digital:** Kumpulan dzikir pagi, petang, setelah salat, dan doa harian lengkap dengan teks Arab, latin, terjemahan, serta rujukan sumber terverifikasi.
* **Kompas Arah Kiblat:** Penunjuk arah kiblat berbasis sensor magnetometer perangkat dan perhitungan koordinat GPS.
* **Tema Adaptif:** Mendukung **Mode Terang** (*Warm Sand & Deep Teal*) dan **Mode Gelap** (*Midnight Teal & Soft Gold*).

---

## 🛠️ Tech Stack & Arsitektur

* **Bahasa:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3)
* **Arsitektur:** MVVM (*Model-View-ViewModel*)
* **Navigasi:** Navigation Compose
* **Database Lokal:** Room Database (SQLite) & Jetpack DataStore
* **Layanan Latar Belakang & Lokasi:** AlarmManager, WorkManager, Fused Location Provider
* **Perhitungan Astronomi:** Pustaka `Adhan` (`com.batoulapps.adhan:adhan2`)

---

## 📂 Struktur Proyek

```text
app/src/main/
├── assets/
│   └── dzikir_data.json              # Data bacaan dzikir & doa harian
├── res/
│   └── raw/                          # File audio azan (azan_subuh, azan_umum)
└── java/lat/pam/jejakibadah/
    ├── MainActivity.kt               # Entry point aplikasi
    ├── data/
    │   ├── local/                    # AppDatabase, DAO, dan 4 Entity Room
    │   ├── preferences/              # Pengaturan lokal via DataStore
    │   └── repository/               # IbadahRepository
    ├── domain/                       # Logika perhitungan streak, waktu salat & kiblat
    ├── service/
    │   ├── alarm/                    # Penjadwalan & penerima notifikasi azan
    │   └── location/                 # Pengambilan koordinat GPS
    └── ui/
        ├── theme/                    # Konfigurasi warna & tipografi (Light/Dark)
        ├── navigation/               # Rute & NavGraph Bottom Navigation
        ├── components/               # Komponen UI reusable
        └── screens/                  # Layar Beranda, Jejak, Dzikir, Kiblat & Pengaturan