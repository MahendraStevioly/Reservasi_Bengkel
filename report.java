import java.sql.*;

public class report {
    public static void tampilkanLaporanServisTerjual(Connection conn) {
        String sql = "WITH total_servis AS (\n" +
                "    SELECT id_jenis_servis, COUNT(*) AS jumlah_terjual\n" +
                "    FROM detail_servis\n" +
                "    GROUP BY id_jenis_servis\n" +
                ")\n" +
                "SELECT js.nama_servis, ts.jumlah_terjual\n" +
                "FROM total_servis ts\n" +
                "JOIN jenis_servis js ON js.id_jenis_servis = ts.id_jenis_servis;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n=== LAPORAN JUMLAH SERVIS TERJUAL ===");
            System.out.printf("%-30s | %-15s\n", "Nama Servis", "Jumlah Terjual");
            System.out.println("-------------------------------+-----------------");
            boolean hasData = false;
            while (rs.next()) {
                String namaServis = rs.getString("nama_servis");
                int jumlahTerjual = rs.getInt("jumlah_terjual");
                System.out.printf("%-30s | %-15d\n", namaServis, jumlahTerjual);
                hasData = true;
            }
            if (!hasData) {
                System.out.println("Tidak ada data servis terjual.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void tampilkanLaporanTeknisiMingguan(Connection conn) {
        String sql = "SELECT " +
                " t.nama AS nama_teknisi, " +
                " MONTHNAME(b.tanggal_booking) AS bulan, " +
                " MONTH(b.tanggal_booking) as bulan_ke, " +
                " FLOOR((DAY(b.tanggal_booking) - 1) / 7) + 1 AS minggu_ke, " +
                " COUNT(*) AS jumlah_servis " +
                "FROM teknisi t " +
                "JOIN booking_servis b ON t.id_teknisi = b.id_teknisi " +
                "WHERE YEAR(b.tanggal_booking) = YEAR(CURDATE()) " +
                "GROUP BY t.id_teknisi, MONTHNAME(b.tanggal_booking), MONTH(b.tanggal_booking), minggu_ke " +
                "ORDER BY t.nama, bulan_ke, minggu_ke;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n=== LAPORAN JUMLAH SERVIS TEKNISI PER MINGGU (TAHUN INI) ===");
            System.out.printf("%-20s | %-10s | %-8s | %-10s\n", "Nama Teknisi", "Bulan", "Minggu", "Jumlah Servis");
            System.out.println("---------------------+------------+----------+--------------");
            boolean hasData = false;
            while (rs.next()) {
                String namaTeknisi = rs.getString("nama_teknisi");
                String bulan = rs.getString("bulan");
                int minggu = rs.getInt("minggu_ke");
                int jumlahServis = rs.getInt("jumlah_servis");
                System.out.printf("%-20s | %-10s | %-8d | %-10d\n", namaTeknisi, bulan, minggu, jumlahServis);
                hasData = true;
            }
            if (!hasData) {
                System.out.println("Tidak ada data servis teknisi per minggu untuk tahun ini.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void tampilkanRingkasanStatusBooking(Connection conn) {
        String sql = "select\n" +
                "    SUM(CASE WHEN status_booking = 'aktif' THEN 1 ELSE 0 END) AS aktif,\n" +
                "    SUM(CASE WHEN status_booking = 'selesai' THEN 1 ELSE 0 END) AS selesai,\n" +
                "    SUM(CASE WHEN status_booking = 'dibatalkan' THEN 1 ELSE 0 END) AS dibatalkan,\n" +
                "    SUM(CASE WHEN status_booking = 'ditolak' THEN 1 ELSE 0 END) AS ditolak,\n" +
                "    SUM(CASE WHEN status_booking = 'menunggu' THEN 1 ELSE 0 END) AS menunggu,\n" +
                "    count(*)as total_servis\n" +
                "FROM booking_servis;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n=== RINGKASAN STATUS BOOKING SERVIS ===");
            if (rs.next()) {
                System.out.printf("Aktif      : %d\n", rs.getInt("aktif"));
                System.out.printf("Selesai    : %d\n", rs.getInt("selesai"));
                System.out.printf("Dibatalkan : %d\n", rs.getInt("dibatalkan"));
                System.out.printf("Ditolak    : %d\n", rs.getInt("ditolak"));
                System.out.printf("Menunggu   : %d\n", rs.getInt("menunggu"));
                System.out.printf("--------------------------\n");
                System.out.printf("Total Servis: %d\n", rs.getInt("total_servis"));
            } else {
                System.out.println("Tidak ada data booking servis.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 