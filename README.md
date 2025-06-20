# 🚗 Sistem Booking Servis Bengkel Mobil

Selamat datang di proyek Final Project Basis Data kami!  
Repositori ini berisi implementasi sistem reservasi dan manajemen layanan bengkel mobil berbasis Java & MySQL.

🛠️ Dibangun menggunakan:

- Java (CLI Interface)
- MySQL (Stored Procedure, View, Trigger, Report)
- DBeaver (untuk pengelolaan database)

---

## 🔧 Fitur Utama

### 1. 🗖 Booking Servis Mobil
Pelanggan dapat melakukan pemesanan servis berdasarkan tanggal, jam, jenis servis, dan teknisi yang tersedia.

Contoh data:
```plaintext
Nama Pelanggan: Mahendra
Mobil: Ferrari LaFerrari SVJ 50
Servis: Ganti Oli Mesin
Tanggal: 2025-06-20, Jam: 08:00
```

### 2. 🧑‍💼 Manajemen Teknisi Otomatis (Trigger)
Trigger SQL otomatis mengubah status teknisi:
- Saat booking "aktif" ditambahkan → teknisi menjadi "sibuk"
- Saat servis selesai dan teknisi tidak punya job aktif → status kembali "tersedia"

✅ Tidak perlu atur manual!

### 3. ⏱ Estimasi Waktu Servis (Stored Procedure)
Menghitung total waktu servis dari seluruh layanan yang dipesan dalam satu booking menggunakan prosedur `estimasi_waktu_servis`.

Contoh:
```sql
CALL estimasi_waktu_servis(26, @hasil);
SELECT @hasil; -- Output: 570 (menit)
```

### 4. 📊 Laporan (Reporting System)

Tersedia 3 laporan menggunakan SQL lanjutan:

- ✅ CTE: Total jumlah pemesanan berdasarkan jenis servis
- ✅ Subquery: Teknisi yang menangani ≥ 3 servis dalam minggu tertentu
- ✅ CrossTab: Rekap jumlah booking berdasarkan status (aktif, selesai, dibatalkan)

Contoh (CTE):
```sql
WITH total_servis AS (
  SELECT id_jenis_servis, COUNT(*) AS jumlah
  FROM detail_servis
  GROUP BY id_jenis_servis
)
SELECT js.nama_servis, jumlah FROM total_servis ts
JOIN jenis_servis js ON js.id_jenis_servis = ts.id_jenis_servis;
```

### 5. 👁 View: v_booking_aktif
Menampilkan data gabungan booking yang statusnya "aktif", lengkap dengan nama pelanggan, teknisi, dan layanan yang dipesan.

Contoh hasil:
| Pelanggan | Mobil        | Servis           | Teknisi | Tanggal  |
|-----------|--------------|------------------|---------|----------|
| Mahendra  | McLaren 720s | Servis + Bodykit | Hizkia  | 2025-06-20 |

Dapat diakses dari Java CLI melalui `BookingAktifView.java`

---

## 🗃 Struktur Database

Entity utama:
- `user` (admin/pelanggan)
- `mobil`
- `teknisi`
- `jenis_servis`
- `booking_servis`
- `detail_servis`

Relasi: One-to-many antar `mobil` → `user`, dan `detail_servis` → `booking_servis` + `jenis_servis`

Disertai:
- Foreign key constraint
- Trigger
- View
- Stored Procedure
- Contoh data dummy

---

## 🚀 Cara Menjalankan

1. Import file SQL: `dump-bengkel_mobil.sql` ke MySQL (bisa pakai DBeaver)
2. Jalankan CLI Java dari file `Main.java`
3. Lakukan reservasi, lihat view, dan coba fitur laporan

---

## 👥 Tim Pengembang

Kelompok 6 - Proyek Basis Data  
Mahasiswa Universitas Pembangunan Nasional "Veteran" Jawa Timur  

- 24082010079 Mahendra Stevioly Putra  
- 24082010069 Rosalinda Eka Hernalia  
- 24082010053 An Nisa’ Fatmawati  
- 24082010076 Andrey Parinding
