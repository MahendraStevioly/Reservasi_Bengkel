import java.sql.*;

// Kelas BookingAktifView digunakan untuk menampilkan daftar booking aktif dari view v_booking_aktif
public class BookingAktifView {
    public static void tampilkanBookingAktif(Connection conn) {
        String sql = "SELECT * FROM v_booking_aktif";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("== Daftar Booking Aktif ==");
            int lastBookingId = -1;
            String lastCustomer = "";
            String lastCar = "";
            String lastTechnician = "";
            String lastDate = "";
            String lastTime = "";
            boolean hasData = false;
            while (rs.next()) {
                int bookingId = rs.getInt("id_booking");
                String customer = rs.getString("nama_pelanggan");
                String car = rs.getString("merk_mobil");
                String service = rs.getString("nama_servis");
                String technician = rs.getString("nama_teknisi");
                String date = String.valueOf(rs.getDate("tanggal_booking"));
                String time = rs.getString("jam_booking");
                if (bookingId != lastBookingId) {
                    if (lastBookingId != -1) {
                        System.out.println();
                    }
                    System.out.println("------------------------------");
                    System.out.printf("Booking ID   : %d\n", bookingId);
                    System.out.printf("Customer     : %s\n", customer);
                    System.out.printf("Car          : %s\n", car);
                    System.out.printf("Technician   : %s\n", technician);
                    System.out.printf("Date         : %s\n", date);
                    System.out.printf("Time         : %s\n", time);
                    System.out.println("Services     :");
                    hasData = true;
                }
                System.out.printf("  - %s\n", service);
                lastBookingId = bookingId;
            }
            if (!hasData) {
                System.out.println("Tidak ada booking aktif.");
            } else {
                System.out.println("------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 