# 📘 Quorri - Proyek Pemrograman Mobile

Aplikasi kuis interaktif berbasis Android yang dikembangkan sebagai tugas proyek mata kuliah Pemrograman Mobile di Fakultas Ilmu Komputer dan Teknologi Informasi, Universitas Sumatera Utara.

---

##  Deskripsi

**Quorri** adalah aplikasi mobile yang dirancang untuk memberikan pengalaman belajar yang interaktif melalui kuis. Pengguna dapat mendaftar, masuk, memilih kuis dari berbagai kategori, dan menjawab serangkaian pertanyaan. Aplikasi ini terhubung langsung dengan Firebase untuk autentikasi pengguna dan manajemen data kuis (soal, jawaban, dan skor) secara *real-time* menggunakan Firestore.

---

## 👥 Anggota Kelompok

Proyek ini dikembangkan oleh tim dari Fasilkom-TI, Universitas Sumatera Utara:

| Nama | NIM | Peran |
| :--- | :--- | :--- |
| **Zoya Indah Permatasari Tampubolon** | `231402009` | Quality Assurance & Frontend Developer |
| **Rizki Hamdani** | `231402027` | Database Engineer & Backend Developer |
| **Gresia Angelina Siahaan** | `231402036` | UI/UX Designer & Frontend Developer |
| **Wan Guntar Alam Barus** | `231402060` | Database Engineer & Backend Developer |
| **Gabriella Shalisha Sembiring** | `231402085` | Project Manager & Frontend Developer |

---

## ✨ Fitur Utama

-   🔐 **Autentikasi Pengguna**: Sistem *Login* dan *Register* yang aman menggunakan **Firebase Auth**.
-   📚 **Daftar Kuis**: Menampilkan daftar kuis yang tersedia yang diambil dari **Firebase Firestore**.
-   ❓ **Sesi Kuis Interaktif**: Antarmuka pengerjaan kuis dengan pertanyaan pilihan ganda.
-   📊 **Manajemen Skor**: Penghitungan skor otomatis setelah kuis selesai dan penyimpanan riwayat skor (jika diimplementasikan).
-   🎨 **UI Modern**: Didesain menggunakan **Material Design 3** untuk pengalaman pengguna yang bersih dan modern.

---

## 🛠️ Development Environment (Tech Stack)

Proyek ini dibangun menggunakan teknologi berikut:

-   **Target SDK**: API Level 36 Baklava Android 16.0
- **Min SDK**: API Level 24 Nougat Android 7.0
-   **IDE**: Android Studio Narwhal 2025.1.3
-   **Database**: Firebase Firestore
-   **Authentication**: Firebase Auth
-   **UI Framework**: Material Design 3 / XML Layout
-   **Version Control**: Git & GitHub
-   **Manajemen Dependensi**: Gradle 8.13

---

## 🚀 Cara Menjalankan Proyek

Ikuti langkah-langkah ini untuk menjalankan proyek di lingkungan pengembangan lokal Anda.

### 1. Prasyarat

-   [Android Studio](https://developer.android.com/studio) 
-   [Git](https://git-scm.com/)
-   Akun Google untuk mengakses Firebase (SOON)

### 2. Instalasi

1.  **Clone repository** ini ke mesin lokal Anda:
    ```bash
    git clone https://github.com/shalishagabriellaa/Quiz_App.git
    ```

2.  **Buka proyek** di Android Studio:
    -   Buka Android Studio.
    -   Pilih `Open` dan arahkan ke folder proyek yang baru saja Anda clone.
    -   Tunggu hingga proses *Gradle Sync* selesai.

