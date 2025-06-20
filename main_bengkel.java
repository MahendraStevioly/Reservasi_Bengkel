import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Kelas utama aplikasi Bengkel Mobil
// Menyediakan menu admin dan pelanggan untuk mengelola user, mobil, teknisi, jenis servis, booking, dan laporan
// Dibawah ini adalah struktur utama dan penjelasan setiap metode
public class main_bengkel {
    private static Scanner scanner = new Scanner(System.in);
    private static userDAO userDAO = new userDAO();
    private static mobilDAO mobilDAO = new mobilDAO();
    private static booking_servisDAO bookingDAO = new booking_servisDAO();
    private static jenis_servisDAO jenisServisDAO = new jenis_servisDAO();
    private static teknisiDAO teknisiDAO = new teknisiDAO();
    private static detail_servisDAO detailServisDAO = new detail_servisDAO();
    private static User currentUser = null;

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== BENGKEL MOBIL ===");
            System.out.println("1. Login");
            System.out.println("2. Keluar");
            System.out.print("Pilih menu: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    running = false;
                    System.out.println("Terima kasih telah menggunakan aplikasi!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void login() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Use optimized authentication method
        User user = userDAO.authenticateUser(username, password);
        if (user != null) {
            currentUser = user;
            if (user.getrole() == User.Role.ADMIN) {
                System.out.println("Login berhasil! Selamat datang, Admin.");
                adminMenu();
            } else if (user.getrole() == User.Role.PELANGGAN) {
                System.out.println("Login berhasil! Selamat datang, " + user.getnama() + ".");
                if (password.trim().isEmpty()) {
                    System.out.println("Info: Anda login tanpa password. Untuk keamanan, sebaiknya set password.");
                }
                menuPelanggan(scanner, currentUser);
            }
        } else {
            System.out.println("Login gagal! Username atau password salah.");
            System.out.println("Catatan: Pelanggan dapat login tanpa password jika belum diset.");
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n=== MENU ADMIN ===");
            System.out.println("1. Kelola User");
            System.out.println("2. Kelola Mobil");
            System.out.println("3. Kelola Teknisi");
            System.out.println("4. Kelola Jenis Servis");
            System.out.println("5. Kelola Booking");
            System.out.println("6. Riwayat Transaksi");
            System.out.println("7. Logout");
            System.out.print("Pilih menu (1-7): ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (pilihan) {
                case 1:
                    kelolaUser(scanner);
                    break;
                case 2:
                    kelolaMobil(scanner);
                    break;
                case 3:
                    kelolaTeknisi(scanner);
                    break;
                case 4:
                    kelolaJenisServis(scanner);
                    break;
                case 5:
                    kelolaBooking(scanner);
                    break;
                case 6:
                    lihatRiwayatTransaksi();
                    try (Connection conn = DBConnection.getConnection()) {
                        report.tampilkanLaporanServisTerjual(conn);
                    } catch (Exception e) {
                        System.out.println("Gagal menampilkan laporan servis terjual: " + e.getMessage());
                    }
                    break;
                case 7:
                    try (Connection conn = DBConnection.getConnection()) {
                        report.tampilkanRingkasanStatusBooking(conn);
                    } catch (Exception e) {
                        System.out.println("Gagal menampilkan laporan ringkasan status booking: " + e.getMessage());
                    }
                    break;
                case 8:
                    System.out.println("Logout berhasil!");
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void menuPelanggan(Scanner scanner, User user) {
        while (true) {
            System.out.println("\n=== MENU PELANGGAN ===");
            System.out.println("1. Lihat Mobil Saya");
            System.out.println("2. Tambah Mobil");
            System.out.println("3. Tambah Booking");
            System.out.println("4. Lihat Riwayat Booking");
            System.out.println("5. Batalkan Booking");
            System.out.println("6. Logout");
            System.out.print("Pilih menu (1-6): ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (pilihan) {
                case 1:
                    lihatMobilPelanggan(user);
                    break;
                case 2:
                    tambahMobilPelanggan(scanner, user);
                    break;
                case 3:
                    // Check if user has any cars
                    List<mobil> mobilList = mobilDAO.readMobilByUserId(user.getidUser());
                    if (mobilList.isEmpty()) {
                        System.out.println("\nAnda belum memiliki mobil yang terdaftar!");
                        System.out.println("Silakan tambahkan mobil terlebih dahulu.");
                        System.out.print("Apakah Anda ingin menambahkan mobil sekarang? (y/n): ");
                        String jawaban = scanner.nextLine().toLowerCase();
                        if (jawaban.equals("y")) {
                            tambahMobilPelanggan(scanner, user);
                        }
                    } else {
                        tambahBookingPelanggan(scanner, user);
                    }
                    break;
                case 4:
                    lihatRiwayatBookingPelanggan(user);
                    break;
                case 5:
                    batalkanBookingPelanggan(scanner, user);
                    break;
                case 6:
                    System.out.println("Logout berhasil!");
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void kelolaUser(Scanner scanner) {
        while (true) {
            System.out.println("\n=== KELOLA USER ===");
            System.out.println("1. Lihat Semua User");
            System.out.println("2. Tambah User");
            System.out.println("3. Update Password User");
            System.out.println("4. Hapus User");
            System.out.println("5. Kembali");
            System.out.print("Pilih menu (1-5): ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (pilihan) {
                case 1:
                    lihatSemuaUser();
                    break;
                case 2:
                    tambahUser(scanner);
                    break;
                case 3:
                    updatePasswordUser(scanner);
                    break;
                case 4:
                    hapusUser(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void lihatSemuaUser() {
        System.out.println("\n=== DAFTAR USER ===");
        List<User> users = userDAO.readAllUser();
        if (users.isEmpty()) {
            System.out.println("Belum ada data user.");
            return;
        }
        
        // Prepare data for formatted table
        String[] headers = {"ID", "Username", "Role", "Nama", "No Telepon"};
        String[][] data = new String[users.size()][5];
        
        int rowIndex = 0;
        for (User u : users) {
            data[rowIndex][0] = String.valueOf(u.getidUser());
            data[rowIndex][1] = u.getusername();
            data[rowIndex][2] = u.getrole().toString();
            data[rowIndex][3] = u.getnama();
            data[rowIndex][4] = u.getno_tlp();
            rowIndex++;
        }
        
        printFormattedTable(headers, data);
    }

    private static void tambahUser(Scanner scanner) {
        System.out.println("\n=== TAMBAH USER ===");
        
        // Validate username uniqueness
        String username;
        do {
            System.out.print("Masukkan username: ");
            username = scanner.nextLine();
            if (userDAO.isUsernameExists(username)) {
                System.out.println("Username sudah ada. Silakan pilih username lain.");
            }
        } while (userDAO.isUsernameExists(username));
        
        System.out.print("Masukkan password: ");
        String password = scanner.nextLine();
        
        // Validate role input
        String roleStr;
        do {
            System.out.print("Masukkan role (admin/pelanggan): ");
            roleStr = scanner.nextLine();
            if (!roleStr.equalsIgnoreCase("admin") && !roleStr.equalsIgnoreCase("pelanggan")) {
                System.out.println("Role tidak valid. Masukkan 'admin' atau 'pelanggan'.");
            }
        } while (!roleStr.equalsIgnoreCase("admin") && !roleStr.equalsIgnoreCase("pelanggan"));
        
        System.out.print("Masukkan nama: ");
        String nama = scanner.nextLine();
        System.out.print("Masukkan no telepon: ");
        String noTlp = scanner.nextLine();
        
        User.Role role = roleStr.equalsIgnoreCase("admin") ? User.Role.ADMIN : User.Role.PELANGGAN;
        User newUser = new User(username, password, role, nama, noTlp);
        
        try {
            userDAO.createuser(newUser);
            System.out.println("User berhasil ditambahkan!");
        } catch (SQLException e) {
            System.out.println("Gagal menambahkan user: " + e.getMessage());
        }
    }

    private static void updatePasswordUser(Scanner scanner) {
        System.out.println("\n=== UPDATE PASSWORD USER ===");
        System.out.print("Masukkan username: ");
        String username = scanner.nextLine();
        System.out.print("Masukkan password baru: ");
        String newPassword = scanner.nextLine();
        
        userDAO.updateUser(username, newPassword);
    }

    private static void hapusUser(Scanner scanner) {
        System.out.println("\n=== HAPUS USER ===");
        lihatSemuaUser();
        int idUser = getValidInteger(scanner, "\nMasukkan ID user yang ingin dihapus: ");
        userDAO.deleteUser(idUser);
    }

    private static void kelolaMobil(Scanner scanner) {
        while (true) {
            System.out.println("\n=== KELOLA MOBIL ===");
            System.out.println("1. Lihat Data Mobil Pelanggan");
            System.out.println("2. Hapus Mobil Pelanggan");
            System.out.println("3. Kembali");
            System.out.print("Pilih menu (1-3): ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (pilihan) {
                case 1:
                    lihatDataMobilPelanggan();
                    break;
                case 2:
                    hapusMobilPelanggan(scanner);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void lihatDataMobilPelanggan() {
        System.out.println("\n=== DATA MOBIL PELANGGAN ===");
        List<mobil> allMobil = mobilDAO.readAllMobilWithUserInfo();
        if (allMobil.isEmpty()) {
            System.out.println("Belum ada data mobil.");
            return;
        }
        
        // Prepare data for formatted table
        String[] headers = {"ID", "Merk", "Tipe", "Tahun", "ID Pelanggan"};
        String[][] data = new String[allMobil.size()][5];
        
        int rowIndex = 0;
        for (mobil m : allMobil) {
            data[rowIndex][0] = String.valueOf(m.getidMobil());
            data[rowIndex][1] = m.getmerk();
            data[rowIndex][2] = m.gettipe();
            data[rowIndex][3] = String.valueOf(m.gettahun());
            data[rowIndex][4] = m.getfk_user() + " (" + (m.getOwnerName() != null ? m.getOwnerName() : "Unknown") + ")";
            rowIndex++;
        }
        
        printFormattedTable(headers, data);
    }

    private static void hapusMobilPelanggan(Scanner scanner) {
        System.out.println("\n=== HAPUS MOBIL PELANGGAN ===");
        List<mobil> allMobil = mobilDAO.readAllMobilWithUserInfo();
        if (allMobil.isEmpty()) {
            System.out.println("Belum ada data mobil.");
            return;
        }
        
        // Prepare data for formatted table
        String[] headers = {"ID", "Merk", "Tipe", "Tahun", "ID Pelanggan"};
        String[][] data = new String[allMobil.size()][5];
        
        int rowIndex = 0;
        for (mobil m : allMobil) {
            data[rowIndex][0] = String.valueOf(m.getidMobil());
            data[rowIndex][1] = m.getmerk();
            data[rowIndex][2] = m.gettipe();
            data[rowIndex][3] = String.valueOf(m.gettahun());
            data[rowIndex][4] = m.getfk_user() + " (" + (m.getOwnerName() != null ? m.getOwnerName() : "Unknown") + ")";
            rowIndex++;
        }
        
        printFormattedTable(headers, data);
        
        System.out.print("\nMasukkan ID mobil yang ingin dihapus: ");
        int idMobil = getValidInteger(scanner, "");
        
        try {
            mobilDAO.deleteMobil(idMobil);
            System.out.println("Mobil berhasil dihapus!");
        } catch (Exception e) {
            System.out.println("Gagal menghapus mobil: " + e.getMessage());
        }
    }

    private static void lihatRiwayatTransaksi() {
        System.out.println("\n=== RIWAYAT TRANSAKSI ===");
        List<booking_servis> bookings = bookingDAO.readCompletedTransactions();
        if (bookings.isEmpty()) {
            System.out.println("Belum ada data transaksi selesai.");
            return;
        }

        // Prepare data for formatted table
        String[] headers = {"No Transaksi", "Tanggal", "Jam", "Status", "Mobil", "Pelanggan", "Teknisi", "Total Bayar"};
        String[][] data = new String[bookings.size()][8];
        
        int totalIncome = 0;
        int rowIndex = 0;
        
        for (booking_servis bs : bookings) {
            String mobilInfo = (bs.getMerk() != null && bs.getTipe() != null) ? 
                              bs.getMerk() + " " + bs.getTipe() : "Unknown";
            String ownerName = (bs.getOwnerName() != null) ? bs.getOwnerName() : "Unknown";
            String teknisiName = (bs.getTeknisiName() != null) ? bs.getTeknisiName() : "Unknown";
            int totalPayment = bs.getTotalPayment();
            totalIncome += totalPayment;
            
            data[rowIndex][0] = String.valueOf(bs.getno_transaksi());
            data[rowIndex][1] = bs.gettanggal().toString();
            data[rowIndex][2] = bs.getjam().toString();
            data[rowIndex][3] = bs.getstatusBook();
            data[rowIndex][4] = mobilInfo;
            data[rowIndex][5] = ownerName;
            data[rowIndex][6] = teknisiName;
            data[rowIndex][7] = formatCurrency(totalPayment);
            rowIndex++;
        }
        
        printFormattedTable(headers, data);
        
        System.out.println("\n=== TOTAL PENDAPATAN ===");
        System.out.printf("Total pendapatan dari semua transaksi selesai: %s\n", formatCurrency(totalIncome));
    }

    private static void kelolaBooking(Scanner scanner) {
        while (true) {
            System.out.println("\n=== KELOLA BOOKING ===");
            System.out.println("1. Lihat Booking Aktif");
            System.out.println("2. Lihat Semua Booking");
            System.out.println("3. Proses Booking");
            System.out.println("4. Selesai Booking");
            System.out.println("5. Tolak Booking");
            System.out.println("6. Laporan Ringkasan Status Booking");
            System.out.println("7. Kembali");
            System.out.print("Pilih menu (1-7): ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (pilihan) {
                case 1:
                    try (Connection conn = DBConnection.getConnection()) {
                        BookingAktifView.tampilkanBookingAktif(conn);
                    } catch (Exception e) {
                        System.out.println("Gagal menampilkan booking aktif: " + e.getMessage());
                    }
                    break;
                case 2:
                    lihatSemuaBooking();
                    break;
                case 3:
                    prosesBooking(scanner);
                    break;
                case 4:
                    selesaiBooking(scanner);
                    break;
                case 5:
                    tolakBooking(scanner);
                    break;
                case 6:
                    try (Connection conn = DBConnection.getConnection()) {
                        report.tampilkanRingkasanStatusBooking(conn);
                    } catch (Exception e) {
                        System.out.println("Gagal menampilkan laporan ringkasan status booking: " + e.getMessage());
                    }
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void lihatSemuaBooking() {
        System.out.println("\n=== DAFTAR BOOKING ===");
        List<booking_servis> bookings = bookingDAO.readBookingsWithDetails();
        if (bookings.isEmpty()) {
            System.out.println("Tidak ada data booking.");
            return;
        }

        // Prepare data for formatted table
        String[] headers = {"ID", "No Transaksi", "Tanggal", "Jam", "Status", "Mobil", "Pelanggan", "Total Bayar"};
        String[][] data = new String[bookings.size()][8];
        
        int rowIndex = 0;
        for (booking_servis bs : bookings) {
            String mobilInfo = (bs.getMerk() != null && bs.getTipe() != null) ?
                            bs.getMerk() + " " + bs.getTipe() : "Unknown";
            String ownerName = (bs.getOwnerName() != null) ? bs.getOwnerName() : "Unknown";
            int totalPayment = bs.getTotalPayment() > 0 ? bs.getTotalPayment() : calculateTotalPayment(bs.getidBooking());
            
            data[rowIndex][0] = String.valueOf(bs.getidBooking());
            data[rowIndex][1] = String.valueOf(bs.getno_transaksi());
            data[rowIndex][2] = bs.gettanggal().toString();
            data[rowIndex][3] = bs.getjam().toString();
            data[rowIndex][4] = bs.getstatusBook();
            data[rowIndex][5] = mobilInfo;
            data[rowIndex][6] = ownerName;
            data[rowIndex][7] = formatCurrency(totalPayment);
            rowIndex++;
        }
        
        printFormattedTable(headers, data);
    }

    private static void prosesBooking(Scanner scanner) {
        System.out.println("\n=== PROSES BOOKING ===");
        List<booking_servis> bookings = bookingDAO.readBookingsWithDetails();
        List<booking_servis> pendingBookings = new ArrayList<>();
        
        for (booking_servis bs : bookings) {
            if (bs.getstatusBook().equals("Menunggu")) {
                pendingBookings.add(bs);
            }
        }
        
        if (pendingBookings.isEmpty()) {
            System.out.println("Tidak ada booking yang menunggu diproses.");
            return;
        }
        
        System.out.println("ID\tNo Transaksi\tTanggal\t\tJam\t\tMobil\t\tPelanggan\tTotal Bayar");
        System.out.println("----------------------------------------------------------------------------");
        for (booking_servis bs : pendingBookings) {
            String mobilInfo = (bs.getMerk() != null && bs.getTipe() != null) ? 
                              bs.getMerk() + " " + bs.getTipe() : "Unknown";
            String ownerName = (bs.getOwnerName() != null) ? bs.getOwnerName() : "Unknown";
            int totalPayment = bs.getTotalPayment() > 0 ? bs.getTotalPayment() : calculateTotalPayment(bs.getidBooking());
            
            System.out.printf("%d\t%d\t\t%s\t%s\t%s\t%s\t\t%s\n",
                bs.getidBooking(),
                bs.getno_transaksi(),
                bs.gettanggal(),
                bs.getjam(),
                mobilInfo,
                ownerName,
                formatCurrency(totalPayment));
        }
        
        System.out.print("\nMasukkan ID booking yang akan diproses: ");
        int idBooking = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        try {
            // Status will be set to database default in bookingDAO.updateBookingStatus
            bookingDAO.updateBookingStatus(idBooking, "Diproses");
            System.out.println("Booking berhasil diproses!");
        } catch (Exception e) {
            System.out.println("Gagal memproses booking: " + e.getMessage());
        }
    }

    private static void selesaiBooking(Scanner scanner) {
        System.out.println("\n=== SELESAI BOOKING ===");
        List<booking_servis> bookings = bookingDAO.readBookingsWithDetails();
        List<booking_servis> activeBookings = new ArrayList<>();
        
        for (booking_servis bs : bookings) {
            if (bs.getstatusBook().equalsIgnoreCase("aktif")) {
                activeBookings.add(bs);
            }
        }
        
        if (activeBookings.isEmpty()) {
            System.out.println("Tidak ada booking yang sedang aktif.");
            return;
        }
        
        System.out.println("ID\tNo Transaksi\tTanggal\t\tJam\t\tMobil\t\tPelanggan\tTotal Bayar");
        System.out.println("----------------------------------------------------------------------------");
        for (booking_servis bs : activeBookings) {
            String mobilInfo = (bs.getMerk() != null && bs.getTipe() != null) ? 
                              bs.getMerk() + " " + bs.getTipe() : "Unknown";
            String ownerName = (bs.getOwnerName() != null) ? bs.getOwnerName() : "Unknown";
            int totalPayment = bs.getTotalPayment() > 0 ? bs.getTotalPayment() : calculateTotalPayment(bs.getidBooking());
            
            System.out.printf("%d\t%d\t\t%s\t%s\t%s\t%s\t\t%s\n",
                bs.getidBooking(),
                bs.getno_transaksi(),
                bs.gettanggal(),
                bs.getjam(),
                mobilInfo,
                ownerName,
                formatCurrency(totalPayment));
        }
        
        System.out.print("\nMasukkan ID booking yang akan diselesaikan: ");
        int idBooking = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        try {
            // Generate transaction number if not exists
            booking_servis booking = null;
            for (booking_servis bs : activeBookings) {
                if (bs.getidBooking() == idBooking) {
                    booking = bs;
                    break;
                }
            }
            
            if (booking == null) {
                System.out.println("ID booking tidak valid atau booking tidak aktif.");
                return;
            }
            
            if (booking.getno_transaksi() == 0) {
                int noTransaksi = generateTransactionNumber();
                bookingDAO.updateBookingTransactionNumber(idBooking, noTransaksi);
            }
            
            bookingDAO.updateBookingStatus(idBooking, "Selesai");
            System.out.println("Booking berhasil diselesaikan!");
        } catch (Exception e) {
            System.out.println("Gagal menyelesaikan booking: " + e.getMessage());
        }
    }

    private static void tolakBooking(Scanner scanner) {
        System.out.println("\n=== TOLAK BOOKING ===");
        List<booking_servis> bookings = bookingDAO.readBookingsWithDetails();
        List<booking_servis> pendingBookings = new ArrayList<>();
        
        for (booking_servis bs : bookings) {
            if (bs.getstatusBook().equalsIgnoreCase("Menunggu")) {
                pendingBookings.add(bs);
            }
        }
        
        if (pendingBookings.isEmpty()) {
            System.out.println("Tidak ada booking yang menunggu ditolak.");
            return;
        }
        
        System.out.println("ID\tTanggal\t\tJam\t\tMobil\t\tPelanggan\tTotal Bayar");
        System.out.println("----------------------------------------------------------------");
        for (booking_servis bs : pendingBookings) {
            String mobilInfo = (bs.getMerk() != null && bs.getTipe() != null) ? 
                              bs.getMerk() + " " + bs.getTipe() : "Unknown";
            String ownerName = (bs.getOwnerName() != null) ? bs.getOwnerName() : "Unknown";
            int totalPayment = bs.getTotalPayment() > 0 ? bs.getTotalPayment() : calculateTotalPayment(bs.getidBooking());
            
            System.out.printf("%d\t%s\t%s\t%s\t%s\t\t%s\n",
                bs.getidBooking(),
                bs.gettanggal(),
                bs.getjam(),
                mobilInfo,
                ownerName,
                formatCurrency(totalPayment));
        }
        
        System.out.print("\nMasukkan ID booking yang akan ditolak: ");
        int idBooking = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        try {
            // Check if booking exists and is pending
            booking_servis booking = null;
            for (booking_servis bs : pendingBookings) {
                if (bs.getidBooking() == idBooking) {
                    booking = bs;
                    break;
                }
            }
            
            if (booking == null) {
                System.out.println("Booking tidak ditemukan atau tidak dalam status menunggu.");
                return;
            }
            
            bookingDAO.updateBookingStatus(idBooking, "Ditolak");
            System.out.println("Booking berhasil ditolak!");
        } catch (Exception e) {
            System.out.println("Gagal menolak booking: " + e.getMessage());
        }
    }

    private static int calculateTotalPayment(int bookingId) {
        return bookingDAO.calculateTotalPayment(bookingId);
    }

    private static void kelolaTeknisi(Scanner scanner) {
        while (true) {
            System.out.println("\n=== KELOLA TEKNISI ===");
            System.out.println("1. Lihat Semua Teknisi");
            System.out.println("2. Tambah Teknisi");
            System.out.println("3. Update Status Teknisi");
            System.out.println("4. Hapus Teknisi");
            System.out.println("5. Laporan Perfoma Teknisi");
            System.out.println("6. Kembali");
            System.out.print("Pilih menu (1-6): ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (pilihan) {
                case 1:
                    lihatSemuaTeknisi();
                    break;
                case 2:
                    tambahTeknisi(scanner);
                    break;
                case 3:
                    updateStatusTeknisi(scanner);
                    break;
                case 4:
                    hapusTeknisi(scanner);
                    break;
                case 5:
                    try (Connection conn = DBConnection.getConnection()) {
                        report.tampilkanLaporanTeknisiMingguan(conn);
                    } catch (Exception e) {
                        System.out.println("Gagal menampilkan laporan teknisi mingguan: " + e.getMessage());
                    }
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void lihatSemuaTeknisi() {
        System.out.println("\n=== DAFTAR TEKNISI ===");
        List<teknisi> teknisiList = teknisiDAO.readAllteknisi();
        if (teknisiList.isEmpty()) {
            System.out.println("Belum ada data teknisi.");
            return;
        }
        
        // Prepare data for formatted table
        String[] headers = {"ID", "Nama", "Status"};
        String[][] data = new String[teknisiList.size()][3];
        
        int rowIndex = 0;
        for (teknisi t : teknisiList) {
            data[rowIndex][0] = String.valueOf(t.getidTeknisi());
            data[rowIndex][1] = t.getnama();
            data[rowIndex][2] = t.getstatus();
            rowIndex++;
        }
        
        printFormattedTable(headers, data);
    }

    private static void tambahTeknisi(Scanner scanner) {
        System.out.println("\n=== TAMBAH TEKNISI ===");
        System.out.print("Masukkan nama teknisi: ");
        String nama = scanner.nextLine();
        System.out.print("Masukkan status (Tersedia/Sibuk): ");
        String status = scanner.nextLine();
        
        teknisi newTeknisi = new teknisi(0, nama, status);
        
        try {
            teknisiDAO.addTeknisi(newTeknisi);
            System.out.println("Teknisi berhasil ditambahkan!");
        } catch (SQLException e) {
            System.out.println("Gagal menambahkan teknisi: " + e.getMessage());
        }
    }

    private static void updateStatusTeknisi(Scanner scanner) {
        System.out.println("\n=== UPDATE STATUS TEKNISI ===");
        lihatSemuaTeknisi();
        System.out.print("\nMasukkan ID teknisi: ");
        int idTeknisi = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Masukkan status baru (Tersedia/Sibuk): ");
        String newStatus = scanner.nextLine();
        
        teknisiDAO.updateStatusTeknisi(idTeknisi, newStatus);
    }

    private static void hapusTeknisi(Scanner scanner) {
        System.out.println("\n=== HAPUS TEKNISI ===");
        lihatSemuaTeknisi();
        System.out.print("\nMasukkan ID teknisi yang ingin dihapus: ");
        int idTeknisi = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        teknisiDAO.deleteTeknisi(idTeknisi);
    }

    private static void kelolaJenisServis(Scanner scanner) {
        while (true) {
            System.out.println("\n=== KELOLA JENIS SERVIS ===");
            System.out.println("1. Lihat Semua Jenis Servis");
            System.out.println("2. Tambah Jenis Servis");
            System.out.println("3. Update Jenis Servis");
            System.out.println("4. Hapus Jenis Servis");
            System.out.println("5. Kembali");
            System.out.print("Pilih menu (1-5): ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (pilihan) {
                case 1:
                    lihatSemuaJenisServis();
                    break;
                case 2:
                    tambahJenisServis(scanner);
                    break;
                case 3:
                    updateJenisServis(scanner);
                    break;
                case 4:
                    hapusJenisServis(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void lihatSemuaJenisServis() {
        System.out.println("\n=== DAFTAR JENIS SERVIS ===");
        List<jenis_servis> servisList = jenisServisDAO.readJenisServis();
        
        // Prepare data for formatted table
        String[] headers = {"ID", "Nama Servis", "Waktu", "Harga"};
        String[][] data = new String[servisList.size()][4];
        
        int rowIndex = 0;
        for (jenis_servis js : servisList) {
            data[rowIndex][0] = String.valueOf(js.getidJenisServis());
            data[rowIndex][1] = js.getNamaServis();
            data[rowIndex][2] = js.gettime().toString().substring(0, 5); // Only show HH:mm
            data[rowIndex][3] = formatCurrency(js.getharga());
            rowIndex++;
        }
        
        printFormattedTable(headers, data);
    }

    private static void tambahJenisServis(Scanner scanner) {
        System.out.println("\n=== TAMBAH JENIS SERVIS ===");
        
        System.out.print("Masukkan nama servis: ");
        String namaServis = scanner.nextLine();
        
        Time time = validateAndParseTime(scanner, "Masukkan waktu pengerjaan (format: HH:mm, contoh: 02:30): ");
        
        int harga = getValidInteger(scanner, "Masukkan harga: ");
        
        try {
            jenis_servis newServis = new jenis_servis(namaServis, time, harga);
            jenisServisDAO.addJenisServis(newServis);
            System.out.println("Jenis servis berhasil ditambahkan!");
        } catch (SQLException e) {
            System.out.println("Gagal menambahkan jenis servis: " + e.getMessage());
        }
    }

    private static void updateJenisServis(Scanner scanner) {
        System.out.println("\n=== UPDATE JENIS SERVIS ===");
        lihatSemuaJenisServis();
        
        int id = getValidInteger(scanner, "\nMasukkan ID jenis servis yang akan diupdate: ");
        
        System.out.print("Masukkan nama servis baru: ");
        String newNama = scanner.nextLine();
        
        Time newTime = validateAndParseTime(scanner, "Masukkan waktu pengerjaan baru (format: HH:mm, contoh: 02:30): ");
        
        int newHarga = getValidInteger(scanner, "Masukkan harga baru: ");
        
        jenisServisDAO.updateJenisServis(id, newNama, newTime, newHarga);
    }

    private static void hapusJenisServis(Scanner scanner) {
        System.out.println("\n=== HAPUS JENIS SERVIS ===");
        lihatSemuaJenisServis();
        System.out.print("\nMasukkan ID jenis servis yang ingin dihapus: ");
        int idServis = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        jenisServisDAO.deleteJenisServis(idServis);
    }

    private static int generateTransactionNumber() {
        return (int) (System.currentTimeMillis() % 1000000);
    }

    // Utility methods for input validation and formatting
    private static Time validateAndParseTime(Scanner scanner, String prompt) {
        Time time = null;
        boolean validTime = false;
        while (!validTime) {
            try {
                System.out.print(prompt);
                String timeStr = scanner.nextLine();
                if (timeStr.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                    time = Time.valueOf(timeStr + ":00");
                    validTime = true;
                } else {
                    System.out.println("Format waktu tidak valid. Gunakan format HH:mm (contoh: 02:30)");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Format waktu tidak valid. Gunakan format HH:mm (contoh: 02:30)");
            }
        }
        return time;
    }

    private static void printTableHeader(String... headers) {
        StringBuilder headerLine = new StringBuilder();
        StringBuilder separatorLine = new StringBuilder();
        
        for (String header : headers) {
            headerLine.append(header).append("\t");
            separatorLine.append("-".repeat(Math.max(header.length(), 8))).append("\t");
        }
        
        System.out.println(headerLine.toString().trim());
        System.out.println(separatorLine.toString().trim());
    }

    // Improved table formatting with fixed-width columns
    private static void printFormattedTable(String[] headers, String[][] data) {
        // Calculate column widths based on headers and data
        int[] columnWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();
        }
        
        // Find maximum width for each column from data
        for (String[] row : data) {
            for (int i = 0; i < row.length && i < columnWidths.length; i++) {
                if (row[i] != null) {
                    columnWidths[i] = Math.max(columnWidths[i], row[i].length());
                }
            }
        }
        
        // Ensure minimum width of 8 characters
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = Math.max(columnWidths[i], 8);
        }
        
        // Print header
        StringBuilder headerLine = new StringBuilder();
        StringBuilder separatorLine = new StringBuilder();
        for (int i = 0; i < headers.length; i++) {
            headerLine.append(String.format("%-" + columnWidths[i] + "s", headers[i]));
            if (i < headers.length - 1) headerLine.append("  ");
            separatorLine.append("-".repeat(columnWidths[i]));
            if (i < headers.length - 1) separatorLine.append("  ");
        }
        System.out.println(headerLine.toString());
        System.out.println(separatorLine.toString());
        
        // Print data rows
        for (String[] row : data) {
            StringBuilder dataLine = new StringBuilder();
            for (int i = 0; i < row.length && i < columnWidths.length; i++) {
                String value = row[i] != null ? row[i] : "";
                dataLine.append(String.format("%-" + columnWidths[i] + "s", value));
                if (i < row.length - 1 && i < columnWidths.length - 1) dataLine.append("  ");
            }
            System.out.println(dataLine.toString());
        }
    }

    // Helper method to format a single row with consistent spacing
    private static void printFormattedRow(String... values) {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            String value = values[i] != null ? values[i] : "";
            // Use fixed width for each column
            int width = getColumnWidth(i, values.length);
            row.append(String.format("%-" + width + "s", value));
            if (i < values.length - 1) row.append("  ");
        }
        System.out.println(row.toString());
    }

    // Helper method to get appropriate column width based on position
    private static int getColumnWidth(int columnIndex, int totalColumns) {
        switch (columnIndex) {
            case 0: return 8;   // ID columns
            case 1: return 12;  // Date columns
            case 2: return 10;  // Time columns
            case 3: return 12;  // Status columns
            case 4: return 15;  // Transaction number
            case 5: return 20;  // Car info
            case 6: return 20;  // Customer name
            case 7: return 15;  // Technician name
            case 8: return 15;  // Price columns
            default: return 12;
        }
    }

    private static int getValidInteger(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid. Masukkan angka.");
            }
        }
    }

    private static String formatCurrency(int amount) {
        return String.format("Rp%,d", amount);
    }

    private static String validateDateInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String dateStr = scanner.nextLine();
            if (dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                return dateStr;
            } else {
                System.out.println("Format tanggal tidak valid. Gunakan format YYYY-MM-DD");
            }
        }
    }

    private static void lihatMobilPelanggan(User user) {
        System.out.println("\n=== DAFTAR MOBIL ===");
        List<mobil> mobilList = mobilDAO.readMobilByUserId(user.getidUser());
        if (mobilList.isEmpty()) {
            System.out.println("Belum ada data mobil.");
        } else {
            // Prepare data for formatted table
            String[] headers = {"ID", "Merk", "Tipe", "Tahun"};
            String[][] data = new String[mobilList.size()][4];
            
            int rowIndex = 0;
            for (mobil m : mobilList) {
                data[rowIndex][0] = String.valueOf(m.getidMobil());
                data[rowIndex][1] = m.getmerk();
                data[rowIndex][2] = m.gettipe();
                data[rowIndex][3] = String.valueOf(m.gettahun());
                rowIndex++;
            }
            
            printFormattedTable(headers, data);
        }
    }

    private static void tambahMobilPelanggan(Scanner scanner, User user) {
        System.out.println("\n=== TAMBAH MOBIL ===");
        System.out.print("Masukkan merk mobil: ");
        String merk = scanner.nextLine();
        System.out.print("Masukkan tipe mobil: ");
        String tipe = scanner.nextLine();
        System.out.print("Masukkan tahun mobil: ");
        int tahun = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // Using the constructor that takes idPelanggan, merk, tipe, tahun, fk_user
        mobil newMobil = new mobil(0, merk, tipe, tahun, user.getidUser());

        try {
            mobilDAO.addMobil(newMobil);
            System.out.println("Mobil berhasil ditambahkan!");
        } catch (SQLException e) {
            System.out.println("Gagal menambahkan mobil: " + e.getMessage());
        }
    }

    private static void tambahBookingPelanggan(Scanner scanner, User user) {
        System.out.println("\n=== BUAT BOOKING ===");
        
        // Show user's cars
        System.out.println("\nDaftar Mobil Anda:");
        List<mobil> mobilList = mobilDAO.readMobilByUserId(user.getidUser());
        if (mobilList.isEmpty()) {
            System.out.println("Anda belum memiliki mobil. Silakan tambahkan mobil terlebih dahulu.");
            return;
        }
        
        // Prepare data for formatted table
        String[] carHeaders = {"ID", "Merk", "Tipe", "Tahun"};
        String[][] carData = new String[mobilList.size()][4];
        
        int carRowIndex = 0;
        for (mobil m : mobilList) {
            carData[carRowIndex][0] = String.valueOf(m.getidMobil());
            carData[carRowIndex][1] = m.getmerk();
            carData[carRowIndex][2] = m.gettipe();
            carData[carRowIndex][3] = String.valueOf(m.gettahun());
            carRowIndex++;
        }
        
        printFormattedTable(carHeaders, carData);
        
        System.out.print("\nPilih ID mobil: ");
        int idMobil = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // Show available technicians
        System.out.println("\nDaftar Teknisi Tersedia:");
        List<teknisi> teknisiList = teknisiDAO.readAllteknisi();
        List<teknisi> availableTeknisi = new ArrayList<>();
        
        // Prepare data for formatted table
        String[] techHeaders = {"ID", "Nama", "Status"};
        String[][] techData = new String[teknisiList.size()][3];
        
        int techRowIndex = 0;
        for (teknisi t : teknisiList) {
            if (t.getstatus().equals("Tersedia")) {
                techData[techRowIndex][0] = String.valueOf(t.getidTeknisi());
                techData[techRowIndex][1] = t.getnama();
                techData[techRowIndex][2] = t.getstatus();
                availableTeknisi.add(t);
                techRowIndex++;
            }
        }
        
        // Create a new array with only available technicians
        String[][] availableTechData = new String[availableTeknisi.size()][3];
        for (int i = 0; i < availableTeknisi.size(); i++) {
            availableTechData[i] = techData[i];
        }
        
        if (availableTeknisi.isEmpty()) {
            System.out.println("Maaf, tidak ada teknisi yang tersedia saat ini.");
            return;
        }
        
        printFormattedTable(techHeaders, availableTechData);
        
        System.out.print("\nPilih ID teknisi: ");
        int idTeknisi = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // Verify selected teknisi is available
        boolean teknisiValid = false;
        for (teknisi t : availableTeknisi) {
            if (t.getidTeknisi() == idTeknisi) {
                teknisiValid = true;
                break;
            }
        }
        
        if (!teknisiValid) {
            System.out.println("ID teknisi tidak valid atau teknisi tidak tersedia.");
            return;
        }

        // Get booking date and time
        String tanggalStr = validateDateInput(scanner, "Masukkan tanggal booking (YYYY-MM-DD): ");
        System.out.print("Masukkan jam booking (HH:mm): ");
        String jamStr = scanner.nextLine();
        while (!jamStr.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            System.out.println("Format jam tidak valid. Gunakan format HH:mm");
            System.out.print("Masukkan jam booking (HH:mm): ");
            jamStr = scanner.nextLine();
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Create booking
            Date tanggal = Date.valueOf(tanggalStr);
            Time jam = Time.valueOf(jamStr + ":00");
            int noTransaksi = generateTransactionNumber();
            
            booking_servis newBooking = new booking_servis(idMobil, idTeknisi, tanggal, jam, "Menunggu", noTransaksi);
            bookingDAO.createBooking(newBooking);
            
            // Get the booking ID
            int bookingId = bookingDAO.getLastBookingId();
            
            // Show available service types and allow multiple selections
            List<Integer> selectedServices = new ArrayList<>();
            boolean continueAdding = true;
            
            while (continueAdding) {
                System.out.println("\nDaftar Jenis Servis:");
                List<jenis_servis> servisList = jenisServisDAO.readJenisServis();
                
                // Prepare data for formatted table
                String[] serviceHeaders = {"ID", "Nama Servis", "Waktu", "Harga"};
                String[][] serviceData = new String[servisList.size()][4];
                
                int serviceRowIndex = 0;
                for (jenis_servis js : servisList) {
                    serviceData[serviceRowIndex][0] = String.valueOf(js.getidJenisServis());
                    serviceData[serviceRowIndex][1] = js.getNamaServis();
                    serviceData[serviceRowIndex][2] = js.gettime().toString().substring(0, 5);
                    serviceData[serviceRowIndex][3] = formatCurrency(js.getharga());
                    serviceRowIndex++;
                }
                
                printFormattedTable(serviceHeaders, serviceData);
                
                System.out.print("\nPilih ID jenis servis: ");
                int idJenisServis = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                // Verify service exists
                boolean serviceExists = false;
                for (jenis_servis js : servisList) {
                    if (js.getidJenisServis() == idJenisServis) {
                        serviceExists = true;
                        break;
                    }
                }
                
                if (!serviceExists) {
                    System.out.println("ID jenis servis tidak valid!");
                    continue;
                }
                
                // Add service to booking
                detail_servis newDetail = new detail_servis(bookingId, idJenisServis);
                detailServisDAO.createDetailServis(newDetail, conn);
                selectedServices.add(idJenisServis);
                
                // Commit after each service addition
                conn.commit();
                
                System.out.print("\nTambah jenis servis lain? (y/n): ");
                String answer = scanner.nextLine().toLowerCase();
                continueAdding = answer.equals("y");
            }
            
            // Calculate and show total duration at the end
            System.out.println("\nMenghitung durasi pengerjaan...");
            int totalMinutes = 0;
            for (int serviceId : selectedServices) {
                // Get service duration from jenis_servis table
                String durationSql = "SELECT estimasi_waktu FROM jenis_servis WHERE id_jenis_servis = ?";
                try (PreparedStatement durationStmt = conn.prepareStatement(durationSql)) {
                    durationStmt.setInt(1, serviceId);
                    ResultSet rs = durationStmt.executeQuery();
                    if (rs.next()) {
                        Time serviceTime = rs.getTime("estimasi_waktu");
                        totalMinutes += (serviceTime.getHours() * 60) + serviceTime.getMinutes();
                    }
                }
            }
            
            if (totalMinutes > 0) {
                int hours = totalMinutes / 60;
                int minutes = totalMinutes % 60;
                if (hours > 0) {
                    System.out.printf("Total durasi pengerjaan: %d jam %d menit\n", hours, minutes);
                } else {
                    System.out.printf("Total durasi pengerjaan: %d menit\n", minutes);
                }
            }
            
            conn.commit(); // Commit transaction
            System.out.println("\nBooking berhasil dibuat dengan " + selectedServices.size() + " jenis servis!");
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Gagal membuat booking: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void lihatRiwayatBookingPelanggan(User user) {
        System.out.println("\n=== RIWAYAT BOOKING ===");
        List<booking_servis> bookings = bookingDAO.readBookingsByUserId(user.getidUser());
        if (bookings.isEmpty()) {
            System.out.println("Belum ada data booking.");
        } else {
            // Prepare data for formatted table
            String[] headers = {"ID", "Tanggal", "Jam", "Status", "No Transaksi", "Total Bayar"};
            String[][] data = new String[bookings.size()][6];
            
            int rowIndex = 0;
            for (booking_servis bs : bookings) {
                int totalPayment = bs.getTotalPayment() > 0 ? bs.getTotalPayment() : calculateTotalPayment(bs.getidBooking());
                
                data[rowIndex][0] = String.valueOf(bs.getidBooking());
                data[rowIndex][1] = bs.gettanggal().toString();
                data[rowIndex][2] = bs.getjam().toString();
                data[rowIndex][3] = bs.getstatusBook();
                data[rowIndex][4] = String.valueOf(bs.getno_transaksi());
                data[rowIndex][5] = formatCurrency(totalPayment);
                rowIndex++;
            }
            
            printFormattedTable(headers, data);
        }
    }

    private static void batalkanBookingPelanggan(Scanner scanner, User user) {
        System.out.println("\n=== BATALKAN BOOKING ===");
        List<booking_servis> bookings = bookingDAO.readBookingsByUserId(user.getidUser());
        List<booking_servis> userBookings = new ArrayList<>();
        
        // Filter bookings that can be cancelled (status "Menunggu")
        for (booking_servis bs : bookings) {
            if (bs.getstatusBook().equals("Menunggu")) {
                userBookings.add(bs);
            }
        }
        
        if (userBookings.isEmpty()) {
            System.out.println("Tidak ada booking yang dapat dibatalkan.");
            return;
        }
        
        System.out.println("\nDaftar Booking yang Dapat Dibatalkan:");
        
        // Prepare data for formatted table
        String[] headers = {"ID", "Tanggal", "Jam", "No Transaksi", "Total Bayar"};
        String[][] data = new String[userBookings.size()][5];
        
        int rowIndex = 0;
        for (booking_servis bs : userBookings) {
            int totalPayment = bs.getTotalPayment() > 0 ? bs.getTotalPayment() : calculateTotalPayment(bs.getidBooking());
            
            data[rowIndex][0] = String.valueOf(bs.getidBooking());
            data[rowIndex][1] = bs.gettanggal().toString();
            data[rowIndex][2] = bs.getjam().toString();
            data[rowIndex][3] = String.valueOf(bs.getno_transaksi());
            data[rowIndex][4] = formatCurrency(totalPayment);
            rowIndex++;
        }
        
        printFormattedTable(headers, data);
        
        int idBooking = getValidInteger(scanner, "\nMasukkan ID booking yang ingin dibatalkan: ");
        
        try {
            bookingDAO.updateBookingStatus(idBooking, "Dibatalkan");
            System.out.println("Booking berhasil dibatalkan!");
        } catch (Exception e) {
            System.out.println("Gagal membatalkan booking: " + e.getMessage());
        }
    }
}