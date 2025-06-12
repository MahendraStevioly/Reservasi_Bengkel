import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
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

        List<User> users = userDAO.readAllUser();
        for (User user : users) {
            if (user.getusername().equals(username)) {
                if (user.getrole() == User.Role.ADMIN) {
                    if (user.getpassword().equals(password)) {
                        currentUser = user;
                        adminMenu();
                        return;
                    }
                } else if (user.getrole() == User.Role.PELANGGAN) {
                    currentUser = user;
                    menuPelanggan(scanner, currentUser);
                    return;
                }
            }
        }
        System.out.println("Login gagal! Username atau password salah.");
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n=== MENU ADMIN ===");
            System.out.println("1. Kelola User");
            System.out.println("2. Kelola Mobil");
            System.out.println("3. Kelola Teknisi");
            System.out.println("4. Kelola Jenis Servis");
            System.out.println("5. Kelola Booking");
            System.out.println("6. Lihat Data Mobil Pelanggan");
            System.out.println("7. Kelola Mobil Pelanggan");
            System.out.println("8. Riwayat Transaksi");
            System.out.println("9. Logout");
            System.out.print("Pilih menu (1-9): ");
            
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
                    lihatDataMobilPelanggan();
                    break;
                case 7:
                    kelolaMobilPelanggan(scanner);
                    break;
                case 8:
                    lihatRiwayatTransaksi();
                    break;
                case 9:
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
        
        System.out.println("ID\tUsername\tRole\t\tNama\t\tNo Telepon");
        System.out.println("--------------------------------------------------------");
        for (User u : users) {
            System.out.printf("%d\t%s\t\t%s\t%s\t%s\n",
                u.getidUser(),
                u.getusername(),
                u.getrole(),
                u.getnama(),
                u.getno_tlp());
        }
    }

    private static void tambahUser(Scanner scanner) {
        System.out.println("\n=== TAMBAH USER ===");
        System.out.print("Masukkan username: ");
        String username = scanner.nextLine();
        System.out.print("Masukkan password: ");
        String password = scanner.nextLine();
        System.out.print("Masukkan role (admin/pelanggan): ");
        String roleStr = scanner.nextLine();
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
        System.out.print("\nMasukkan ID user yang ingin dihapus: ");
        int idUser = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        userDAO.deleteUser(idUser);
    }

    private static void kelolaMobil(Scanner scanner) {
        while (true) {
            System.out.println("\n=== KELOLA MOBIL ===");
            System.out.println("1. Lihat Semua Mobil");
            System.out.println("2. Tambah Mobil");
            System.out.println("3. Hapus Mobil");
            System.out.println("4. Kembali");
            System.out.print("Pilih menu (1-4): ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (pilihan) {
                case 1:
                    lihatSemuaMobil();
                    break;
                case 2:
                    tambahMobil(scanner);
                    break;
                case 3:
                    hapusMobil(scanner);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void lihatSemuaMobil() {
        System.out.println("\n=== DAFTAR MOBIL ===");
        List<mobil> mobilList = mobilDAO.readAllMobil();
        if (mobilList.isEmpty()) {
            System.out.println("Belum ada data mobil.");
            return;
        }
        
        System.out.println("ID\tMerk\t\tTipe\t\tTahun\t\tID User");
        System.out.println("--------------------------------------------------------");
        for (mobil m : mobilList) {
            System.out.printf("%d\t%s\t\t%s\t\t%d\t\t%d\n",
                m.getidMobil(),
                m.getmerk(),
                m.gettipe(),
                m.gettahun(),
                m.getfk_user());
        }
    }

    private static void tambahMobil(Scanner scanner) {
        System.out.println("\n=== TAMBAH MOBIL ===");
        System.out.print("Masukkan merk mobil: ");
        String merk = scanner.nextLine();
        System.out.print("Masukkan tipe mobil: ");
        String tipe = scanner.nextLine();
        System.out.print("Masukkan tahun mobil: ");
        int tahun = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Masukkan ID user: ");
        int idUser = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        mobil newMobil = new mobil(0, merk, tipe, tahun, idUser);
        
        try {
            mobilDAO.addMobil(newMobil);
            System.out.println("Mobil berhasil ditambahkan!");
        } catch (SQLException e) {
            System.out.println("Gagal menambahkan mobil: " + e.getMessage());
        }
    }

    private static void hapusMobil(Scanner scanner) {
        System.out.println("\n=== HAPUS MOBIL ===");
        lihatSemuaMobil();
        System.out.print("\nMasukkan ID mobil yang ingin dihapus: ");
        int idMobil = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        mobilDAO.deleteMobil(idMobil);
    }

    private static void kelolaTeknisi(Scanner scanner) {
        while (true) {
            System.out.println("\n=== KELOLA TEKNISI ===");
            System.out.println("1. Lihat Semua Teknisi");
            System.out.println("2. Tambah Teknisi");
            System.out.println("3. Update Status Teknisi");
            System.out.println("4. Hapus Teknisi");
            System.out.println("5. Kembali");
            System.out.print("Pilih menu (1-5): ");
            
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
        
        System.out.println("ID\tNama\t\tStatus");
        System.out.println("----------------------------------------");
        for (teknisi t : teknisiList) {
            System.out.printf("%d\t%s\t\t%s\n",
                t.getidTeknisi(),
                t.getnama(),
                t.getstatus());
        }
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
        System.out.println("ID\tNama Servis\t\tWaktu\t\tHarga");
        System.out.println("--------------------------------------------------------");
        for (jenis_servis js : servisList) {
            System.out.printf("%d\t%s\t\t%s\t\tRp%d\n",
                js.getidJenisServis(),
                js.getNamaServis(),
                js.gettime().toString().substring(0, 5), // Only show HH:mm
                js.getharga());
        }
    }

    private static void tambahJenisServis(Scanner scanner) {
        System.out.println("\n=== TAMBAH JENIS SERVIS ===");
        
        System.out.print("Masukkan nama servis: ");
        String namaServis = scanner.nextLine();
        
        Time time = null;
        boolean validTime = false;
        while (!validTime) {
            try {
                System.out.print("Masukkan waktu pengerjaan (format: HH:mm, contoh: 02:30): ");
                String timeStr = scanner.nextLine();
                // Validate time format
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
        
        System.out.print("Masukkan harga: ");
        int harga = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        try {
            jenis_servis newServis = new jenis_servis(namaServis, time, harga);
            jenisServisDAO.addJenisServis(newServis);
        } catch (SQLException e) {
            System.out.println("Gagal menambahkan jenis servis: " + e.getMessage());
        }
    }

    private static void updateJenisServis(Scanner scanner) {
        System.out.println("\n=== UPDATE JENIS SERVIS ===");
        lihatSemuaJenisServis();
        
        System.out.print("\nMasukkan ID jenis servis yang akan diupdate: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        System.out.print("Masukkan nama servis baru: ");
        String newNama = scanner.nextLine();
        
        Time newTime = null;
        boolean validTime = false;
        while (!validTime) {
            try {
                System.out.print("Masukkan waktu pengerjaan baru (format: HH:mm, contoh: 02:30): ");
                String timeStr = scanner.nextLine();
                // Validate time format
                if (timeStr.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                    newTime = Time.valueOf(timeStr + ":00");
                    validTime = true;
                } else {
                    System.out.println("Format waktu tidak valid. Gunakan format HH:mm (contoh: 02:30)");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Format waktu tidak valid. Gunakan format HH:mm (contoh: 02:30)");
            }
        }
        
        System.out.print("Masukkan harga baru: ");
        int newHarga = scanner.nextInt();
        
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

    private static void manageBooking() {
        while (true) {
            System.out.println("\n=== KELOLA BOOKING PELANGGAN ===");
            System.out.println("1. Lihat Semua Booking");
            System.out.println("2. Proses Booking");
            System.out.println("3. Tolak Booking");
            System.out.println("0. Kembali");
            System.out.print("Pilih menu: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    viewAllBookings();
                    break;
                case 2:
                    processBooking();
                    break;
                case 3:
                    rejectBooking();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void viewAllBookings() {
        System.out.println("\n=== DAFTAR BOOKING ===");
        List<booking_servis> bookings = bookingDAO.readbBooking_servis();
        if (bookings.isEmpty()) {
            System.out.println("Belum ada data booking.");
        } else {
            System.out.println("ID\tTanggal\t\tJam\t\tStatus\t\tNo Transaksi");
            System.out.println("--------------------------------------------------------");
            for (booking_servis bs : bookings) {
                System.out.printf("%d\t%s\t%s\t%s\t%d\n",
                    bs.getidBooking(),
                    bs.gettanggal(),
                    bs.getjam(),
                    bs.getstatusBook(),
                    bs.getno_transaksi());
            }
        }
    }

    private static void processBooking() {
        viewAllBookings();
        System.out.print("\nMasukkan ID booking yang akan diproses: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline

        bookingDAO.updateBookingStatus(id, "Diproses");
    }

    private static void rejectBooking() {
        viewAllBookings();
        System.out.print("\nMasukkan ID booking yang akan ditolak: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline

        bookingDAO.updateBookingStatus(id, "Ditolak");
    }

    private static void viewMobil() {
        System.out.println("\n=== DAFTAR MOBIL ===");
        List<mobil> mobilList = mobilDAO.readMobilByUserId(currentUser.getidUser());
        if (mobilList.isEmpty()) {
            System.out.println("Belum ada data mobil.");
        } else {
            System.out.println("ID\tMerk\t\tTipe\t\tTahun");
            System.out.println("----------------------------------------");
            for (mobil m : mobilList) {
                System.out.printf("%d\t%s\t\t%s\t\t%d\n",
                    m.getidMobil(),
                    m.getmerk(),
                    m.gettipe(),
                    m.gettahun());
            }
        }
    }

    private static int generateTransactionNumber() {
        return (int) (System.currentTimeMillis() % 1000000);
    }

    private static void createBooking() {
        System.out.println("\n=== BUAT BOOKING ===");
        
        // Show user's cars
        System.out.println("\nDaftar Mobil Anda:");
        List<mobil> mobilList = mobilDAO.readMobilByUserId(currentUser.getidUser());
        if (mobilList.isEmpty()) {
            System.out.println("Anda belum memiliki mobil. Silakan tambahkan mobil terlebih dahulu.");
            return;
        }
        
        System.out.println("ID\tMerk\t\tTipe\t\tTahun");
        System.out.println("----------------------------------------");
        for (mobil m : mobilList) {
            System.out.printf("%d\t%s\t\t%s\t\t%d\n",
                m.getidMobil(),
                m.getmerk(),
                m.gettipe(),
                m.gettahun());
        }
        
        System.out.print("\nPilih ID mobil: ");
        int idMobil = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // Show available technicians
        System.out.println("\nDaftar Teknisi:");
        List<teknisi> teknisiList = teknisiDAO.readAllteknisi();
        System.out.println("ID\tNama\t\tStatus");
        System.out.println("----------------------------------------");
        for (teknisi t : teknisiList) {
            System.out.printf("%d\t%s\t\t%s\n",
                t.getidTeknisi(),
                t.getnama(),
                t.getstatus());
        }
        
        System.out.print("\nPilih ID teknisi: ");
        int idTeknisi = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // Show available service types
        System.out.println("\nDaftar Jenis Servis:");
        List<jenis_servis> servisList = jenisServisDAO.readJenisServis();
        System.out.println("ID\tNama Servis\t\tWaktu\t\tHarga");
        System.out.println("--------------------------------------------------------");
        for (jenis_servis js : servisList) {
            System.out.printf("%d\t%s\t\t%d jam\t\tRp%d\n",
                js.getidJenisServis(),
                js.getNamaServis(),
                js.gettime(),
                js.getharga());
        }
        
        System.out.print("\nPilih ID jenis servis: ");
        int idJenisServis = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // Get booking date and time
        System.out.print("Masukkan tanggal booking (YYYY-MM-DD): ");
        String tanggalStr = scanner.nextLine();
        System.out.print("Masukkan jam booking (HH:mm): ");
        String jamStr = scanner.nextLine();

        try {
            // Create booking
            Date tanggal = Date.valueOf(tanggalStr);
            Time jam = Time.valueOf(jamStr + ":00");
            int noTransaksi = generateTransactionNumber();
            
            booking_servis newBooking = new booking_servis(idMobil, idTeknisi, tanggal, jam, "Menunggu", noTransaksi);
            bookingDAO.createBooking(newBooking);
            
            // Get the booking ID
            List<booking_servis> bookings = bookingDAO.readbBooking_servis();
            int bookingId = bookings.get(bookings.size() - 1).getidBooking();
            
            // Create service detail
            detail_servis newDetail = new detail_servis(bookingId, idJenisServis);
            detailServisDAO.createDetailServis(newDetail);
            
            System.out.println("Booking berhasil dibuat!");
        } catch (SQLException e) {
            System.out.println("Gagal membuat booking: " + e.getMessage());
        }
    }

    private static void viewBookingStatus() {
        List<booking_servis> bookings = bookingDAO.readbBooking_servis();
        System.out.println("\n=== STATUS BOOKING ===");
        for (booking_servis booking : bookings) {
            System.out.println("ID Booking: " + booking.getidBooking());
            System.out.println("No. Transaksi: " + booking.getno_transaksi());
            System.out.println("Tanggal: " + booking.gettanggal());
            System.out.println("Jam: " + booking.getjam());
            System.out.println("Status: " + booking.getstatusBook());
            System.out.println("-------------------");
        }
    }

    private static void cancelBooking() {
        System.out.println("\n=== BATALKAN BOOKING ===");
        List<booking_servis> bookings = bookingDAO.readbBooking_servis();
        
        System.out.println("\nDaftar Booking Aktif:");
        for (booking_servis booking : bookings) {
            if (booking.getstatusBook().equals("Menunggu")) {
                System.out.println("ID: " + booking.getidBooking());
                System.out.println("No. Transaksi: " + booking.getno_transaksi());
                System.out.println("Tanggal: " + booking.gettanggal());
                System.out.println("Jam: " + booking.getjam());
                System.out.println("-------------------");
            }
        }

        System.out.print("\nMasukkan ID Booking yang akan dibatalkan: ");
        int idBooking = scanner.nextInt();
        scanner.nextLine(); // consume newline

        bookingDAO.deleteBooking(idBooking);
    }

    private static void viewServiceDetails() {
        System.out.println("\n=== DETAIL SERVIS ===");
        List<detail_servis> detailList = detailServisDAO.readDetail_Servis();
        if (detailList.isEmpty()) {
            System.out.println("Belum ada data detail servis.");
        } else {
            System.out.println("ID Detail\tID Booking\tID Jenis Servis");
            System.out.println("----------------------------------------");
            for (detail_servis ds : detailList) {
                System.out.printf("%d\t\t%d\t\t%d\n",
                    ds.getidDetail(),
                    ds.getidBooking(),
                    ds.getidJenisServis());
            }
        }
    }

    private static void addMobil(int userId) {
        System.out.println("\n=== TAMBAH MOBIL ===");
        System.out.print("Masukkan merk mobil: ");
        String merk = scanner.nextLine();
        System.out.print("Masukkan tipe mobil: ");
        String tipe = scanner.nextLine();
        System.out.print("Masukkan tahun mobil: ");
        int tahun = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // Using the constructor that takes idPelanggan, merk, tipe, tahun, fk_user
        mobil newMobil = new mobil(0, merk, tipe, tahun, userId);

        try {
            mobilDAO.addMobil(newMobil);
            System.out.println("Mobil berhasil ditambahkan!");
        } catch (SQLException e) {
            System.out.println("Gagal menambahkan mobil: " + e.getMessage());
        }
    }

    private static void lihatMobilPelanggan(User user) {
        System.out.println("\n=== DAFTAR MOBIL ===");
        List<mobil> mobilList = mobilDAO.readMobilByUserId(user.getidUser());
        if (mobilList.isEmpty()) {
            System.out.println("Belum ada data mobil.");
        } else {
            System.out.println("ID\tMerk\t\tTipe\t\tTahun");
            System.out.println("----------------------------------------");
            for (mobil m : mobilList) {
                System.out.printf("%d\t%s\t\t%s\t\t%d\n",
                    m.getidMobil(),
                    m.getmerk(),
                    m.gettipe(),
                    m.gettahun());
            }
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
        
        System.out.println("ID\tMerk\t\tTipe\t\tTahun");
        System.out.println("----------------------------------------");
        for (mobil m : mobilList) {
            System.out.printf("%d\t%s\t\t%s\t\t%d\n",
                m.getidMobil(),
                m.getmerk(),
                m.gettipe(),
                m.gettahun());
        }
        
        System.out.print("\nPilih ID mobil: ");
        int idMobil = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // Show available technicians
        System.out.println("\nDaftar Teknisi Tersedia:");
        List<teknisi> teknisiList = teknisiDAO.readAllteknisi();
        List<teknisi> availableTeknisi = new ArrayList<>();
        System.out.println("ID\tNama\t\tStatus");
        System.out.println("----------------------------------------");
        for (teknisi t : teknisiList) {
            if (t.getstatus().equals("Tersedia")) {
                System.out.printf("%d\t%s\t\t%s\n",
                    t.getidTeknisi(),
                    t.getnama(),
                    t.getstatus());
                availableTeknisi.add(t);
            }
        }
        
        if (availableTeknisi.isEmpty()) {
            System.out.println("Maaf, tidak ada teknisi yang tersedia saat ini.");
            return;
        }
        
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

        // Show available service types
        System.out.println("\nDaftar Jenis Servis:");
        List<jenis_servis> servisList = jenisServisDAO.readJenisServis();
        System.out.println("ID\tNama Servis\t\tWaktu\t\tHarga");
        System.out.println("--------------------------------------------------------");
        for (jenis_servis js : servisList) {
            System.out.printf("%d\t%s\t\t%s\t\tRp%d\n",
                js.getidJenisServis(),
                js.getNamaServis(),
                js.gettime().toString().substring(0, 5), // Only show HH:mm
                js.getharga());
        }
        
        System.out.print("\nPilih ID jenis servis: ");
        int idJenisServis = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // Get booking date and time
        System.out.print("Masukkan tanggal booking (YYYY-MM-DD): ");
        String tanggalStr = scanner.nextLine();
        System.out.print("Masukkan jam booking (HH:mm): ");
        String jamStr = scanner.nextLine();

        try {
            // Create booking
            Date tanggal = Date.valueOf(tanggalStr);
            Time jam = Time.valueOf(jamStr + ":00");
            int noTransaksi = generateTransactionNumber();
            
            booking_servis newBooking = new booking_servis(idMobil, idTeknisi, tanggal, jam, "Menunggu", noTransaksi);
            bookingDAO.createBooking(newBooking);
            
            // Get the booking ID
            List<booking_servis> bookings = bookingDAO.readbBooking_servis();
            int bookingId = bookings.get(bookings.size() - 1).getidBooking();
            
            // Create service detail
            detail_servis newDetail = new detail_servis(bookingId, idJenisServis);
            detailServisDAO.createDetailServis(newDetail);
            
            System.out.println("Booking berhasil dibuat!");
        } catch (SQLException e) {
            System.out.println("Gagal membuat booking: " + e.getMessage());
        }
    }

    private static void lihatRiwayatBookingPelanggan(User user) {
        System.out.println("\n=== RIWAYAT BOOKING ===");
        List<booking_servis> bookings = bookingDAO.readbBooking_servis();
        if (bookings.isEmpty()) {
            System.out.println("Belum ada data booking.");
        } else {
            System.out.println("ID\tTanggal\t\tJam\t\tStatus\t\tNo Transaksi");
            System.out.println("--------------------------------------------------------");
            for (booking_servis bs : bookings) {
                List<mobil> mobilList = mobilDAO.readMobilByUserId(user.getidUser());
                for (mobil m : mobilList) {
                    if (m.getidMobil() == bs.getidMobil()) {
                        System.out.printf("%d\t%s\t%s\t%s\t%d\n",
                            bs.getidBooking(),
                            bs.gettanggal(),
                            bs.getjam(),
                            bs.getstatusBook(),
                            bs.getno_transaksi());
                        break;
                    }
                }
            }
        }
    }

    private static void batalkanBookingPelanggan(Scanner scanner, User user) {
        System.out.println("\n=== BATALKAN BOOKING ===");
        List<booking_servis> bookings = bookingDAO.readbBooking_servis();
        List<booking_servis> userBookings = new ArrayList<>();
        
        // Filter bookings for this user
        for (booking_servis bs : bookings) {
            List<mobil> mobilList = mobilDAO.readMobilByUserId(user.getidUser());
            for (mobil m : mobilList) {
                if (m.getidMobil() == bs.getidMobil() && bs.getstatusBook().equals("Menunggu")) {
                    userBookings.add(bs);
                    break;
                }
            }
        }
        
        if (userBookings.isEmpty()) {
            System.out.println("Tidak ada booking yang dapat dibatalkan.");
            return;
        }
        
        System.out.println("\nDaftar Booking yang Dapat Dibatalkan:");
        System.out.println("ID\tTanggal\t\tJam\t\tNo Transaksi");
        System.out.println("----------------------------------------");
        for (booking_servis bs : userBookings) {
            System.out.printf("%d\t%s\t%s\t%d\n",
                bs.getidBooking(),
                bs.gettanggal(),
                bs.getjam(),
                bs.getno_transaksi());
        }
        
        System.out.print("\nMasukkan ID booking yang ingin dibatalkan: ");
        int idBooking = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        try {
            bookingDAO.updateBookingStatus(idBooking, "Dibatalkan");
            System.out.println("Booking berhasil dibatalkan!");
        } catch (Exception e) {
            System.out.println("Gagal membatalkan booking: " + e.getMessage());
        }
    }

    private static void lihatDataMobilPelanggan() {
        System.out.println("\n=== DATA MOBIL PELANGGAN ===");
        List<mobil> allMobil = mobilDAO.readAllMobil();
        if (allMobil.isEmpty()) {
            System.out.println("Belum ada data mobil.");
            return;
        }
        
        System.out.println("ID\tMerk\t\tTipe\t\tTahun\t\tID Pelanggan");
        System.out.println("--------------------------------------------------------");
        for (mobil m : allMobil) {
            User owner = userDAO.getUserById(m.getfk_user());
            System.out.printf("%d\t%s\t\t%s\t\t%d\t\t%d (%s)\n",
                m.getidMobil(),
                m.getmerk(),
                m.gettipe(),
                m.gettahun(),
                m.getfk_user(),
                owner != null ? owner.getnama() : "Unknown");
        }
    }

    private static void kelolaMobilPelanggan(Scanner scanner) {
        while (true) {
            System.out.println("\n=== KELOLA MOBIL PELANGGAN ===");
            System.out.println("1. Lihat Semua Mobil");
            System.out.println("2. Hapus Mobil");
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

    private static void hapusMobilPelanggan(Scanner scanner) {
        System.out.println("\n=== HAPUS MOBIL PELANGGAN ===");
        List<mobil> allMobil = mobilDAO.readAllMobil();
        if (allMobil.isEmpty()) {
            System.out.println("Belum ada data mobil.");
            return;
        }
        
        System.out.println("ID\tMerk\t\tTipe\t\tTahun\t\tID Pelanggan");
        System.out.println("--------------------------------------------------------");
        for (mobil m : allMobil) {
            User owner = userDAO.getUserById(m.getfk_user());
            System.out.printf("%d\t%s\t\t%s\t\t%d\t\t%d (%s)\n",
                m.getidMobil(),
                m.getmerk(),
                m.gettipe(),
                m.gettahun(),
                m.getfk_user(),
                owner != null ? owner.getnama() : "Unknown");
        }
        
        System.out.print("\nMasukkan ID mobil yang ingin dihapus: ");
        int idMobil = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        try {
            mobilDAO.deleteMobil(idMobil);
            System.out.println("Mobil berhasil dihapus!");
        } catch (Exception e) {
            System.out.println("Gagal menghapus mobil: " + e.getMessage());
        }
    }

    private static void lihatRiwayatTransaksi() {
        System.out.println("\n=== RIWAYAT TRANSAKSI ===");
        List<booking_servis> bookings = bookingDAO.readbBooking_servis();
        boolean found = false;
        System.out.println("No Transaksi\tTanggal\t\tJam\t\tStatus\t\tMobil\t\tPelanggan\t\tTeknisi");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        for (booking_servis bs : bookings) {
            if (!"Selesai".equals(bs.getstatusBook())) continue;
            mobil m = mobilDAO.getMobilById(bs.getidMobil());
            User owner = userDAO.getUserById(m.getfk_user());
            teknisi t = teknisiDAO.getTeknisiById(bs.getidTeknisi());
            System.out.printf("%d\t\t%s\t%s\t%s\t%s %s\t%s\t\t%s\n",
                bs.getno_transaksi(),
                bs.gettanggal(),
                bs.getjam(),
                bs.getstatusBook(),
                m.getmerk(),
                m.gettipe(),
                owner != null ? owner.getnama() : "Unknown",
                t != null ? t.getnama() : "Unknown");
            found = true;
        }
        if (!found) {
            System.out.println("Belum ada data transaksi selesai.");
        }
    }

    private static void kelolaBooking(Scanner scanner) {
        while (true) {
            System.out.println("\n=== KELOLA BOOKING ===");
            System.out.println("1. Lihat Semua Booking");
            System.out.println("2. Proses Booking");
            System.out.println("3. Selesai Booking");
            System.out.println("4. Tolak Booking");
            System.out.println("5. Kembali");
            System.out.print("Pilih menu (1-5): ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (pilihan) {
                case 1:
                    lihatSemuaBooking();
                    break;
                case 2:
                    prosesBooking(scanner);
                    break;
                case 3:
                    selesaiBooking(scanner);
                    break;
                case 4:
                    tolakBooking(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void lihatSemuaBooking() {
        System.out.println("\n=== DAFTAR BOOKING ===");
        List<booking_servis> bookings = bookingDAO.readbBooking_servis();
        if (bookings.isEmpty()) {
            System.out.println("Tidak ada data booking.");
            return;
        }

        System.out.println("ID\tNo Transaksi\tTanggal\t\tJam\t\tStatus\t\tMobil\t\tPelanggan");
        System.out.println("----------------------------------------------------------------------------------------");
        for (booking_servis bs : bookings) {
            mobil m = mobilDAO.getMobilById(bs.getidMobil());
            User owner = null;
            if (m != null) {
                owner = userDAO.getUserById(m.getfk_user());
            }
            
            String mobilInfo = (m != null) ? m.getmerk() + " " + m.gettipe() : "Unknown";
            String ownerName = (owner != null) ? owner.getnama() : "Unknown";
            
            System.out.printf("%d\t%d\t\t%s\t%s\t%s\t%s\t%s\n",
                bs.getidBooking(),
                bs.getno_transaksi(),
                bs.gettanggal(),
                bs.getjam(),
                bs.getstatusBook(),
                mobilInfo,
                ownerName);
        }
    }

    private static void prosesBooking(Scanner scanner) {
        System.out.println("\n=== PROSES BOOKING ===");
        List<booking_servis> bookings = bookingDAO.readbBooking_servis();
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
        
        System.out.println("ID\tNo Transaksi\tTanggal\t\tJam\t\tMobil\t\tPelanggan");
        System.out.println("----------------------------------------------------------------");
        for (booking_servis bs : pendingBookings) {
            mobil m = mobilDAO.getMobilById(bs.getidMobil());
            User owner = userDAO.getUserById(m.getfk_user());
            
            System.out.printf("%d\t%d\t\t%s\t%s\t%s %s\t%s\n",
                bs.getidBooking(),
                bs.getno_transaksi(),
                bs.gettanggal(),
                bs.getjam(),
                m.getmerk(),
                m.gettipe(),
                owner != null ? owner.getnama() : "Unknown");
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
        List<booking_servis> bookings = bookingDAO.readbBooking_servis();
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
        
        System.out.println("ID\tNo Transaksi\tTanggal\t\tJam\t\tMobil\t\tPelanggan");
        System.out.println("----------------------------------------------------------------");
        for (booking_servis bs : activeBookings) {
            mobil m = mobilDAO.getMobilById(bs.getidMobil());
            User owner = null;
            if (m != null) {
                owner = userDAO.getUserById(m.getfk_user());
            }
            
            String mobilInfo = (m != null) ? m.getmerk() + " " + m.gettipe() : "Unknown";
            String ownerName = (owner != null) ? owner.getnama() : "Unknown";
            
            System.out.printf("%d\t%d\t\t%s\t%s\t%s\t%s\n",
                bs.getidBooking(),
                bs.getno_transaksi(),
                bs.gettanggal(),
                bs.getjam(),
                mobilInfo,
                ownerName);
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
        List<booking_servis> bookings = bookingDAO.readbBooking_servis();
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
        
        System.out.println("ID\tTanggal\t\tJam\t\tMobil\t\tPelanggan");
        System.out.println("----------------------------------------------------------------");
        for (booking_servis bs : pendingBookings) {
            mobil m = mobilDAO.getMobilById(bs.getidMobil());
            User owner = null;
            if (m != null) {
                owner = userDAO.getUserById(m.getfk_user());
            }
            
            String mobilInfo = (m != null) ? m.getmerk() + " " + m.gettipe() : "Unknown";
            String ownerName = (owner != null) ? owner.getnama() : "Unknown";
            
            System.out.printf("%d\t%s\t%s\t%s\t%s\n",
                bs.getidBooking(),
                bs.gettanggal(),
                bs.getjam(),
                mobilInfo,
                ownerName);
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
}